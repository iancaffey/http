# http ![](https://img.shields.io/github/license/mashape/apistatus.svg) [![Build Status](https://travis-ci.org/iitc/http.svg?branch=master)](https://travis-ci.org/iitc/http)
Java http server and web application framework.

## Example

```java
    HttpServer server = new HttpServer(8080);
    server.accept(new Controller() {
        @Route(pattern = "/?id=([0-9]+)")
        public Response id(int id) {
            return Response.ok("<title>:4head:</title>" + "<p>Id: " + id + "</p>" + System.currentTimeMillis());
        }
    });
```

```java
    HttpServer server = new HttpServer(8080);
    Response response = Response.ok("<title>:4head:</title>" + "<p>Fuck you. :kappa:</p>" + System.currentTimeMillis());
    Router router = Router.of(RoutingTable.singleton("GET", URIPattern.compile("/?id=[0-9]+"), response));
    server.accept(router);
```

```java
    HttpServer server = new HttpServer(8080);
    server.accept(Router.of(RoutingTable.singleton("GET", URIPattern.compile("/?id=[0-9]+"), (writer) -> {
        StringBuilder builder = new StringBuilder();
        builder.append("<title>:4head:</title>").
                append("<p>Fuck you. :kappa:</p>").
                append(System.currentTimeMillis());
        writer.printHeader("HTTP/1.0 200 OK");
        writer.printDate(Instant.now());
        writer.printServer("http/1.0");
        writer.printContentType("text/html");
        writer.printContentLength(builder.toString().getBytes().length);
        writer.printExpiration(null);
        writer.printLastModified(null);
        writer.endHeader();
        writer.print(builder);
    })));
```