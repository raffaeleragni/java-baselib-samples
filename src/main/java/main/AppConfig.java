package main;

import static baselib.ExceptionWrapper.ex;
import baselib.storage.FSKV;
import baselib.http.HttpServer;
import baselib.jdbc.JdbcInstance;
import dagger.Provides;
import static java.lang.String.valueOf;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@dagger.Module
public class AppConfig {

  public @Provides Supplier<Connection> connectionSupplier() {
    return () -> ex(() -> DriverManager.getConnection("jdbc:h2:mem:shenanigans;DB_CLOSE_DELAY=-1"));
  }

  public @Provides JdbcInstance jdbcInstance(Supplier<Connection> connectionSupplier) {
    var db = new JdbcInstance(connectionSupplier);
    db.execute("create table if not exists test (id varchar(255), name varchar(255), primary key(id))", st -> {});
    return db;
  }

  public @Provides Supplier<List<Table>> tableSelector(JdbcInstance db) {
    return db.makeRecordSelector(Table.class, "select id,name from test", st -> {});
  }

  public @Provides Function<Table, String> tableAdder(JdbcInstance db) {
    return t -> valueOf(
      db.execute("insert into test (id, name) values(?, ?)", st -> {
        st.setString(1, t.id());
        st.setString(2, t.name());
      }));
  }

  public @Provides FSKV<Table> tableFSKV() {
    return new FSKV<>(Paths.get("tempdir"), Table.class);
  }

  public @Provides HttpServer httpServer(Map<String, Function<HttpServer.Context, String>> endpoints) {
    System.setProperty("sun.net.httpserver.maxReqTime", "1000");
    return HttpServer.create(8080, 10, endpoints);
  }
}
