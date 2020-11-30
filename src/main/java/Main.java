import baselib.HttpServer;
import baselib.HttpServer.Context;
import static baselib.JSONBuilder.toJSON;
import static baselib.JSONReader.toRecord;
import baselib.JdbcInstance;
import baselib.MetricsExporter;
import static java.lang.String.valueOf;
import java.util.Map;
import java.util.function.Function;

public class Main {
  public static void main(String[] args) {
    new Main().main();
  }

  public record Table(String id, String name) {}

  JdbcInstance prepareDB() {
    System.setProperty("JDBC_URL", "jdbc:h2:mem:shenanigans;DB_CLOSE_DELAY=-1");
    var db = JdbcInstance.defaultClient();
    db.execute("create table if not exists test (id varchar(255), name varchar(255), primary key(id))", st -> {});
    return db;
  }

  void runServer(Map<String, Function<Context, String>> endpoints) {
    System.setProperty("sun.net.httpserver.maxReqTime", "1000");

    HttpServer.create(8080, 10, HttpServer.of(endpoints)).start();
    System.out.println("Started server on port 8080");
  }

  void main() {
    var db = prepareDB();
    var selector = db.makeRecordSelector(Table.class, "select id,name from test", st -> {});
    Function<Table, String> addNew = t -> valueOf(
      db.execute("insert into test (id, name) values(?, ?)", st -> {
        st.setString(1, t.id());
        st.setString(2, t.name());
      }));

    runServer(Map.of(
      "/", c -> "test",
      "/get", c -> toJSON(selector.get()),
      "/insert", c -> addNew.apply(toRecord(Table.class, c.body())),
      "/echo", c -> c.body(),
      "/metrics", c -> {
        c.writer(out -> MetricsExporter.DEFAULT.export(out));
        return "";
      }
    ));
  }
}
