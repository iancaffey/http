# http ![](https://img.shields.io/github/license/mashape/apistatus.svg) [![Build Status](https://travis-ci.org/iitc/http.svg?branch=master)](https://travis-ci.org/iitc/http)
Java http server and web application framework.

## Example

```java
    HttpServer server = HttpServer.bind(8080);
    server.accept(Controller.class);
    server.listen();
    ...    
    public static class Controller {
        @Get("/")
        public static HttpHandler index() {
            return Response.code(ResponseCode.OK);
        }
    }
```