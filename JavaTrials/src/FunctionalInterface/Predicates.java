/**
 * Predicates.java
 *
 * @author Werner M. Heigl
 * @version 2018.11.28
 */
package FunctionalInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class explores the use of predicates in the filtering of streams.
 * 
 */
public class Predicates {

	private static Predicate<Double> isReal = d -> !d.isNaN() && !d.isInfinite();
	private static Predicate<Double> isPositive = d -> d >= 0;

	public static void main(String[] args) {

		List<Double> list = new ArrayList<Double>();
		list.add(-3.0);
		list.add(-2.0);
		list.add(Double.NEGATIVE_INFINITY);
		list.add(-1.0);
		list.add(0.0);
		list.add(Double.NaN);
		list.add(1.0);
		list.add(2.0);
		list.add(3.0);
		list.add(Double.POSITIVE_INFINITY);

		List<Double> filteredList = list.stream().filter(isReal).collect(Collectors.toList());
		double average = filteredList.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
		List<Double> positive = list.stream().filter(isReal.and(isPositive)).collect(Collectors.toList());

		System.out.println("Original list = " + list);
		System.out.println("Filtered list = " + filteredList);
		System.out.println("Average = " + average);
		System.out.println("Positive = " + positive);
	}

}
