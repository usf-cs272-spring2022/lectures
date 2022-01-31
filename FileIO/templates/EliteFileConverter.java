package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;

public class EliteFileConverter {
	public static char toLeetSpeak(char letter) {
		boolean random = Math.random() < 0.5;

		// https://docs.oracle.com/en/java/javase/15/language/switch-expressions.html
		return switch (letter) {
			case 'a', 'A' -> '\0'; // TODO Implement this case
			case 'b', 'B' -> '8';
			case 'e', 'E' -> '3';
			case 'i', 'I' -> '!';
			case 'l', 'L' -> '1';
			case 'o', 'O' -> '0';
			case 's', 'S' -> random ? '5' : '$';
			case 't', 'T' -> '7';
			default -> '\0'; // TODO Implement this case
		};
	}

	public static String toLeetSpeak(String text, double threshold) {
		// TODO Implement toLeetSpeak(String, double) method
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public static String toLeetSpeak(String text) {
		// TODO Implement toLeetSpeak(String) method
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public static void toLeetSpeakMemoryIntensive(Path input, Path output)
			throws IOException {
		// TODO Implement toLeetSpeakMemoryIntensive method
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public static void toLeetSpeakMemoryIntensiveStream(Path input, Path output)
			throws IOException {
		// TODO Implement toLeetSpeakMemoryIntensiveStream method
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public static void toLeetSpeak(Path input, Path output) throws IOException {
		// TODO Implement toLeetSpeak(Path, Path) method
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public static void main(String[] args) throws Exception {
		// https://docs.oracle.com/en/java/javase/15/text-blocks/index.html
		String text = ""; // TODO Fill in value

		System.out.println(text);
		System.out.println(toLeetSpeak(text));
		System.out.println(toLeetSpeak(text, 0.25));
		System.out.println(toLeetSpeak(text, 1.00));

//		String filename = EliteFileConverter.class.getSimpleName();
//		Path base = Path.of("src", "main", "java", "edu", "usfca", "cs272");
//		Path input = base.resolve(filename + ".java");
//		Path output = Path.of("out", filename + ".txt");

//		Files.createDirectories(output.getParent());
//		toLeetSpeak(input, output);

//		Path nowhere = Path.of("nowhere");
//		toLeetSpeak(nowhere, nowhere);
	}
}
