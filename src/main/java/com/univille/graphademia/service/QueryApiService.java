package com.univille.graphademia.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.univille.graphademia.node.Referencia;

public class QueryApiService {
    
    public static String procurarObraPorTitulo(String titulo) {
        String urlBase = "https://api.semanticscholar.org/graph/v1/paper/search/match?query=";
        int maxRetries = 10;
        int retryCount = 0;
        int tempoDeEspera = 5000;
    
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
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
    
                    Gson gson = new Gson();
                    RespostaApiService apiResponse = gson.fromJson(response.toString(), RespostaApiService.class);
    
                    if (apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
                        String paperId = apiResponse.getData().get(0).getPaperId();
                        System.out.println("Paper ID: " + paperId);
                        return paperId;
                    } else {
                        System.out.println("Nenhum resultado encontrado.");
                    }
                } else if (responseCode == 429) {
                    System.out.println("429: Too many requests. Waiting...");
                    retryCount++;
                    String retryAfter = connection.getHeaderField("Retry-After");
    
                    if (retryAfter != null) {
                        tempoDeEspera = Integer.parseInt(retryAfter) * 1000;
                    } else {
                        tempoDeEspera *= 2;
                    }
                    Thread.sleep(tempoDeEspera);
                } else {
                    System.out.println("GET failed... Response Code: " + responseCode);
                    break;
                }
            } catch (JsonSyntaxException e) {
                System.out.println("Error parsing JSON: " + e.getMessage());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    
        System.out.println("Retries reached maximum. Returning null.");
        return null;
    }
    
    public static List<Referencia> procurarDetalhesObra(String id) {
        String urlString = "https://api.semanticscholar.org/graph/v1/paper/" + id + "?fields=title,authors,references,openAccessPdf,externalIds,publicationVenue,year,publicationDate,publicationTypes,tldr,citationCount";
        int maxRetries = 10;
        int retryCount = 0;
        int tempoDeEspera = 5000;
    
        while (retryCount < maxRetries) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
    
                int responseCode = connection.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    
                    Gson gson = new Gson();
                    JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
    
                    if (jsonResponse.has("references")) {
                        List<Referencia> referencesList = new ArrayList<>();
                        for (var reference : jsonResponse.getAsJsonArray("references")) {
                            JsonObject refObject = reference.getAsJsonObject();
                            
                            String paperId = refObject.has("paperId") && !refObject.get("paperId").isJsonNull() 
                                            ? refObject.get("paperId").getAsString() 
                                            : "Null";
    
                            String title = refObject.get("title").getAsString();
                            
                            referencesList.add(new Referencia(paperId, title));
                        }
                        return referencesList;
                    } else {
                        System.out.println("Nenhuma referência encontrada.");
                        return Collections.emptyList();
                    }
                } else if (responseCode == 429) {
                    System.out.println("429: Too many requests. Espere um pouco...");
                    retryCount++;
                    String retryAfter = connection.getHeaderField("Retry-After");
                    
                    if (retryAfter != null) {
                        tempoDeEspera = Integer.parseInt(retryAfter) * 1000;
                    } else {
                        tempoDeEspera *= 2;
                    }
                    Thread.sleep(tempoDeEspera);
                } else {
                    System.out.println("GET falhou... Código: " + responseCode);
                    break;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    
        System.out.println("Retries chegaram ao máximo. Retornando null.");
        return null;
    }
    
    public static String procurarAutorPorNome(String nome) {
        String urlBase = "https://api.semanticscholar.org/graph/v1/author/search?query=";
        int maxRetries = 10;
        int retryCount = 0;
        int tempoDeEspera = 5000;
    
        while (retryCount < maxRetries) {
            try {
                String urlString = urlBase + URLEncoder.encode(nome, StandardCharsets.UTF_8.toString());
                URL url = new URL(urlString);
    
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
    
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
    
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
    
                    System.out.println("Resposta: " + response.toString());
                    return response.toString();
                } else if (responseCode == 429) {
                    System.out.println("429: Too many requests. Espere um pouco...");
                    retryCount++;
                    String retryAfter = connection.getHeaderField("Retry-After");
                    
                    if (retryAfter != null) {
                        tempoDeEspera = Integer.parseInt(retryAfter) * 1000;
                    } else {
                        tempoDeEspera *= 2;
                    }
                    Thread.sleep(tempoDeEspera);
                } else {
                    System.out.println("GET falhou... Response Code: " + responseCode);
                    break;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    
        System.out.println("Retries chegaram ao máximo. Retornando null.");
        return null;
    }    

}
