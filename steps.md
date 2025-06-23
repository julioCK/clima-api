
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
> É uma classe Java que implementa a interface javax.servlet.Servlet e é responsável por processar
> e tratar **requisições Http** em aplicações web Java.
> 
> Elas funcionam como módulos do lado do servidor que processam requisições, executam lógica (acessam
> banco de dados, geram HTML, etc) e **retornam uma resposta**.

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