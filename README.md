# http ![](https://img.shields.io/github/license/mashape/apistatus.svg) [![Build Status](https://travis-ci.org/iancaffey/http.svg?branch=master)](https://travis-ci.org/iancaffey/http)
Java http server and web application framework.

## Example

### Basic route
```java
    HttpServer server = HttpServer.bind(8080);
    server.accept(new Controller() {
        @Get("/")
        public HttpHandler index() {
            return ok();
        }
    });
    server.listen();
```
### Parameterized route
```java
    HttpServer server = HttpServer.bind(8080);
    server.accept(new Controller() {
        @Get("/user/{username}")
        public HttpHandler index(String username) {
            return ok();
        }
    });
    server.listen();
```
Keep in mind, annotation-based routes can be limiting, as the only supported pattern for parameters is `.+`. 
Using the Router directly provides full support for custom regex, parameter ordering, and delegation to any instantiated class and its methods.
