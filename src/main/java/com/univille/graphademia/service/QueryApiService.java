package com.univille.graphademia.service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.univille.graphademia.dto.Referencia;
import com.univille.graphademia.node.Autor;
import com.univille.graphademia.node.Obra;

@Service
public class QueryApiService {

    public static int maxRetries = 10;
    public static int retryCount = 0;
    public static int tempoDeEspera = 5000;
    public static String msgMaxRetries = "Retries chegaram ao máximo. Retornando null.";
    public static String msg429 = "429: Too many requests. Espere um pouco...";
    public static String msgSemResult = "Nenhum resultado encontrado.";
    public static String msgGetFalhou = "GET falhou... Código: "; 

    public static String procurarObraPorTitulo(String titulo) {
        String urlBase = "https://api.semanticscholar.org/graph/v1/paper/search/match?query=";
        
        OUTER:
        while (retryCount < maxRetries) {
            try {
                String encodedNome = URLEncoder.encode(titulo, StandardCharsets.UTF_8.toString());
                String urlString = urlBase + encodedNome;
                @SuppressWarnings("deprecation")
                URL url = new URL(urlString);
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
                        }

                        in.close();

                        Gson gson = new Gson();
                        RespostaApiService resposta = gson.fromJson(response.toString(), RespostaApiService.class);
                        if (resposta.getData() != null && !resposta.getData().isEmpty()) {
                            String paperId = resposta.getData().get(0).toString();
                            System.out.println("Paper ID: " + paperId);
                            return paperId;
                        } else {
                            System.out.println(msgSemResult);
                        }
                    }
                    case 429 -> {
                        System.out.println(msg429);
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
                        System.out.println(msgGetFalhou + responseCode);
                        break OUTER;
                    }
                }
            } catch (JsonSyntaxException e) {
                System.out.println("Erro no durante parsing do JSON: " + e.getMessage());
            } catch (IOException | InterruptedException e) {
            }
        }
        System.out.println(msgMaxRetries);
        return null;
    }

    public static Obra procurarDetalhesObra(String id) {
        String urlString = "https://api.semanticscholar.org/graph/v1/paper/" + id + "?fields=title,authors,references,openAccessPdf,externalIds,year,publicationDate,publicationVenue,publicationTypes,tldr,citationCount";
        
        OUTER:
        while (retryCount < maxRetries) {
            try {
                @SuppressWarnings("deprecation")
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
                        StringBuilder response = new StringBuilder();

                        String inputLine;
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

                        if (jsonResponse.getAsJsonObject("externalIds") != null && jsonResponse.getAsJsonObject("externalIds").has("DOI")) {
                            obra.setDoi(jsonResponse.getAsJsonObject("externalIds").get("DOI").getAsString());
                        }

                        if (jsonResponse.has("publicationVenue") && jsonResponse.get("publicationVenue").isJsonObject()) {
                            JsonObject publicationVenue = jsonResponse.getAsJsonObject("publicationVenue");
                            obra.setPublicationVenueName(publicationVenue.has("name") ? publicationVenue.get("name").getAsString() : null);
                            obra.setPublicationVenueType(publicationVenue.has("type") ? publicationVenue.get("type").getAsString() : null);
                        }

                        if (jsonResponse.getAsJsonObject("openAccessPdf") != null && jsonResponse.getAsJsonObject("openAccessPdf").has("url")) {
                            obra.setUrl(jsonResponse.getAsJsonObject("openAccessPdf").get("url").getAsString());
                        }

                        if (jsonResponse.getAsJsonObject("tldr") != null && jsonResponse.getAsJsonObject("tldr").has("text")) {
                            obra.setTldr(jsonResponse.getAsJsonObject("tldr").get("text").getAsString());
                        }

                        if (jsonResponse.getAsJsonObject("publicationTypes") != null) {
                            JsonElement publicationTypesElement = jsonResponse.get("publicationTypes");
                            if (publicationTypesElement.isJsonArray()) {
                                StringBuilder typesBuilder = new StringBuilder();
                                for (JsonElement typeElement : publicationTypesElement.getAsJsonArray()) {
                                    typesBuilder.append(typeElement.getAsString()).append(", ");
                                }
                                obra.setPublicationTypes(typesBuilder.length() > 0 ? typesBuilder.substring(0, typesBuilder.length() - 2) : null);
                            }
                        }

                        if (jsonResponse.get("authors") != null && jsonResponse.get("authors").isJsonArray()) {
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

                        if (jsonResponse.get("references") != null && jsonResponse.get("references").isJsonArray()) {
                            List<Referencia> listaReferencias = new ArrayList<>();
                            for (JsonElement reference : jsonResponse.getAsJsonArray("references")) {
                                JsonObject refObject = reference.getAsJsonObject();
                                String refPaperId = refObject.has("paperId") && !refObject.get("paperId").isJsonNull()
                                        ? refObject.get("paperId").getAsString()
                                        : null;
                                String refTitle = refObject.has("title") ? refObject.get("title").getAsString() : null;
                                listaReferencias.add(new Referencia(refPaperId, refTitle));
                            }
                            obra.setReferencias(listaReferencias);
                            obra.gerarObrasAPartirDeReferencia(listaReferencias);
                        }

                        return obra;
                    }
                    case 429 -> {
                        System.out.println(msg429);
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
                        System.out.println(msgGetFalhou + responseCode);
                        break OUTER;
                    }
                }
            } catch (IOException | InterruptedException e) {
            }
        }
        System.out.println(msgMaxRetries);
        return null;
    }

    public static List<Obra> procurarDetalhesMultiplasObras(List<Obra> listaObras) {
        List<String> listaIds = new ArrayList<>();
        Map<String, Obra> obraMap = new HashMap<>();

        // Popular mapa com ids e obras
        for (Obra obra : listaObras) {
            String id = obra.getPaperId();
            listaIds.add(id);
            obraMap.put(id, obra);
        }

        OUTER:
        while (retryCount < maxRetries) {
            try {
                
                Thread.sleep(tempoDeEspera);

                @SuppressWarnings("deprecation")
                URL url = new URL("https://api.semanticscholar.org/graph/v1/paper/batch?fields=title,authors,references,openAccessPdf,externalIds,year,publicationDate,publicationVenue,publicationTypes,tldr,citationCount");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Passar listaIds para payload json
                JsonArray idsArray = new JsonArray();
                for (String id : listaIds) {
                    idsArray.add(id);
                }

                JsonObject jsonBody = new JsonObject();
                jsonBody.add("ids", idsArray);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonBody.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();

                switch (responseCode) {
                    case HttpURLConnection.HTTP_OK -> {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder resposta = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            resposta.append(inputLine);
                        }
                        in.close();

                        Gson gson = new Gson();
                        JsonArray jsonResponseArray = gson.fromJson(resposta.toString(), JsonArray.class);                            

                        // Iterar sobre os ids e atualizar as obras pela resposta
                        for (int i = 0; i < listaIds.size(); i++) {
                            JsonElement jsonElement = jsonResponseArray.get(i);
                            JsonObject jsonResponse = jsonElement.getAsJsonObject();

                            // Pegar objeto obra do índice
                            Obra obra = obraMap.get(listaIds.get(i));

                            if (obra != null) {

                                obra.setTitle(jsonResponse.has("title") ? jsonResponse.get("title").getAsString() : null);
                                obra.setYear(jsonResponse.has("year") ? jsonResponse.get("year").getAsInt() : null);

                                if (jsonResponse.has("externalIds") && jsonResponse.get("externalIds").isJsonObject()
                                && jsonResponse.getAsJsonObject("externalIds").has("DOI")) {
                                    obra.setDoi(jsonResponse.getAsJsonObject("externalIds").get("DOI").getAsString());
                                }

                                if (jsonResponse.has("publicationVenue") && jsonResponse.get("publicationVenue").isJsonObject()) {
                                    JsonObject publicationVenue = jsonResponse.getAsJsonObject("publicationVenue");
                                    obra.setPublicationVenueName(publicationVenue.has("name") ? publicationVenue.get("name").getAsString() : null);
                                    obra.setPublicationVenueType(publicationVenue.has("type") ? publicationVenue.get("type").getAsString() : null);
                                }

                                if (jsonResponse.has("openAccessPdf") && jsonResponse.get("openAccessPdf").isJsonObject()) {
                                    JsonObject openAccessPdf = jsonResponse.getAsJsonObject("openAccessPdf");
                                    if (openAccessPdf.has("url")) {
                                        obra.setUrl(openAccessPdf.get("url").getAsString());
                                    }
                                }

                                if (jsonResponse.getAsJsonObject("tldr") != null && jsonResponse.getAsJsonObject("tldr").has("text")) {
                                    obra.setTldr(jsonResponse.getAsJsonObject("tldr").get("text").getAsString());
                                }

                                if (jsonResponse.has("publicationTypes") && jsonResponse.get("publicationTypes").isJsonObject()) {
                                    JsonArray publicationTypes = jsonResponse.getAsJsonArray("publicationTypes");
                                    List<String> types = new ArrayList<>();
                                    for (JsonElement typeElement : publicationTypes) {
                                        types.add(typeElement.getAsString());
                                    }
                                    obra.setPublicationTypes(String.join(", ", types));
                                }

                                if (jsonResponse.get("authors") != null && jsonResponse.get("authors").isJsonArray()) {
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

                                if (jsonResponse.get("references") != null && jsonResponse.get("references").isJsonArray()) {
                                    List<Referencia> listaReferencias = new ArrayList<>();
                                    
                                    for (JsonElement reference : jsonResponse.getAsJsonArray("references")) {
                                        JsonObject refObject = reference.getAsJsonObject();
                                        String refPaperId = refObject.has("paperId") && !refObject.get("paperId").isJsonNull()
                                                ? refObject.get("paperId").getAsString()
                                                : null;
                                        String refTitle = refObject.has("title") ? refObject.get("title").getAsString() : null;
                                        listaReferencias.add(new Referencia(refPaperId, refTitle));
                                    }
                                    obra.setReferencias(listaReferencias);
                                    obra.gerarObrasAPartirDeReferencia(listaReferencias);
                                }
                            }
                        }
                        return listaObras;
                    }
                    case 429 -> {
                        System.out.println(msg429);
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
                        System.out.println(msgGetFalhou + responseCode);
                        break OUTER;
                    }
                }
            } catch (IOException | InterruptedException e) {
            }
        }
        System.out.println(msgMaxRetries);
        return null;
        }


    public static List<Autor> procurarAutorPorNome(String nome) {
        String urlBase = "https://api.semanticscholar.org/graph/v1/author/search?query=";

        OUTER:
        while (retryCount < maxRetries) {
            try {
                String encodedNome = URLEncoder.encode(nome, StandardCharsets.UTF_8.toString());
                String urlString = urlBase + encodedNome;
                @SuppressWarnings("deprecation")
                URL url = new URL(urlString);
                System.out.println("Request URL: " + urlString);

                Thread.sleep(tempoDeEspera);

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
                        }
                        in.close();

                        Gson gson = new Gson();
                        RespostaApiService resposta = gson.fromJson(response.toString(), RespostaApiService.class);
                        List<Autor> listaAutores = new ArrayList<>();

                        if (resposta.getData() != null) {
                            for (Object obj : resposta.getData()) {
                                Autor autor = (Autor) obj;
                                listaAutores.add(autor);
                            }
                            return listaAutores;
                        } else {
                            System.out.println(msgSemResult);
                        }
                    }
                    case 429 -> {
                        System.out.println(msg429);
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
                        System.out.println(msgGetFalhou + responseCode);
                        break OUTER;
                    }
                }
            } catch (IOException | InterruptedException e) {
            }
        }
        System.out.println(msgMaxRetries);
        return null;
    }
}
