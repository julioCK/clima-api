
Uma API é uma construção que permite que dois sistemas se comuniquem entre si.

Uma API REST é uma API que segue os principios REST, que é uma arquitetura baseada
no protocolo **HTTP** e em **resources**.

Esse projeto será uma API REST, portanto permitirá que sistemas clientes interajam através
de **requisições HTTP** que vão conter ações como `GET`, `POST`, `PUT`, `DELETE` e serão direcionadas
para um **recurso** identificado por uma **URI**.

Para receber as requisições a nossa API vai contar com um servidor HTTP.

Esse servidor vai:
- escutar requisições vindas de um cliente através de uma porta (ex: porta 80(HTTP), porta 443(HTTPS), etc);
- identifica a URL e o método HTTP (ex metodo: `GET`, URL: /clientes/1)
- encaminha esses dados para um **handler** que geralmente é uma função mapeada para receber essa rota+metodo
- devolve a resposta fornecida pela lógica contida na aplicação para o cliente.

### Step 1:

Pra começar 

- Criar classe Main;
- Iniciar um servidor HTTP embutido na porta 8080 na classe MAIN;
- Criar uma Handler que é uma classe que implementa a interface HttpHandler (classe ClimaController);
- implementar o método ``handle()`` que vai receber a `request` e enviar a `response`;
- Responder com um JSON simulado, sem integrar com Redis nem API externa ainda;



## 

### 🧠 Quem chama o método `handle()`?

O método `handle(HttpExchange exchange)` da classe `ClimaController` (ou qualquer classe que implemente `HttpHandler`) **é chamado automaticamente pelo servidor HTTP embutido do Java** (`com.sun.net.httpserver.HttpServer`).

### ✅ Como isso acontece?

No `Main.java`, você registra um contexto com:

```java
server.createContext("/clima", new ClimaController());
```
Com isso, você está dizendo:

    "Sempre que uma requisição for feita para /clima, use essa instância de ClimaController para tratar."

Depois, quando o servidor é iniciado com:
```server.start();``` ele fica escutando na porta (8080, no exemplo). 

A cada nova requisição HTTP para o caminho /clima, o servidor:

- Cria um objeto HttpExchange que encapsula os dados da requisição
- Chama o método: ```climaController.handle(exchange);```
- Aguarda que o método escreva a resposta para o cliente (por meio de exchange.getResponseBody())