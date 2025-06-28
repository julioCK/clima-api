package com.juliock.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juliock.dto.WeatherApiResponseDTO;
import com.juliock.exceptions.ApiCallException;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class ClimaService {
                                                                                        // 172.17.0.1 é o IP do container do redis
    private final RedisCacheService redisCacheService = new RedisCacheService("172.17.0.1", 6379);
    private final int CACHE_TTL_SECONDS = 3600;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // --- API URL ---
    private static final String API_KEY = "RDG44J4HGGC7JHJYA5TZARFPK";
    private static final String API_BASE_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";
    private static final String UNIT_GROUP = "metric";
    private static final String ELEMENTS = String.join(",", "datetime","name","tempmax","tempmin","temp","feelslike","precip","precipprob");
    private static final String INCLUDE = String.join(",", "hours","fcst","remote","obs","stats");
    private static final String OPTIONS = "useobs";
    private static final String CONTENT_TYPE = "json";
    // --- ! ---

    public WeatherApiResponseDTO search(String cidade) throws IOException, ApiCallException {

        /* Antes de fazer uma chamada à API Visual Crossing, verificar se não existe uma key registrada no Redis que está
        *  associada à consulta.
        *  Os dados salvos no redis terão sempre uma key com o mesmo nome da cidade. */

        String cacheKey = cidade.toLowerCase(); // a key que será consultada no redis é o nome da cidade.
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
            String requestUrl = buildURL(cidade.toLowerCase(), "today");
            System.out.println(requestUrl); // exibe URL no log do servidor
            HttpGet request = new HttpGet(requestUrl);

            // ----- Enviar a requisição Get, receber a RESPOSTA e converte-la em String atribuindo à variavel responseStr -----
            /* o metodo HttpClient.execute() recebe como argumentos a requisição e um implementação da interface funcional
               HttpClientResponseHandler. A função lambda passada como parametro abaixo implementa o unico metodo dessa interface. */

             responseStr = client.execute(request, response -> {
                 int status = response.getCode(); // Cod HTTP
                 String responseBody = EntityUtils.toString(response.getEntity()); // getEntity() le o corpo da resposta e EntityUtils.toString converte para String

                 if (status >= 200 && status < 300) {
                     return responseBody;
                 } else {
                     throw new ApiCallException(status, responseBody); // se der algum erro na chamada, o status code e a mensagem do corpo do http retornado pela api vao ser registradas nessa exception.
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

    private String buildURL(String cidade, String date) {
        String endPoint = String.join("/", cidade, date);

        return String.format("%s%s?unitGroup=%s&elements=%s&include=%s&key=%s&options=%s&contentType=%s",
                API_BASE_URL,
                endPoint,
                UNIT_GROUP,
                URLEncoder.encode(ELEMENTS, StandardCharsets.UTF_8), // garantir que a "," que separa os termos seja codificada como "%2C"
                URLEncoder.encode(INCLUDE, StandardCharsets.UTF_8), // garantir que a "," que separa os termos seja codificada como "%2C"
                API_KEY,
                OPTIONS,
                CONTENT_TYPE);
    }
}
