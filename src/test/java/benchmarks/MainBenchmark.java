package benchmarks;

import org.openjdk.jmh.annotations.Benchmark;

public class MainBenchmark {
  public static void main(String[] args) {
    BenchmarkRun.run(MainBenchmark.class);
  }

  @Benchmark
  public void runConstr() {
  }
}
