package com.juliock.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;


public class ClimaService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String API_KEY = "RDG44J4HGGC7JHJYA5TZARFPK";
    private final String API_BASE_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";

    public String search(String cidade) throws IOException {
        String testRequest = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Campinas/today?unitGroup=metric&elements=datetime%2Cname%2Ctempmax%2Ctempmin%2Ctemp%2Cfeelslike%2Cprecip%2Cprecipprob&include=hours%2Cfcst%2Cremote%2Cobs%2Cstats&key=RDG44J4HGGC7JHJYA5TZARFPK&options=useobs&contentType=json";

        // a biblioteca Apache httpclient5 vai permitir realizar requisições http para a API externa.

        // HttpClient é a interface principal dessa biblioteca. Cria conexões, envia requisições e recebe respostas
        // HttpClients é a factory de HttpClient. creatDefault() cria uma instancia com as configurações padrão.
        HttpClient client = HttpClients.createDefault();

        // Criar uma requisição GET
        HttpGet request = new HttpGet(testRequest);

        // Enviar a requisição Get, receber a resposta e converte-la em String atribuindo à variavel responseStr

            /* o metodo HttpClient.execute() recebe como argumentos a requisição e um implementação da interface funcional
               HttpClientResponseHandler. A função lambda abaixo implementa o unico metodo dessa interface. */
        String responseStr = client.execute(request, response -> {

            int status = response.getCode(); // Cod HTTP
            if(status >= 200 && status < 300) {
                return EntityUtils.toString(response.getEntity()); // getEntity() le o corpo da resposta e EntityUtils.toString
                                                                  //    converte para String
            }
            else {
                throw new RuntimeException("Erro http: " + status);
            }
        });

        return responseStr;
    }
}
