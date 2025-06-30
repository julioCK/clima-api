
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

---
## Step 1: 

Pra começar 

- Criar classe Main;
- Iniciar um servidor HTTP embutido na porta 8080 na classe MAIN;
- Criar uma Handler que é uma classe que implementa a interface HttpHandler (classe ClimaController);
- implementar o método ``handle()`` que vai receber a `request` e enviar a `response`;
- Responder com um JSON simulado, sem integrar com Redis nem API externa ainda;


## 

### Quem chama o método `handle()`?

O método `handle(HttpExchange exchange)` da classe `ClimaController` (ou qualquer classe que implemente `HttpHandler`) **é chamado automaticamente pelo servidor HTTP embutido do Java** (`com.sun.net.httpserver.HttpServer`).

###  Como isso acontece?

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

---
## Step 2:

Agora vamos mudar do servidor Http embutido do java para o **Apache Tomcat** separado da aplicação.

#### Primeiro algumas definições para ficar claro:

> **Servlet**
> 
> É uma classe Java que herda de `jakarta.servlet.http.HttpServlet` e é responsável por processar
> e tratar **requisições Http** em aplicações web Java.
> 
> Elas funcionam como módulos do lado do servidor que processam requisições, executam lógica (acessam
> banco de dados, geram HTML, etc) e **retornam uma resposta**.
> 
> É boa prática que o Servlet atue como **Controller**, 
> delegando a lógica de negócio para outras classes. 
> Isso promove separação de responsabilidades, testabilidade e manutenibilidade do código.

> **Tomcat**
>
> É um servidor de aplicação.
> Funciona como um container de servlets, como um ambiente onde os servlets vivem.
>
> O Tomcat é responsável por:
> - Escutar portas Http (como a port 8080)
> - Interpretar arquivos `.war`
> - Executar os servlets
> - Gerencia ciclo de vida, segurança, sessões e outras funcionalidades

Como daqui pra frente vamos utilizar o Tomcat, precisamos providenciar servlets para que ele execute.

- I - Refatorar a classe ClimaController para `ClimaServlet`.
  -   Para que o Tomcat reconheça essa classe como um servlet, ela precisa ser herdeira da classe `javax.servlet.http.HttpServlet`.


* *&nbsp;O Tomcat espera um arquivo `.war` pra poder rodar. 
    > **.war** (web archive) é o formato padrão oficial para aplicações java web.
    É como se fosse um arquivo .zip que contém as classes compiladas (arquivos `.class`), arquivos de configuração,
    arquivos `.html`, `.css`, `.js`, etc.

    O `.war` é organizado pelo Maven numa estrutura de diretórios padronizada, exemplo:

    **(exemplo.war)**<br/>
    `/ `  (root) <br/>
    ├── `index.html`<br/>
    └── `WEB-INF/` (diretório invisível para o navegador que contem arquivos de config e segurança)<br/>
    &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;└── `web.xml`&nbsp; (Arquivo de configuração da aplicação web)<br/>
    &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;└── `lib/`&nbsp; (JARs necessários para a aplicação (Maven cuida disso))<br/> 
    &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;└── `classes/`&nbsp; (Onde o Maven coloca os `.class` compilados)<br/>

  > Dentro do diretório WEB-INF vai um arquivo nomeado `web.xml`. Esse arquivo é fundamental, pois é nele
  que estarão contidas orientações que o servidor seguirá na hora de executar a aplicação.
  
  Exemplo:
```xml
 <!-- Declara o servlet -->
    <servlet>
        <servlet-name>ClimaServlet</servlet-name>
        <servlet-class>com.juliock.servlet.ClimaServlet</servlet-class>
    </servlet>

    <!-- Mapeia uma URL para o servlet -->
    <servlet-mapping>
        <servlet-name>ClimaServlet</servlet-name>
        <url-pattern>/clima</url-pattern>
    </servlet-mapping>

```
---
## Step 3:

  - Testar uma requisiçao à API do Visual Crossing.
  - Para enviar essa requisição será usada a biblioteca `httpclient5` da Apache.

>Essa biblioteca permite, entre outras coisas:
>
>✅ Enviar requisições HTTP (GET, POST, PUT, DELETE, etc.) <br/>
>✅ Adicionar cabeçalhos (headers) <br/>
>✅ Incluir parâmetros e corpo da requisição <br/>
>✅ Tratar respostas (status, corpo da resposta, erros) <br/>
<br/>
  - URL base : https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/
    - Endpoints da API Visual Crossing: 
      - /timeline/`location` – forecast queries.
      - /timeline/`location`/`date1`/`date2` – queries for a specific date range.

---
## Estado atual:
✅ Um servlet (`ClimaServlet`) servindo a rota `/clima`;<br/>
✅ Uma classe de serviço (`ClimaService`) que consulta dados reais da Visual Crossing Weather API;<br/>
✅ Projeto empacotado como `.war`, rodando dentro de um container Docker com Tomcat;<br/>
✅ Retorno de String JSON cru vindo da API externa para o cliente.
<br/><br/>
---
## Step 4:

  - Implementar classes DTO para representar os dados vindos a API externa;
    - A resposta da API externa é uma String JSON.
    - A biblioteca <ins>Jackson</ins> vai extrair os dados JSON e instanciar essas classes.
    - Escolhemos os dados que queremos da resposta da API e criamos as classes com campos com os mesmos nomes dos campos do JSON.
    - Todos os campos devem ter getters e setters e as classes devem ter um construtor padrão vazio.
  - Desserializar:  JSON -> Objeto Java
  - Serializar:     Objeto Java -> JSON
  - Enviar String JSON como resposta.
### Jackson
 - O `ObjectMapper` é a principal classe da biblioteca Jackson para trabalhar com JSON em Java. Ele fornece métodos para serializar (converter objeto → JSON) e desserializar (converter JSON → objeto).
 - Principais métodos:

| Método                          | Para que serve                                                  |
| ------------------------------- | --------------------------------------------------------------- |
| `readValue(json, Classe.class)` | JSON → Objeto Java                                              |
| `writeValueAsString(objeto)`    | Objeto Java → String JSON                                       |
| `writeValue(File, objeto)`      | Objeto Java → JSON em arquivo                                   |
| `registerModule(modulo)`        | Ativa suporte extra (ex: datas do Java 8)                       |
| `configure(...)`                | Ativa/desativa comportamentos (ex: falha em campo desconhecido) |
---
## Step 5:

Para que a API Java fique mais eficiente evitando chamadas repetidas e lentas à API externa (Visual Crossing Weather).

Sem Redis:<br/>
- Toda requisição para sua API → chama Visual Crossing → traz os dados → devolve.<br/>
- Mesmo que o usuário pergunte mil vezes a mesma previsão, você paga latência e custo da API externa sempre.

Com Redis:<br/>

- Sua API pergunta pro Redis: “Já tenho essa previsão?”<br/>
✅ Se sim, devolve instantaneamente (em memória, super rápido).<br/>
❌ Se não, consulta a Visual Crossing, salva o resultado no Redis e devolve.


#### Fluxo basico da aplicação com Redis:

```csharp
[HTTP Client]
    ↓
[Servlet / Controller]
    ↓
[ClimaService]
    ↓
[Check Redis] <── Already there? ──✔── Result
    ↓
Not there
    ↓
[Call Visual Crossing]
    ↓
[Store in Redis (with TTL)]
    ↓
[Return to Client]

```
#### O código Java vai precisar:

✅ Conectar ao servidor Redis (hostname, porta)<br/>
✅ Consultar se a chave existe (GET)<br/>
✅ Salvar se não existir (SET com EXPIRE)<br/>
✅ Transformar JSON para objeto (usando Jackson, como já faz)<br/>

#### Implementar o **Redis** para atuar como cache das chamadas à API visual crossing.
  - #1: subir um container docker com o redis: `docker run --name redis-clima -p 6379:6379 -d redis`
  - #2: Para a aplicação Java se conectar com o Redis, precisamos de um cliente Redis para o Java: **Jedis**.
  - #3: Criar uma classe de serviço para encapsular o acesso do aplicativo ao Redis: `RedisCacheService` 

---
## Step 6:
- <ins>Tratamento de exceções</ins>:
A API externa envia mensagens sobre eventuais erros no corpo da resposta. O ideal é capturar essas
mensagens e exibir para o usuário. Também temos que tratar com mais especificidade os principais
status code de erro.<br/><br/>

    - **#1**: Criar uma exception personalizada para os erros: `ApiCallException`;
      - Essa Exception vai conter o status code e a mensagem que a API da visual crossing devolve;
      - O corpo da resposta, no caso de erro, é texto puro (String).
    - **#2**: Refatorar `ClimaService` e `ClimaServlet`.

---
## Step 7: 
 - Limitar a quantidade de requests à API do Virtual Crossing. Serão 2 tipos de limite
   - Limite total diário de 1000 chamadas;
   - Limite por ip de usuário/hora de 10 chamadas;
---
## Step 8:
 - Rodar a aplicação com **Docker-Compose**;
   - Empacotar a aplicação no formato `.war` com `> mvn clean package`;
   - Criar o arquivo `Dockerfile` na raíz da aplicação;
     - *esse arquivo vai conter instruções para subir um container tomcat com a aplicação dentro;
   - Criar o arquivo `docker-compose.yml` para definir os serviços que vão rodar (container) e a relação entre eles;
   - Build e execução: `docker-compose up --build` (no diretório raíz).