package com.univille.graphademia.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class SemanticScholarAPI {
    
    public static String procurarObraPorTitulo(String titulo) {

        String urlBase = "https://api.semanticscholar.org/graph/v1/paper/search/match?query=";
        
        try {
            // Construir URL da API com titulo encodado
            String encodedNome = URLEncoder.encode(titulo, StandardCharsets.UTF_8.toString());
            String urlString = urlBase + encodedNome;
            URL url = new URL(urlString);

            // Abrir conexão, fazer request
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { 
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                // Ler response por linha
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Converter o JSON para objetos Java
                Gson gson = new Gson();
                RespostaAPI apiResponse = gson.fromJson(response.toString(), RespostaAPI.class);

                // Se houver dados, retornar o paperId do primeiro Paper
                if (apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
                    String paperId = apiResponse.getData().get(0).getPaperId();
                    System.out.println("Paper ID: " + paperId);
                    return paperId;
                } else {
                    System.out.println("Nenhum resultado encontrado.");
                }
            } else {
                System.out.println("GET falhou... Response Code: " + responseCode);
            }
        } catch (JsonSyntaxException e) {
            System.out.println("Erro ao analisar o JSON: " + e.getMessage());
        } catch (IOException e) {}
        return null;
    }

    public static String procurarReferenciasDaObra(String id) {

        String urlString = "https://api.semanticscholar.org/graph/v1/paper/"+id+"/references";
        
            try {
                URL url = new URL(urlString);
    
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
    
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
                } else {
                    System.out.println("GET falhou... Response Code: " + responseCode);
                }
            } catch (IOException e) {}
            return null;
        }

    public static String procurarAutorPorNome(String nome) {

    String urlBase = "https://api.semanticscholar.org/graph/v1/author/search?query=";
    
        try {
            String urlString = urlBase + nome.replace(" ", "%20"); // Encodar URL
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

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
            } else {
                System.out.println("GET falhou... Response Code: " + responseCode);
            }
        } catch (IOException e) {}
        return null;
    }

}
