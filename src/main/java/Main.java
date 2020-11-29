import baselib.HttpServer;
import baselib.JSONBuilder;
import baselib.JdbcInstance;
import static java.lang.String.valueOf;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

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
    Function<String, String> addNew = id -> valueOf(
        db.execute("insert into test (id, name) values(?, 'blah')", st -> st.setString(1, id)));

    HttpServer.create(8080, HttpServer.of(Map.of(
      "/", () -> "test",
      "/get", () -> JSONBuilder.toJSON(selector.get()),
      "/insert", () -> addNew.apply(UUID.randomUUID().toString())
    ))).start();

    System.out.println("Started server on port 8080");
  }
}
