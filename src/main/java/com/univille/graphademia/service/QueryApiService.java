package com.univille.graphademia.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.univille.graphademia.node.Autor;
import com.univille.graphademia.node.Obra;
import com.univille.graphademia.node.Referencia;

public class QueryApiService {
    
    public static String procurarObraPorTitulo(String titulo) {
        String urlBase = "https://api.semanticscholar.org/graph/v1/paper/search/match?query=";
        int maxRetries = 10;
        int retryCount = 0;
        int tempoDeEspera = 5000;
    
        OUTER:
        while (retryCount < maxRetries) {
            try {
                String encodedNome = URLEncoder.encode(titulo, StandardCharsets.UTF_8.toString());
                String urlString = urlBase + encodedNome;
                URL url = new URL(urlString);
                System.out.println("Request URL: " + urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                int responseCode = connection.getResponseCode();
                switch (responseCode) {
                    case HttpURLConnection.HTTP_OK -> {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }   in.close();
                        Gson gson = new Gson();
                        RespostaApiService apiResponse = gson.fromJson(response.toString(), RespostaApiService.class);
                        if (apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
                            String paperId = apiResponse.getData().get(0).getPaperId();
                            System.out.println("Paper ID: " + paperId);
                            return paperId;
                        } else {
                            System.out.println("Nenhum resultado encontrado.");
                        }
                    }
                    case 429 -> {
                        System.out.println("429: Too many requests. Waiting...");
                        retryCount++;
                        String retryAfter = connection.getHeaderField("Retry-After");
                        if (retryAfter != null) {
                            tempoDeEspera = Integer.parseInt(retryAfter) * 1000;
                        } else {
                            tempoDeEspera *= 2;
                        }   Thread.sleep(tempoDeEspera);
                    }
                    default -> {
                        System.out.println("GET failed... Response Code: " + responseCode);
                        break OUTER;
                    }
                }
            }catch (JsonSyntaxException e) {
                System.out.println("Erro no durante parsing do JSON: " + e.getMessage());
            }catch (IOException | InterruptedException e) {
            }
        }
    
        System.out.println("Retries reached maximum. Returning null.");
        return null;
    }
    public static Obra procurarDetalhesObra(String id) {
        String urlString = "https://api.semanticscholar.org/graph/v1/paper/" + id + "?fields=title,autores,references,openAccessPdf,externalIds,year,publicationDate,publicationVenue,publicationTypes,tldr,citationCount";
        int maxRetries = 10;
        int retryCount = 0;
        int tempoDeEspera = 5000;
        
        OUTER:
        while (retryCount < maxRetries) {
            try {
                URL url = new URL(urlString);
                Thread.sleep(tempoDeEspera);
    
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                int responseCode = connection.getResponseCode();
                switch (responseCode) {
                    case HttpURLConnection.HTTP_OK -> {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        
                        Gson gson = new Gson();
                        JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
    
                        Obra obra = new Obra();
    
                        obra.setPaperId(id);
                        obra.setTitle(jsonResponse.has("title") ? jsonResponse.get("title").getAsString() : null);
                        obra.setYear(jsonResponse.has("year") ? jsonResponse.get("year").getAsInt() : null);
    
                        if (jsonResponse.has("externalIds") && jsonResponse.getAsJsonObject("externalIds").has("DOI")) {
                            obra.setDoi(jsonResponse.getAsJsonObject("externalIds").get("DOI").getAsString());
                        }
    
                        if (jsonResponse.has("publicationVenue")) {
                            JsonObject publicationVenue = jsonResponse.getAsJsonObject("publicationVenue");
                            obra.setPublicationVenueName(publicationVenue.has("name") ? publicationVenue.get("name").getAsString() : null);
                            obra.setPublicationVenueType(publicationVenue.has("type") ? publicationVenue.get("type").getAsString() : null);
                        }
    
                        if (jsonResponse.has("openAccessPdf") && jsonResponse.getAsJsonObject("openAccessPdf").has("url")) {
                            obra.setUrl(jsonResponse.getAsJsonObject("openAccessPdf").get("url").getAsString());
                        }
    
                        if (jsonResponse.has("tldr") && jsonResponse.getAsJsonObject("tldr").has("text")) {
                            obra.setTldr(jsonResponse.getAsJsonObject("tldr").get("text").getAsString());
                        }
    
                        if (jsonResponse.has("publicationTypes")) {
                            JsonElement publicationTypesElement = jsonResponse.get("publicationTypes");
                            if (publicationTypesElement.isJsonArray()) {
                                StringBuilder typesBuilder = new StringBuilder();
                                for (JsonElement typeElement : publicationTypesElement.getAsJsonArray()) {
                                    typesBuilder.append(typeElement.getAsString()).append(", ");
                                }
                                obra.setPublicationTypes(typesBuilder.length() > 0 ? typesBuilder.substring(0, typesBuilder.length() - 2) : null);
                            }
                        }
    
                        if (jsonResponse.has("authors") && jsonResponse.get("authors").isJsonArray()) {
                            List<Autor> autores = new ArrayList<>();
                            for (JsonElement authorElement : jsonResponse.getAsJsonArray("authors")) {
                                JsonObject authorObj = authorElement.getAsJsonObject();
                                Autor author = new Autor(
                                    authorObj.has("authorId") ? authorObj.get("authorId").getAsString() : null,
                                    authorObj.has("name") ? authorObj.get("name").getAsString() : null
                                );
                                autores.add(author);
                            }
                            obra.setAuthors(autores);
                        }
    
                        if (jsonResponse.has("references") && jsonResponse.get("references").isJsonArray()) {
                            List<Referencia> referencesList = new ArrayList<>();
                            for (JsonElement reference : jsonResponse.getAsJsonArray("references")) {
                                JsonObject refObject = reference.getAsJsonObject();
                                String refPaperId = refObject.has("paperId") && !refObject.get("paperId").isJsonNull()
                                        ? refObject.get("paperId").getAsString()
                                        : "Null";
                                String refTitle = refObject.has("title") ? refObject.get("title").getAsString() : null;
                                referencesList.add(new Referencia(refPaperId, refTitle));
                            }
                            obra.setReferences(referencesList);
                        }
    
                        return obra;
                    }
                    case 429 -> {
                        System.out.println("429: Too many requests. Espere um pouco...");
                        retryCount++;
                        String retryAfter = connection.getHeaderField("Retry-After");
                        if (retryAfter != null) {
                            tempoDeEspera = Integer.parseInt(retryAfter) * 1000;
                        } else {
                            tempoDeEspera *= 2;
                        }
                        Thread.sleep(tempoDeEspera);
                    }
                    default -> {
                        System.out.println("GET falhou... Código: " + responseCode);
                        break OUTER;
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    
        System.out.println("Retries chegaram ao máximo. Retornando null.");
        return null;
    }
    

    public static List<Autor> procurarAutorPorNome(String nome) {
        String urlBase = "https://api.semanticscholar.org/graph/v1/author/search?query=";
        int maxRetries = 10;
        int retryCount = 0;
        int tempoDeEspera = 5000;
    
        OUTER:
        while (retryCount < maxRetries) {
            try {
                String urlString = urlBase + URLEncoder.encode(nome, StandardCharsets.UTF_8.toString());
                URL url = new URL(urlString);
                Thread.sleep(tempoDeEspera);
                
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                int responseCode = connection.getResponseCode();
                switch (responseCode) {
                    case HttpURLConnection.HTTP_OK -> {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        Gson gson = new Gson();
                        JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);

                        if (jsonResponse.has("data") && jsonResponse.get("data").isJsonArray()) {
                            List<Autor> autores = new ArrayList<>();
                            for (JsonElement authorElement : jsonResponse.getAsJsonArray("authors")) {
                                JsonObject authorObj = authorElement.getAsJsonObject();
                                Autor author = new Autor(
                                    authorObj.has("authorId") ? authorObj.get("authorId").getAsString() : null,
                                    authorObj.has("name") ? authorObj.get("name").getAsString() : null
                                );
                                autores.add(author);
                            }
                            return autores;
                        }
                    }
                    case 429 -> {
                        System.out.println("429: Too many requests. Espere um pouco...");
                        retryCount++;
                        String retryAfter = connection.getHeaderField("Retry-After");
                        if (retryAfter != null) {
                            tempoDeEspera = Integer.parseInt(retryAfter) * 1000;
                        } else {
                            tempoDeEspera *= 2;
                        }   Thread.sleep(tempoDeEspera);
                    }
                    default -> {
                        System.out.println("GET falhou... Response Code: " + responseCode);
                        break OUTER;
                    }
                }
            }catch (IOException | InterruptedException e) {
            }
        }
    
        System.out.println("Retries chegaram ao máximo. Retornando null.");
        return null;
    }    

}
