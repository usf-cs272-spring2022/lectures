package edu.usfca.cs272;

/**
 * An example functional interface that performs an action.
 *
 * @see SimpleClass
 * @see LambdaDemo
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
@FunctionalInterface
public interface SimpleInterface {
	/** An example method. Does something. */
	public void simpleMethod();
}
