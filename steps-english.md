# Steps for Building and Running the Java Weather API with Docker Compose

## General Concept

An API is a construct that allows two systems to communicate with each other.

A REST API is an API that follows REST principles, which is an architecture based on the HTTP protocol and resources.

This project is a REST API, allowing client systems to interact via HTTP requests using actions such as GET, POST, PUT, DELETE, which target a resource identified by a URI.

To receive requests, our API will use an HTTP server.

This server will:

- Listen for requests coming from a client on a port (e.g. port 80 (HTTP), port 443 (HTTPS), etc.).
- Identify the URL and the HTTP method (e.g. method: GET, URL: /clients/1).
- Dispatch these data to a handler (usually a function mapped to that route + method).
- Return the response produced by the application logic to the client.

---

## Step 1:

**Getting Started:**

- Create a `*Main` class.
- Start an embedded HTTP server on port 8080 in the `*Main` class.
- Create a Handler class that implements the `HttpHandler` interface (`ClimaController` class).
- Implement the `handle()` method to receive the request and send the response.
- Respond with simulated JSON, without integrating Redis or any external API yet.

**Who calls the `handle()` method?**

The `handle(HttpExchange exchange)` method of `ClimaController` (or any class implementing `HttpHandler`) is automatically called by Java’s embedded HTTP server (`com.sun.net.httpserver.HttpServer`).

**How does it work?**

In `Main.java`, you register a context with:

```java
server.createContext("/clima", new ClimaController());
```
Meaning:

    “Whenever a request is made to /clima, use this ClimaController instance to handle it.”

When the server starts with server.start();, it listens on the port (8080 in this example).

For each HTTP request to /clima:

- It creates an HttpExchange object that encapsulates the request data.

- Calls climaController.handle(exchange);

- Waits for the method to write the response to the client via exchange.getResponseBody().

---
## Step 2:

Migrating from embedded Java HTTP server to Apache Tomcat.
- Definitions

> Servlet:
> 
> A Java class extending jakarta.servlet.http.HttpServlet, responsible for processing HTTP requests in Java web apps.
> 
> Serves as a server-side module that processes requests, performs logic (e.g. database access, HTML generation), and returns a response.
> 
> Best practice: The servlet acts as a Controller, delegating business logic to other classes (separation of concerns, maintainability, testability).

>Tomcat:
>
>- An application server functioning as a Servlet container.
>
>Responsibilities:
>- Listening on HTTP ports (e.g. port 8080).
>- Interpreting .war files.
>- Running servlets.
>- Managing lifecycle, security, sessions, etc.

Implementation Steps:

- Refactor `ClimaController` to `ClimaServlet`, `inheriting javax.servlet.http.HttpServlet` so Tomcat recognizes it as a servlet.

- Package the app as a `.war` archive.

>.war (Web Archive):
>
>- Standard format for Java web applications (essentially a .zip with compiled classes, configs, HTML, CSS, JS, etc.).
>
Typical Maven structure:
````pgsql
(example.war)
/
├── index.html
└── WEB-INF/
     ├── web.xml
     ├── lib/
     └── classes/
````
---
## Step 3:

Test calling the Visual Crossing API.

Use the **Apache httpclient5** library to send HTTP requests.

Features:
- ✅ Send HTTP requests (GET, POST, etc.)
- ✅ Add headers
- ✅ Include parameters and body
- ✅ Handle responses (status, body, errors)

Example Base URL:

`https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/`

API Endpoints:

    /timeline/{location} – forecast queries.

    /timeline/{location}/{date1}/{date2} – queries for specific date ranges.

Current State:
- ✅ A servlet (ClimaServlet) serving /clima.
- ✅ A service class (ClimaService) fetching real data from the Visual Crossing Weather API.
- ✅ Project packaged as a .war, running in a Docker container with Tomcat.
- ✅ Raw JSON string response from the external API to the client.
---
## Step 4:

Implement DTO Classes:

- The API response is raw JSON (String).
- Use Jackson to map JSON → Java objects.
- Create classes with fields matching the JSON structure (with getters, setters, no-arg constructor).

#### Jackson (ObjectMapper):
| Method                       | Purpose                                  |
| ---------------------------- | ---------------------------------------- |
| readValue(json, Class.class) | JSON → Java object                       |
| writeValueAsString(object)   | Java object → JSON string                |
| writeValue(File, object)     | Java object → JSON file                  |
| registerModule(module)       | Enable extra support (e.g. Java 8 dates) |
| configure(...)               | Enable/disable features                  |

---
## Step 5:

- Improve performance with Redis caching.

Without Redis:

    Every request to your API → calls Visual Crossing → returns data → incurs latency and cost every time.

With Redis:

    Your API asks Redis: “Do I already have this forecast?”
    ✅ If yes: returns instantly (in-memory).
    ❌ If no: calls Visual Crossing, stores result in Redis (with TTL), then returns.

Flow:
`````csharp
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
`````
#### Java Code Needs:
- ✅ Connect to Redis (hostname, port).
- ✅ Check if key exists (GET).
- ✅ Store if missing (SET with EXPIRE).
- ✅ JSON ↔ Java object (Jackson).

Implementation Steps:

- #1: Run a Redis container:
> docker run --name redis-clima -p 6379:6379 -d redis
- #2: Add Jedis library for Java Redis client.
- #3: Create a service class to encapsulate Redis access: `RedisCacheService`.

---
## Step 6:

**Exception Handling:**

- External API may return error messages in the response body.
- Capture and return user-friendly error messages.
- Handle key HTTP status codes more precisely.

Implementation Steps:

- #1: Create a custom exception: ApiCallException (includes status code and message).
- #2: Refactor ClimaService and ClimaServlet to use this.

---
## Step 7:

**Rate Limiting:**

- Total daily limit of 1000 calls.
- Per-user IP hourly limit of 10 calls.

---
## Step 8:

**Run the application with Docker Compose:**

- Package the app as a .war: `*mvn clean package`
- Create a `Dockerfile` at the project root:
  - It contains instructions to build a Tomcat container with your `.war` deployed.
- Create a docker-compose.yml:
  - Define services and their relationships (Tomcat container, Redis container).
- Build and run: `docker-compose up --build`