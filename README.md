# http ![](https://img.shields.io/github/license/mashape/apistatus.svg) [![Build Status](https://travis-ci.org/iitc/http.svg?branch=master)](https://travis-ci.org/iitc/http)
Java http server and web application framework.

## Example

```java
    HttpServer server = new HttpServer(8080);
    server.accept(Router.of(RoutingTable.singletonPatternRoute("GET", URIPattern.compile("/?id=[0-9]+"), (writer) -> {
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