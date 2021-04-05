package main;

import static baselib.http.HttpClient.get;
import baselib.storage.FSKV;
import baselib.http.HttpServer.Context;
import static baselib.json.JSONBuilder.toJSON;
import static baselib.json.JSONReader.toRecord;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@dagger.Module
public class Endpoints {

  @Provides @IntoMap @StringKey("/table-get")
  public Function<Context, String> tableGet(Supplier<List<Table>> selector) {
    return c -> toJSON(selector.get());
  }

  @Provides @IntoMap @StringKey("/table-insert")
  public Function<Context, String> tableInsert(Function<Table, String> addNew) {
    return c -> addNew.apply(toRecord(Table.class, c.body()));
  }

  @Provides @IntoMap @StringKey("/echo-body")
  public Function<Context, String> echoBody() {
    return c -> c.body();
  }

  @Provides @IntoMap @StringKey("/echo-path")
  public Function<Context, String> echoPath() {
    return c -> c.variablePath();
  }

  @Provides @IntoMap @StringKey("/kv-get")
  public Function<Context, String> kvGet(FSKV<Table> kv) {
    return c -> toJSON(kv.get(c.variablePath()));
  }

  @Provides @IntoMap @StringKey("/kv-put")
  public Function<Context, String> kcPut(FSKV<Table> kv) {
    return c -> {
      kv.put(c.variablePath(), toRecord(Table.class, c.body()));
      return "";
    };
  }

  @Provides @IntoMap @StringKey("/clientcall")
  public Function<Context, String> clientcall(FSKV<Table> kv) {
    return c -> get("http://wiremock:8080/").body();
  }
}
