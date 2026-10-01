package com.example.CineLog.service;

import com.example.CineLog.dto.OmdbFilme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import java.util.List;
import java.util.Map;

@Service
public class OmdbService {

    @Value("${omdb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<OmdbFilme> buscar(String titulo) {
        String url = UriComponentsBuilder.fromUriString("https://www.omdbapi.com/")
            .queryParam("s", titulo)
            .queryParam("apikey", apiKey)
            .build()
            .toUriString();
        ResponseEntity<Map<String, Object>> httpResponse = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {}
        );

        Map<String, Object> response = httpResponse.getBody();
        if (response == null || !"True".equals(response.get("Response"))) return List.of();

        Object search = response.get("Search");
        if (!(search instanceof List<?> resultados)) return List.of();

        return resultados.stream()
            .filter(Map.class::isInstance)
            .map(item -> (Map<?, ?>) item)
            .map(r -> {
            OmdbFilme f = new OmdbFilme();
            f.setImdbId(readString(r, "imdbID"));
            f.setTitle(readString(r, "Title"));
            f.setYear(readString(r, "Year"));
            f.setPoster(readString(r, "Poster"));

            Map<String, Object> detalhes = buscarDetalhes(f.getImdbId());
            f.setDirector(readString(detalhes, "Director"));
            f.setActors(readString(detalhes, "Actors"));
            f.setGenre(readString(detalhes, "Genre"));
            f.setRuntime(readString(detalhes, "Runtime"));
            f.setPlot(readString(detalhes, "Plot"));
            return f;
        }).toList();
    }

    private Map<String, Object> buscarDetalhes(String imdbId) {
        if (imdbId == null || imdbId.isBlank()) return Map.of();

        String url = UriComponentsBuilder.fromUriString("https://www.omdbapi.com/")
            .queryParam("i", imdbId)
            .queryParam("plot", "short")
            .queryParam("apikey", apiKey)
            .build()
            .toUriString();

        try {
            ResponseEntity<Map<String, Object>> httpResponse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> response = httpResponse.getBody();
            if (response == null || !"True".equals(response.get("Response"))) return Map.of();
            return response;
        } catch (RestClientException ex) {
            return Map.of();
        }
    }

    private String readString(Map<?, ?> source, String key) {
        Object value = source.get(key);
        if (value == null) return "";

        String texto = String.valueOf(value).trim();
        if (texto.equalsIgnoreCase("N/A")) return "";
        return texto;
    }
}