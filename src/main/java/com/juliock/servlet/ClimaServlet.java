package com.juliock.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juliock.dto.WeatherApiResponseDTO;
import com.juliock.services.ClimaService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;



public class ClimaServlet extends HttpServlet {

    private final ClimaService climaService = new ClimaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*  o metodo doGet() será chamado pelo servidor para requisições GET    */

        // HttpServletRequest é uma interface que preve funcionalidades específicas para processar uma request Http
        // HttpServletResponse de forma semelhante, preve funcionalidades para processar uma response Http


        String cidade = request.getParameter("cidade");
        if(cidade == null || cidade.isBlank()) {
            cidade = "unknown";
        }

        WeatherApiResponseDTO weatherApiResponseDTO = climaService.search(cidade);

        /* RESPOSTA */

        // Configura o tipo da resposta
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        //Serializar o objeto de novo para JSON
        ObjectMapper mapper = new ObjectMapper();
        String responseJson = mapper.writeValueAsString(weatherApiResponseDTO);

        // Escreve a resposta no corpo
        PrintWriter out = response.getWriter();
        out.print(responseJson);
        out.flush();
    }

    //esse metodo vai retornar os query params do requisição em um mapa
    /* private Map<String, String> parseQueryParams(String query) {
        // String query = "http://localhost:8080/clima?cidade=Campinas&unidade=celsius";

        String sub = query.substring(query.indexOf("?") + 1);
        String[] params;

        if (sub.contains("&")) {
            params = sub.split("&");
        } else {
            params = new String[]{sub};
        }

        Map<String, String> m = new HashMap<>();
        for (String pair : params) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8); // transformar todos '%20' em " " (espaços em branco).
                String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                m.put(key, value);
            }
        }
        return m;
    }*/
}
