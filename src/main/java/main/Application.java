package main;

import baselib.HttpServer;
import dagger.Component;

@Component(modules = {AppConfig.class, Endpoints.class})
public interface Application {
  HttpServer server();

  public static void main(String[] args) {
    var ns = System.currentTimeMillis();
    DaggerApplication.create().server().start();
    ns = System.currentTimeMillis() - ns;
    System.out.println("Started on port 8080 in "+ns+"ms");
  }
}
