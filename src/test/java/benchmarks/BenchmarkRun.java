package benchmarks;

/*
 * Copyright 2021 Raffaele Ragni.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 *
 * @author Raffaele Ragni
 */
public class BenchmarkRun {
  public static final void run(Class<?> clazz) {
    try {
      new Runner(new OptionsBuilder()
        .include(clazz.getName() + ".*")
        .mode(Mode.AverageTime)
        .timeUnit(TimeUnit.NANOSECONDS)
        .warmupTime(TimeValue.seconds(1))
        .warmupIterations(2)
        .measurementTime(TimeValue.seconds(1))
        .measurementIterations(5)
        .threads(2)
        .forks(1)
        .shouldFailOnError(true)
        .shouldDoGC(true)
        .build())
        .run();
    } catch (RunnerException ex) {
      throw new RuntimeException(ex.getMessage(), ex);
    }
  }
}
