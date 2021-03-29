package main;

import baselib.http.HttpServer;
import dagger.Component;

@Component(modules = {AppConfig.class, Endpoints.class})
public interface Application {
  HttpServer server();
  public static HttpServer createServer() {
    return DaggerApplication.create().server();
  }

  public static void main(String[] args) {
    var ms = System.currentTimeMillis();
    createServer().start();
    ms = System.currentTimeMillis() - ms;
    System.out.println("Started on port 8080 in "+ms+"ms");
  }
}
