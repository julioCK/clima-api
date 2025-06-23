package com.juliock;

// um servidor HTTP integrado no java. É um servidor enxuto, usado principalmente para testes.
import com.juliock.servlet.ClimaServlet;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {

//        // cria um servidor Http escutando no ip 0.0.0.0 porta 8080.
//        //  *0.0.0.0 pode ser qualquer IP
//        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
//
//        // cria um "contexto" que representa um mapeamento entre um determinado URI e um "handler".
//        //  Handler é um componente responsável por processar requisições http em um servidor http.
//        //  Nesse caso a classe que vai implementar o HttpHandler é a ClimaController.
//        server.createContext("/clima", new ClimaServlet());
//
//        // Se quisesse customizar o número de threads, paralelismo ou filas, aqui seria o lugar.
//        // null = usa o executor (gerenciador de threds) padrao da JVM.
//        server.setExecutor(null);
//
//        // inicia o servidor. A partir daqui estará ouvindo na porta 8080 até ser parado.
//        server.start();
//        System.out.println("Servidor iniciado em http://localhost:8080/clima");
    }
}
