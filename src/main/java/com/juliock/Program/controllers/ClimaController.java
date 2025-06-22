package com.juliock.Program.controllers;

import com.juliock.Program.services.ClimaService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

// Essa classe será um "Handler".
// Handler é um componente responsável por processar uma requisição HTTP.
// Ela deve implementar a interface "HttpHandler"
public class ClimaController implements HttpHandler {

    private final ClimaService climaService = new ClimaService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        /*  a classe HttpExchange encapsula o conjunto requisição Http/resposta Http, ou seja, exchange é um objeto
                que vai conter a requisição Http (e métodos para lidar com ela),
                e também será usado para construir a resposta Http (também com métodos para essa finalidade). */

        // essa API só vai atender a requisições GET, se o metodo nao for GET entramos no caso abaixo
        if(!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405,-1); // retorna a response com o cod 405: Method Not Allowed
            return;
        }

        // o componente Handler deve chamar um serviço da camada Service.
        // essa chamada de uma função do service vai passar os query Params da URL
        // portanto temos agora que extrair os query params da URL

        /* REQUEST */

        URI requestURI = exchange.getRequestURI();
        Map<String, String> queryParams = parseQueryParams(requestURI.getRawQuery());
        String cidade = queryParams.getOrDefault("cidade", "unknown"); // pega o valor da key "cidade" ou, se não houver, o valor "unknown" e atribui para a variavel

        // chama o serviço para procurar os dados pela cidade
        String respostaJson = climaService.search(cidade); // retorna um JSON simulado por enquanto.


        /* RESPONSE */

        byte[] byteRespose = respostaJson.getBytes(StandardCharsets.UTF_8); // para trafegar a resposta tem que estar em bytes, StandardCharsets.UTF_8 assegura o encoding apropriado (UTF-8)
        exchange.getResponseHeaders().add("Content-Type", "application/json"); // Define o header HTTP "Content-Type" especificando o tipo de conteúdo da resposta ("application/json").
        exchange.sendResponseHeaders(200, byteRespose.length); // envia o Status Code e o tamanho do corpo (numero de bytes).

        try(OutputStream os = exchange.getResponseBody()) {
            os.write(byteRespose);
        }
    }

    //esse metodo vai retornar os query params do requisição em um mapa
    private Map<String, String> parseQueryParams(String query) {
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
    }
}
