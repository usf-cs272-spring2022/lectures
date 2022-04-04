package edu.usfca.cs272;

/**
 * Demonstrates basic boundary matching in regular expressions. Shows how the
 * MULTILINE (?m) and DOTALL (?s) flags changes the results.
 *
 * @see RegexHelper
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
public class RegexBoundaries extends RegexHelper {
	/**
	 * Demonstrates basic boundary matching in regular expressions.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) {
		// Example string for testing regular expressions.
		String text = "Knock knock!\nWho's there?";
		console.println(text);

		console.println();
		console.println("Input Boundary \\A and \\z");

		// .* will not match \n
		// since no single-line input, does not match anything
		printMatches(text, "\\A.*\\z");

		// .* will match \n
		// the (?s) flag enables matching \n when using "."
		printMatches(text, "(?s)\\A.*\\z");

		console.println();
		console.println("Line Boundary ^ and $");

		// .* will not match \n
		// []
		printMatches(text, "^.*$");

		// ^$ will look at individual lines
		// the (?m) flag enables multiline mode
		// [Knock knock!, Who's there?]
		printMatches(text, "(?m)^.*$");

		// .* will match \n
		// [Knock knock!\nWho's there?]
		printMatches(text, "(?s)^.*$");

		// greedy, matches everything
		// [Knock knock!\nWho's there?]
		printMatches(text, "(?ms)^.*$");

		// reluctant, matches each line
		// [Knock knock!, Who's there?]
		printMatches(text, "(?ms)^.*?$");
	}
}
