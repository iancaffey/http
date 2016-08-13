# http ![](https://img.shields.io/github/license/mashape/apistatus.svg) [![Build Status](https://travis-ci.org/iitc/http.svg?branch=master)](https://travis-ci.org/iitc/http)
Java http server and web application framework.

## Example

```java
    HttpServer server = new HttpServer(8080);
    server.accept(new Controller() {
        @Route(pattern = "/?id=([0-9]+)")
        public Response id(int id) {
            return Response.ok("<title>HTTP Test</title><p>Id: " + id + "</p>");
        }
    });
```

```java
    HttpServer server = new HttpServer(8080);
    Response response = Response.ok("<title>HTTP Test</title><p>Test</p>");
    RoutingTable table = RoutingTable.singleton(Request.GET, "/", response);
    server.accept(Router.of(table));
```