package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

public class ListingBenchmark {
	public static final int WARMUP_ROUNDS = 10;
	public static final int TIMED_ROUNDS = 20;

	public static void main(String[] args) throws IOException, InterruptedException {
		Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.OFF);

		Path test = Path.of(".."); // TODO
		Set<Path> expected = SerialDirectoryListing.list(test);

		List<Benchmarker> benchmarks = new ArrayList<>();

		benchmarks.add(new Benchmarker("Single") {
			@Override
			public Set<Path> run(Path path) throws IOException {
				return SerialDirectoryListing.list(test);
			}
		});

		benchmarks.add(new Benchmarker("Slow") {
			@Override
			public Set<Path> run(Path path) throws InterruptedException {
				return SlowDirectoryListing.list(test);
			}
		});

//		benchmarks.add(new Benchmarker("Threaded") {
//			@Override
//			public Set<Path> run(Path path) throws InterruptedException {
//				return ThreadDirectoryListing.list(test);
//			}
//		});
//
//		benchmarks.add(new Benchmarker("Queue") {
//			@Override
//			public Set<Path> run(Path path) throws InterruptedException {
//				return FastDirectoryListing.list(test);
//			}
//		});
//
//		benchmarks.add(new Benchmarker("Executor") {
//			@Override
//			public Set<Path> run(Path path) throws InterruptedException {
//				return ExecutorDirectoryListing.list(test);
//			}
//		});

		Collections.shuffle(benchmarks);

		for (Benchmarker current : benchmarks) {
			current.benchmark(test, expected);
		}

		Collections.sort(benchmarks);

		String order = benchmarks.stream().map(benchmark -> benchmark.name)
				.collect(Collectors.joining(" > "));

		System.out.println();
		System.out.println(order);
	}

	private static abstract class Benchmarker implements Comparable<Benchmarker> {
		public String name;
		public double average;

		public Benchmarker(String name) {
			this.name = name;
			this.average = 0;
		}

		public abstract Set<Path> run(Path directory) throws IOException, InterruptedException;

		public void benchmark(Path directory, Set<Path> expected) throws InterruptedException, IOException {
			System.out.print(String.format("%9s: ", name));

			Set<Path> actual = run(directory);

			if (!actual.equals(expected)) {
				System.err.printf("Unexpected results! Expected %d elements, found %d elements.",
						expected.size(), actual.size());
			}

			for (int i = 0; i < WARMUP_ROUNDS; i++) {
				actual.addAll(run(directory));
			}

			Instant start = Instant.now();
			for (int i = 0; i < TIMED_ROUNDS; i++) {
				actual.addAll(run(directory));
			}
			Instant end = Instant.now();

			Duration elapsed = Duration.between(start, end);
			average = (double) elapsed.toMillis() / TIMED_ROUNDS;
			System.out.printf("%8.2fms%n", average);
		}

		@Override
		public int compareTo(Benchmarker other) {
			return Double.compare(this.average, other.average);
		}
	}
}
