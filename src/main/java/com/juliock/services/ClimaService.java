package com.juliock.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juliock.dto.WeatherApiResponseDTO;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;


public class ClimaService {
                                                                                        // 172.17.0.2 é o IP do container do redis
    private final RedisCacheService redisCacheService = new RedisCacheService("172.17.0.2", 6379);
    private final int CACHE_TTL_SECONDS = 3600;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String API_KEY = "RDG44J4HGGC7JHJYA5TZARFPK";
    private final String API_BASE_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";

    public WeatherApiResponseDTO search(String cidade) throws IOException {
        String testRequest = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Campinas/today?unitGroup=metric&elements=datetime%2Cname%2Ctempmax%2Ctempmin%2Ctemp%2Cfeelslike%2Cprecip%2Cprecipprob&include=hours%2Cfcst%2Cremote%2Cobs%2Cstats&key=RDG44J4HGGC7JHJYA5TZARFPK&options=useobs&contentType=json";

        /* Antes de fazer uma chamada à API Visual Crossing, verificar se não existe uma key registrada no Redis que está
        *  associada à consulta.
        *  Os dados salvos no redis terão sempre uma key com o mesmo nome da cidade. */

        String cacheKey = cidade;
        String cachedJSON = redisCacheService.get(cacheKey);

        String responseStr = "";
        if(cachedJSON == null) {
            System.out.println("[Cache MISS] Dados não encontrados no Redis. Chamando API externa.");
            //  se a chave fornecida ao metodo get() do redis retornar null significa que não há registros,
            //  nesse caso faremos a chamada à API externa:

            /* A biblioteca Apache httpclient5 vai permitir realizar requisições http para a API externa.
                HttpClient é a interface principal dessa biblioteca. Cria conexões, envia requisições e recebe respostas
                HttpClients é a factory de HttpClient. creatDefault() cria uma instancia com as configurações padrão. */
            HttpClient client = HttpClients.createDefault();

            // ----- Criar uma requisição GET -----
            HttpGet request = new HttpGet(testRequest);

            // ----- Enviar a requisição Get, receber a RESPOSTA e converte-la em String atribuindo à variavel responseStr -----
                /* o metodo HttpClient.execute() recebe como argumentos a requisição e um implementação da interface funcional
                   HttpClientResponseHandler. A função lambda abaixo implementa o unico metodo dessa interface. */
             responseStr = client.execute(request, response -> {

                int status = response.getCode(); // Cod HTTP
                if (status >= 200 && status < 300) {
                    return EntityUtils.toString(response.getEntity()); // getEntity() le o corpo da resposta e EntityUtils.toString converte para String
                } else {
                    throw new RuntimeException("Erro http: " + status);
                }
            });

             // ----- Salvar a resposta da API no Redis por 1h (3600 seg); -----
            redisCacheService.set(cacheKey, responseStr, CACHE_TTL_SECONDS);
        }
        else {
            System.out.println("[Cache HIT] Encontrado no Redis. Usando cache!");
            responseStr = cachedJSON;
        }

        // ----- Desserializar a String JSON instanciando uma classe DTO correspondente com JACKSON -----
        /*  A classe ObjectMapper é a principal da lib Jackson, ela será responsável por desserializar o JSON e instanciar as
         *   classes correspondentes.
         * */
        WeatherApiResponseDTO weatherApiResponseDTO = objectMapper.readValue(responseStr, WeatherApiResponseDTO.class);

        return weatherApiResponseDTO;
    }
}
