/**
 * StreamOfFloats.java
 *
 * @version 2019.02.19
 * @author Werner M. Heigl
 */
package Collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import edu.mines.jtk.util.ArrayMath;

/**
 * This class illustrates how to process float arrays with the Stream API. In general, float arrays
 * need to be converted to double before processing and back to float arrays afterwards.
 *
 */
public class StreamOfFloats {

  private static final String CLASS_NAME = StreamOfFloats.class.getName();
  private static final Logger LOG = Logger.getLogger(CLASS_NAME);

  private static float[] ar1 = new float[] { 1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f };
  private static float[] ar2 = new float[] { 2.1f, 2.2f, 2.3f, 2.4f, 2.5f, 2.6f, 2.7f, 2.8f, 2.9f };
  private static float[] ar3 = new float[] { 3.1f, 3.2f, 3.3f, 3.4f, 3.5f, 3.6f, 3.7f, 3.8f, 3.9f };

  public static void main(String[] args) {

    LOG.setLevel(Level.INFO);

    // 1. Using an IntStream
    LOG.info("Using an IntStream on array1...");
    LOG.info("array1=" + Arrays.toString(ar1));
    IntStream.range(0, ar1.length).mapToDouble(i -> ar1[i]).forEach(System.out::println);

    // 2. Using a Supplier
    LOG.info("\nUsing Supplier<Double> on array2...");
    LOG.info("array2=" + Arrays.toString(ar2));
    int[] index = { 0 };
    Supplier<Double> doubleFromFloat = () -> {
      double current = ar2[index[0]];
      index[0] += 1;
      return current;
    };
    Stream.generate(doubleFromFloat).limit(ar1.length).forEach(System.out::println);

    // 3. Via conversion to String (preserves precision)
    LOG.info("\nUsing Supplier<String> on arra3 to preserve precision...");
    LOG.info("array3=" + Arrays.toString(ar3));
    index[0] = 0;
    Supplier<String> stringFromFloat = () -> {
      String current = Float.toString(ar3[index[0]]);
      index[0] += 1;
      return current;
    };
    Stream.generate(stringFromFloat).mapToDouble(Double::parseDouble).limit(ar3.length)
        .forEach(System.out::println);

    // 4. Streaming an array of float[] with parallel processing
    float[][] ar2d = { ar1, ar2, ar3 };
    LOG.info("\nStreaming an array of float[] with parallel squaring the elements...");
    LOG.info("ar2d=" + Arrays.deepToString(ar2d));
    index[0] = 0;
    Supplier<float[]> floatArray = () -> {
      float[] current = ar2d[index[0]];
      index[0] += 1;
      return current;
    };
    ArrayList<float[]> input = (ArrayList<float[]>) Stream.generate(floatArray).limit(ar2d.length)
        .collect(Collectors.toList());
    UnaryOperator<float[]> square = ar -> {
      float[] result = ArrayMath.mul(ar, ar);
      return result;
    };
    ArrayList<float[]> output = (ArrayList<float[]>) input.parallelStream().map(square)
        .collect(Collectors.toList());
    output.stream().forEach(ar -> System.out.println(Arrays.toString(ar)));
  }

}
