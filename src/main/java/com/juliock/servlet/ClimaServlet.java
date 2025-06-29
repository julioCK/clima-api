package com.juliock.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juliock.dto.WeatherApiResponseDTO;
import com.juliock.exceptions.ApiCallException;
import com.juliock.exceptions.ApiRequestLimitReachedException;
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
        /*  o metodo doGet() será chamado pelo servidor para requisições GET;
                - HttpServletRequest é uma interface que preve funcionalidades específicas para processar uma request Http;
                - HttpServletResponse de forma semelhante, preve funcionalidades para processar uma response Http;  */

        String cidade = request.getParameter("cidade");
        if(cidade == null || cidade.isBlank()) {
            cidade = "unknown";
        }

        /* RESPOSTA */
        // Configura o tipo da resposta
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String clientID = request.getRemoteAddr();

            WeatherApiResponseDTO weatherApiResponseDTO = climaService.search(cidade, clientID);
            response.setStatus(HttpServletResponse.SC_OK); // seta o status code da resposta para 200 OK

            //Serializar o objeto de novo para JSON
            ObjectMapper mapper = new ObjectMapper();
            String responseJson = mapper.writeValueAsString(weatherApiResponseDTO);

            // Escreve a resposta no corpo
            PrintWriter out = response.getWriter();
            out.print(responseJson);
            out.flush();
        }
        catch(ApiCallException ace) {
            response.setStatus(ace.getStatusCode());
            response.getWriter().write("Erro: Status " + ace.getStatusCode() + " - " + ace.getApiMessage());
        }
        catch(ApiRequestLimitReachedException e) {
            response.setStatus(429);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
