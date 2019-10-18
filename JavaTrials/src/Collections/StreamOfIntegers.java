/**
 * StreamOfIntegers.java
 *
 * @author Werner M. Heigl
 * @version 2019.02.07
 */
package Collections;

import java.util.stream.IntStream;

/**
 * This Class illustrates how to partition arrays by creating lists of indices
 * arrays that provide access to the desired array elements.
 * 
 */
public class StreamOfIntegers {

	public static void main(String[] args) {

		final int groupSize = 2;
		final int n = 30;

		System.out.println("Even groups:");
		IntStream.range(0, n).filter(i -> (i / groupSize) % 2 == 0).forEach(System.out::println);

		System.out.println("Odd groups:");
		IntStream.range(0, n).filter(i -> (i / groupSize) % 2 == 1).forEach(System.out::println);
	}

}