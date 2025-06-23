package com.juliock.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class ClimaService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String search(String cidade) {
        try {
            Map<String, String> simDataMap = new HashMap<>();
            simDataMap.put("cidade", cidade);
            simDataMap.put("temperatura", "20C");
            simDataMap.put("Condição", "Parcialmente Nublado");

            return objectMapper.writeValueAsString(simDataMap); // converte o map para o formato JSON
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
