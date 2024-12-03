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
import com.google.gson.JsonPrimitive;
import com.univille.graphademia.dto.Referencia;
import com.univille.graphademia.node.Area;
import com.univille.graphademia.node.Autor;
import com.univille.graphademia.node.Obra;

@Service
public class SemanticScholarAPIService {

    public static int maxRetries = 10;
    public static int retryCount = 0;
    public static int tempoDeEspera = 5000;
    public static String msgMaxRetries = "Retries chegaram ao máximo. Retornando null.";
    public static String sufixoCamposObra = "?fields=title,authors,references,openAccessPdf,externalIds,year,publicationDate,publicationVenue,publicationTypes,tldr,citationCount,fieldsOfStudy";
    public static String sufixoCamposObraRecomendada = "?fields=title,authors,openAccessPdf,externalIds,year,publicationDate,publicationVenue,publicationTypes,citationCount,fieldsOfStudy";    
    public static String sufixoCamposAutor = "?fields=name,papers,externalIds,hIndex";
    
    private static JsonElement buscarRespostaJson(String urlString, String metodo, String payload) throws IOException {
        @SuppressWarnings("deprecation")
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod(metodo);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
    
        // Se for POST, mandar payload
        if ("POST".equals(metodo)) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }
    
        int responseCode = connection.getResponseCode();
        switch (responseCode) {
            case HttpURLConnection.HTTP_OK -> {
                StringBuilder response;
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }

                Gson gson = new Gson();
                JsonElement jsonElement = gson.fromJson(response.toString(), JsonElement.class);

                if (jsonElement.isJsonArray()) {
                    return jsonElement.getAsJsonArray();
                } else if (jsonElement.isJsonObject()) {
                    return jsonElement.getAsJsonObject();
                } else {
                    throw new IOException("Resposta inesperada da API: " + response.toString());
                }

            }
            case 429 -> {
                String retryAfter = connection.getHeaderField("Retry-After");
                tempoDeEspera = retryAfter != null ? Integer.parseInt(retryAfter) * 1000 : tempoDeEspera * 2;
                throw new IOException("Requests demais... Retry-After: " + tempoDeEspera + " ms");
            }
            default -> throw new IOException("Erro HTTP: " + responseCode);
        }
    };
    
    private static String parsearStringJson(JsonElement jsonElement, String fieldName) {

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(fieldName) && !jsonObject.get(fieldName).isJsonNull()) {
                return jsonObject.get(fieldName).getAsString();
            }
        } else if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
            if (jsonPrimitive.isString()) {
                return jsonPrimitive.getAsString();
            }
        }
        return null;

    };

    private static Integer parsearIntJson(JsonElement jsonElement, String fieldName) {

        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(fieldName) && !jsonObject.get(fieldName).isJsonNull()) {
                return jsonObject.get(fieldName).getAsInt();
            }
        } else if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
            if (jsonPrimitive.isNumber()) {
                return jsonPrimitive.getAsInt();
            }
        }
        return null;

    };

    private static Obra popularCamposObra(Obra obra, JsonObject respostaJson) {
        obra.setPaperId(parsearStringJson(respostaJson, "paperId"));
        obra.setTitle(parsearStringJson(respostaJson, "title"));
        obra.setYear(parsearIntJson(respostaJson, "year"));
        
        JsonObject externalIds = respostaJson.has("externalIds") && respostaJson.get("externalIds").isJsonObject() 
                ? respostaJson.getAsJsonObject("externalIds") 
                : null;
        if (externalIds != null) {
            obra.setDoi(parsearStringJson(externalIds, "DOI"));
        }
        
        JsonObject publicationVenue = respostaJson.has("publicationVenue") && respostaJson.get("publicationVenue").isJsonObject()
                ? respostaJson.getAsJsonObject("publicationVenue") 
                : null;
        if (publicationVenue != null) {
            obra.setPublicationVenueName(parsearStringJson(publicationVenue, "name"));
            obra.setPublicationVenueType(parsearStringJson(publicationVenue, "type"));
        }
        
        JsonObject openAccessPdf = respostaJson.has("openAccessPdf") && respostaJson.get("openAccessPdf").isJsonObject()
                ? respostaJson.getAsJsonObject("openAccessPdf")
                : null;
        if (openAccessPdf != null) {
            obra.setUrl(parsearStringJson(openAccessPdf, "url"));
        }
        
        JsonObject tldr = respostaJson.has("tldr") && respostaJson.get("tldr").isJsonObject() 
                ? respostaJson.getAsJsonObject("tldr") 
                : null;
        if (tldr != null) {
            obra.setTldr(parsearStringJson(tldr, "text"));
        }
        
        return obra;
    };
    
    public static Obra deserializarJsonObra(Obra obra, JsonObject respostaJson) {
        popularCamposObra(obra, respostaJson);
        
        if (respostaJson.has("publicationTypes") && respostaJson.get("publicationTypes").isJsonArray()) {
            StringBuilder typesBuilder = new StringBuilder();
            for (JsonElement typeElement : respostaJson.getAsJsonArray("publicationTypes")) {
                typesBuilder.append(typeElement.getAsString()).append(", ");
            }
            obra.setPublicationTypes(typesBuilder.length() > 0 ? typesBuilder.substring(0, typesBuilder.length() - 2) : null);
        }
        
        if (respostaJson.has("authors") && respostaJson.get("authors").isJsonArray()) {
            List<Autor> autores = new ArrayList<>();
            for (JsonElement authorElement : respostaJson.getAsJsonArray("authors")) {
                JsonObject authorObj = authorElement.getAsJsonObject();
                Autor author = new Autor(
                    parsearStringJson(authorObj, "authorId"),
                    parsearStringJson(authorObj, "name")
                );
                autores.add(author);
            }
            obra.setAuthors(autores);
        }
        
        if (respostaJson.has("references") && respostaJson.get("references").isJsonArray()) {
            List<Referencia> listaReferencias = new ArrayList<>();
            for (JsonElement reference : respostaJson.getAsJsonArray("references")) {
                JsonObject refObject = reference.getAsJsonObject();
                listaReferencias.add(new Referencia(
                    parsearStringJson(refObject, "paperId"),
                    parsearStringJson(refObject, "title")
                ));
            }
            obra.setReferencias(listaReferencias);
        }
    
        if (respostaJson.has("fieldsOfStudy") && respostaJson.get("fieldsOfStudy").isJsonArray()) {
            List<Area> areas = new ArrayList<>();
            for (JsonElement reference : respostaJson.getAsJsonArray("fieldsOfStudy")) {
                String area = reference.getAsString();
                areas.add(new Area(area));
            }
            obra.setAreas(areas);
        }
    
        return obra;
    };
    
    public static Obra gerarObraPorTitulo(String titulo) {
        String urlBase = "https://api.semanticscholar.org/graph/v1/paper/search/match?query=";

        while (retryCount < maxRetries) {
            try {
                String encodedNome = URLEncoder.encode(titulo, StandardCharsets.UTF_8.toString());
                JsonObject respostaJson = buscarRespostaJson(urlBase + encodedNome + sufixoCamposObra, "GET", null).getAsJsonObject();
                Obra obra = deserializarJsonObra(new Obra(), respostaJson);
                obra.gerarObrasDeObras(obra.getReferencias());
                return obra;
            } catch (IOException e) {
                System.out.println(e.getMessage());
                retryCount++;
            }
        }
        System.out.println(msgMaxRetries);
        return null;
    };

    public static Obra gerarObraPorId(String id) {
        String urlBase = "https://api.semanticscholar.org/graph/v1/paper/";     

        while (retryCount < maxRetries) {
            try {
                JsonObject respostaJson = buscarRespostaJson(urlBase + id + sufixoCamposObra, "GET", null).getAsJsonObject();
                Obra obra = deserializarJsonObra(new Obra(), respostaJson);
                obra.gerarObrasDeObras(obra.getReferencias());
                return obra;
            } catch (IOException e) {
                System.out.println(e.getMessage());
                retryCount++;
            }
        }
        System.out.println(msgMaxRetries);
        return null;
    };

    public static List<Obra> gerarDetalhesMultiplasObras(List<Obra> listaObras) {
        String urlBase = "https://api.semanticscholar.org/graph/v1/paper/batch";        
    
        List<String> listaIds = new ArrayList<>();
        Map<String, Obra> obraMap = new HashMap<>();
        
        // Popular mapa com ids e obras
        for (Obra obra : listaObras) {
            String id = obra.getPaperId();
            listaIds.add(id);
            obraMap.put(id, obra);
        }
    
        // Criar payload
        JsonArray idsArray = new JsonArray();
        for (String id : listaIds) {
            idsArray.add(id);
        }
    
        JsonObject payload = new JsonObject();
        payload.add("ids", idsArray);
    
        while (retryCount < maxRetries) {
            try {
                JsonElement jsonResponseElement = buscarRespostaJson(urlBase + sufixoCamposObra, "POST", payload.toString());
                JsonArray jsonResponseArray = jsonResponseElement.getAsJsonArray();
            
                List<Obra> obras = new ArrayList<>();
            
                for (int i = 0; i < listaIds.size(); i++) {
                    JsonObject jsonObraResponse = jsonResponseArray.get(i).getAsJsonObject();
                    Obra obra = obraMap.get(listaIds.get(i));
            
                    if (obra != null) {
                        deserializarJsonObra(obra, jsonObraResponse);
                        obra.gerarObrasDeObras(obra.getReferencias());
                        obras.add(obra);
                    }
                }
            
                return obras;
            
            } catch (IOException e) {
                System.out.println(e.getMessage());
                retryCount++;
            }            
            
        }
        System.out.println(msgMaxRetries);
        return null;
    };

    private static Autor popularCamposAutor(JsonObject jsonObject) {
        Autor autor = new Autor();
    
        autor.setAuthorId(parsearStringJson(jsonObject, "authorId"));
        autor.setName(parsearStringJson(jsonObject, "name"));
        autor.setHindex(parsearIntJson(jsonObject, "hIndex"));
    
        JsonObject externalIds = jsonObject.has("externalIds") && !jsonObject.get("externalIds").isJsonNull() 
                ? jsonObject.getAsJsonObject("externalIds") 
                : null;
        if (externalIds != null) {
            autor.setDblp(parsearStringJson(externalIds, "dblp"));
            autor.setOrcid(parsearStringJson(externalIds, "orcid"));
        }
        return autor;
    };

    public static List<Autor> deserializarJsonAutores(JsonObject respostaJson) {
        List<Autor> autores = new ArrayList<>();
    
        if (respostaJson.has("data") && !respostaJson.get("data").isJsonNull()) {
            for (int i = 0; i < respostaJson.get("data").getAsJsonArray().size(); i++) {
                Autor autor = popularCamposAutor(respostaJson.get("data").getAsJsonArray().get(i).getAsJsonObject());
                autores.add(autor);
            }
        }
        return autores;
    };
    
    public static List<Autor> gerarAutorPorNome(String nome) {
        String urlBase = "https://api.semanticscholar.org/graph/v1/author/search?query=";

        while (retryCount < maxRetries) {
            try {
                String encodedNome = URLEncoder.encode(nome, StandardCharsets.UTF_8.toString());
                JsonObject respostaJson = buscarRespostaJson(urlBase + encodedNome + sufixoCamposAutor, "GET", null).getAsJsonObject();
                List<Autor> autores = deserializarJsonAutores(respostaJson);
                return autores;
            } catch (IOException e) {
                System.out.println(e.getMessage());
                retryCount++;
            }
        }
        System.out.println(msgMaxRetries);
        return null;
    };

    public static List<Autor> gerarDetalhesMultiplosAutores(List<Autor> listaAutores) {
        String urlBase = "https://api.semanticscholar.org/graph/v1/author/batch";        

        List<String> listaIds = new ArrayList<>();
        Map<String, Autor> autorMap = new HashMap<>();
        
        for (Autor autor : listaAutores) {
            String id = autor.getAuthorId();
            listaIds.add(id);
            autorMap.put(id, autor);
        }

        JsonArray idsArray = new JsonArray();
        for (String id : listaIds) {
            idsArray.add(id);
        }

        JsonObject payload = new JsonObject();
        payload.add("ids", idsArray);

        while (retryCount < maxRetries) {
            try {
                JsonArray respostaJsonArray = buscarRespostaJson(urlBase + sufixoCamposAutor, "POST", payload.toString()).getAsJsonArray();

                List<Autor> autores = new ArrayList<>();

                for (int i = 0; i < listaIds.size(); i++) {
                    JsonObject jsonObraResponse = respostaJsonArray.get(i).getAsJsonObject();
                    Autor autor = autorMap.get(listaIds.get(i));

                    if (autor != null) {
                        autor = popularCamposAutor(jsonObraResponse);
                        autores.add(autor);
                    }
                }

                return autores;

            } catch (IOException e) {
                System.out.println(e.getMessage());
                retryCount++;
            }
        }
        System.out.println(msgMaxRetries);
        return null;
    };

    public static List<Obra> deserializarJsonRecomendacoes(JsonObject respostaJson) {
        List<Obra> recomendacoes = new ArrayList<>();
    
        if (respostaJson.has("recommendedPapers") && !respostaJson.get("recommendedPapers").isJsonNull()) {
            for (int i = 0; i < respostaJson.get("recommendedPapers").getAsJsonArray().size(); i++) {
                Obra recomendacao = new Obra();
                recomendacao = popularCamposObra(recomendacao, respostaJson.get("recommendedPapers").getAsJsonArray().get(i).getAsJsonObject());
                recomendacoes.add(recomendacao);
            }
        }
        return recomendacoes;
    };
    
    public static List<Obra> gerarRecomendacoes(Obra obra, Integer limite) {
        String urlBase = "https://api.semanticscholar.org/recommendations/v1/papers/forpaper/";

        while (retryCount < maxRetries) {
                    try {
                        JsonObject respostaJson = buscarRespostaJson(urlBase + obra.getPaperId() + sufixoCamposObraRecomendada + "&limit=" + limite, "GET", null).getAsJsonObject();
                        List<Obra> recomendacoes = deserializarJsonRecomendacoes(respostaJson);
                        return recomendacoes;
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        retryCount++;
                    }
                }
                System.out.println(msgMaxRetries);
                return null;
    };

};