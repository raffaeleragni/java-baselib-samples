import baselib.HttpServer;
import baselib.JSONBuilder;
import baselib.JdbcInstance;
import static java.lang.String.valueOf;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

public class Main {
  public static void main(String[] args) {
    new Main().main();
  }

  public record Table(String id, String name) {}

  void main() {
    System.setProperty("JDBC_URL", "jdbc:h2:mem:shenanigans;DB_CLOSE_DELAY=-1");
    var db = JdbcInstance.defaultClient();
    db.execute("create table if not exists test (id varchar(255), name varchar(255), primary key(id))", st -> {});

    var selector = db.makeRecordSelector(Table.class, "select id,name from test", st -> {});
    BiFunction<String, String, String> addNew = (id, name) -> valueOf(
        db.execute("insert into test (id, name) values(?, ?)", st -> {
          st.setString(1, id);
          st.setString(2, name);
        }));

    HttpServer.create(8080, HttpServer.of(Map.of(
      "/", c -> "test",
      "/get", c -> JSONBuilder.toJSON(selector.get()),
      "/insert", c -> addNew.apply(UUID.randomUUID().toString(), c.variablePath()),
      "/echo", c -> c.body()
    ))).start();

    System.out.println("Started server on port 8080");
  }
}
