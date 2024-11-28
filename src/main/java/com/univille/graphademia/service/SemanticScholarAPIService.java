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
    public static String sufixoCamposAutor = "&fields=name,papers,externalIds,hIndex";
    
    private static JsonObject fetchJsonResponse(String urlString, String metodo, String payload) throws IOException {
        @SuppressWarnings("deprecation")
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod(metodo);
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
    
        // Se for post, mandar payload
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
                return gson.fromJson(response.toString(), JsonObject.class);
            }
            case 429 -> {
                String retryAfter = connection.getHeaderField("Retry-After");
                tempoDeEspera = retryAfter != null ? Integer.parseInt(retryAfter) * 1000 : tempoDeEspera * 2;
                throw new IOException("Requests demais... Retry-After: " + tempoDeEspera + " ms");
            }
            default -> throw new IOException("Erro HTTP: " + responseCode);
        }
    };

    private static String getJsonString(JsonObject jsonObject, String fieldName) {
        return jsonObject.has(fieldName) ? jsonObject.get(fieldName).getAsString() : null;
    };
    
    private static Integer getJsonInt(JsonObject jsonObject, String fieldName) {
        return jsonObject.has(fieldName) ? jsonObject.get(fieldName).getAsInt() : null;
    };

    private static Obra popularCamposObra(Obra obra, JsonObject jsonResponse) {
        obra.setPaperId(getJsonString(jsonResponse, "paperId"));
        obra.setTitle(getJsonString(jsonResponse, "title"));
        obra.setYear(getJsonInt(jsonResponse, "year"));
    
        JsonObject externalIds = jsonResponse.getAsJsonObject("externalIds");
        if (externalIds != null) {
            obra.setDoi(getJsonString(externalIds, "DOI"));
        }
    
        JsonObject publicationVenue = jsonResponse.getAsJsonObject("publicationVenue");
        if (publicationVenue != null) {
            obra.setPublicationVenueName(getJsonString(publicationVenue, "name"));
            obra.setPublicationVenueType(getJsonString(publicationVenue, "type"));
        }
    
        JsonObject openAccessPdf = jsonResponse.getAsJsonObject("openAccessPdf");
        if (openAccessPdf != null) {
            obra.setUrl(getJsonString(openAccessPdf, "url"));
        }
    
        JsonObject tldr = jsonResponse.getAsJsonObject("tldr");
        if (tldr != null) {
            obra.setTldr(getJsonString(tldr, "text"));
        }
        return obra;
    };

    public static Obra deserializarJsonObra(JsonObject jsonResponse) {
        Obra obra = new Obra();
        popularCamposObra(obra, jsonResponse);
    
        if (jsonResponse.has("publicationTypes") && jsonResponse.get("publicationTypes").isJsonArray()) {
            StringBuilder typesBuilder = new StringBuilder();
            for (JsonElement typeElement : jsonResponse.getAsJsonArray("publicationTypes")) {
                typesBuilder.append(typeElement.getAsString()).append(", ");
            }
            obra.setPublicationTypes(typesBuilder.length() > 0 ? typesBuilder.substring(0, typesBuilder.length() - 2) : null);
        }
    
        if (jsonResponse.has("authors") && jsonResponse.get("authors").isJsonArray()) {
            List<Autor> autores = new ArrayList<>();
            for (JsonElement authorElement : jsonResponse.getAsJsonArray("authors")) {
                JsonObject authorObj = authorElement.getAsJsonObject();
                Autor author = new Autor(
                    getJsonString(authorObj, "authorId"),
                    getJsonString(authorObj, "name")
                );
                autores.add(author);
            }
            obra.setAuthors(autores);
        }
    
        if (jsonResponse.has("references") && jsonResponse.get("references").isJsonArray()) {
            List<Referencia> listaReferencias = new ArrayList<>();
            for (JsonElement reference : jsonResponse.getAsJsonArray("references")) {
                JsonObject refObject = reference.getAsJsonObject();
                listaReferencias.add(new Referencia(
                    getJsonString(refObject, "paperId"),
                    getJsonString(refObject, "title")
                ));
            }
            obra.setReferencias(listaReferencias);
        }

        if (jsonResponse.has("fieldsOfStudy") && jsonResponse.get("fieldsOfStudy").isJsonArray()) {
            List<Area> areas = new ArrayList<>();
            for (JsonElement reference : jsonResponse.getAsJsonArray("fieldsOfStudy")) {
                JsonObject refObject = reference.getAsJsonObject();
                areas.add(new Area(refObject.getAsString()));
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
                JsonObject jsonResponse = fetchJsonResponse(urlBase + encodedNome + sufixoCamposObra, "GET", null);
                Obra obra = deserializarJsonObra(jsonResponse);
                obra.gerarObrasAPartirDeReferencia(obra.getReferencias());
                return obra;
            } catch (IOException e) {
                System.out.println("Erro: " + e.getMessage());
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
                JsonObject jsonResponse = fetchJsonResponse(urlBase + id + sufixoCamposObra, "GET", null);
                Obra obra = deserializarJsonObra(jsonResponse);
                obra.gerarObrasAPartirDeReferencia(obra.getReferencias());
                return obra;
            } catch (IOException e) {
                System.out.println("Erro: " + e.getMessage());
                retryCount++;
            }
        }
        System.out.println(msgMaxRetries);
        return null;
    };

    public static List<Obra> gerarDetalhesMultiplasObras(List<Obra> listaObras) {
        String urlBase = "https://api.semanticscholar.org/graph/v1/paper/batch?";        
    
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
                JsonObject jsonResponse = fetchJsonResponse(urlBase + sufixoCamposObra, "POST", payload.toString());
    
                // Pegar resposta e atualizar POJO
                JsonArray jsonResponseArray = jsonResponse.getAsJsonArray("papers");

                List<Obra> obras = new ArrayList<>();
    
                for (int i = 0; i < listaIds.size(); i++) {
                    JsonObject jsonObraResponse = jsonResponseArray.get(i).getAsJsonObject();
                    Obra obra = obraMap.get(listaIds.get(i));
    
                    if (obra != null) {
                        popularCamposObra(obra, jsonObraResponse);
                        obra.gerarObrasAPartirDeReferencia(obra.getReferencias());
                        obras.add(obra);
                    }
                }

                return obras;

            } catch (IOException e) {
                System.out.println("Erro: " + e.getMessage());
                retryCount++;
            }
        }
        System.out.println(msgMaxRetries);
        return null;
    };

    private static Autor popularCamposAutor(JsonObject jsonDataIndex) {
        Autor autor = new Autor();

        autor.setAuthorId(getJsonString(jsonDataIndex, "authorId"));
        autor.setName(getJsonString(jsonDataIndex, "name"));
        autor.setHindex(getJsonInt(jsonDataIndex, "hIndex"));
    
        JsonObject externalIds = jsonDataIndex.getAsJsonObject("externalIds");
        if (externalIds != null) {
            autor.setDblp(getJsonString(externalIds, "dblp"));
            autor.setOrcid(getJsonString(externalIds, "orcid"));
        }
        return autor;
    };

    public static List<Autor> deserializarJsonAutores(JsonObject jsonResponse) {
        List<Autor> autores = new ArrayList<>();

        for (int i = 0; i < jsonResponse.get("data").getAsJsonArray().size(); i++) {
            Autor autor = popularCamposAutor(jsonResponse.get("data").getAsJsonArray().get(i).getAsJsonObject());
            autores.add(autor);
        }
        return autores;
    };
    
    public static List<Autor> gerarAutorPorNome(String nome) {
        String urlBase = "https://api.semanticscholar.org/graph/v1/author/search?query=";

        while (retryCount < maxRetries) {
            try {
                String encodedNome = URLEncoder.encode(nome, StandardCharsets.UTF_8.toString());
                JsonObject jsonResponse = fetchJsonResponse(urlBase + encodedNome + sufixoCamposAutor, "GET", null);
                List<Autor> autores = deserializarJsonAutores(jsonResponse);
                return autores;
            } catch (IOException e) {
                System.out.println("Erro: " + e.getMessage());
                retryCount++;
            }
        }
        System.out.println(msgMaxRetries);
        return null;
    };

    public static List<Autor> gerarDetalhesMultiplosAutores(List<Autor> listaAutores) {
        String urlBase = "https://api.semanticscholar.org/graph/v1/author/batch?";        

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
                JsonObject jsonResponse = fetchJsonResponse(urlBase + sufixoCamposAutor, "POST", payload.toString());

                // Pegar resposta e atualizar POJO
                JsonArray jsonResponseArray = jsonResponse.getAsJsonArray("papers");

                List<Autor> autores = new ArrayList<>();

                for (int i = 0; i < listaIds.size(); i++) {
                    JsonObject jsonObraResponse = jsonResponseArray.get(i).getAsJsonObject();
                    Autor autor = autorMap.get(listaIds.get(i));

                    if (autor != null) {
                        autor = popularCamposAutor(jsonObraResponse);
                        autores.add(autor);
                    }
                }

                return autores;

            } catch (IOException e) {
                System.out.println("Erro: " + e.getMessage());
                retryCount++;
            }
        }
        System.out.println(msgMaxRetries);
        return null;
    };

    public static List<Obra> deserializarJsonRecomendacoes(JsonObject jsonResponse) {
        List<Obra> recomendacoes = new ArrayList<>();

        for (int i = 0; i < jsonResponse.get("recommendedPapers").getAsJsonArray().size(); i++) {
            Obra recomendacao = new Obra();
            recomendacao = popularCamposObra(recomendacao, jsonResponse.get("recommendedPapers").getAsJsonArray().get(i).getAsJsonObject());
            recomendacoes.add(recomendacao);
        }
        return recomendacoes;
    };

    public static List<Obra> gerarRecomendacoes(Obra obra) {
        String urlBase = "https://api.semanticscholar.org/recommendations/v1/papers/forpaper/";

        while (retryCount < maxRetries) {
                    try {
                        JsonObject jsonResponse = fetchJsonResponse(urlBase + obra.getPaperId() + sufixoCamposAutor, "GET", null);
                        List<Obra> recomendacoes = deserializarJsonRecomendacoes(jsonResponse);
                        return recomendacoes;
                    } catch (IOException e) {
                        System.out.println("Erro: " + e.getMessage());
                        retryCount++;
                    }
                }
                System.out.println(msgMaxRetries);
                return null;
    }

};