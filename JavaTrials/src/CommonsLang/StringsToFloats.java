/**
 * StringsToFloats.java
 *
 * @author Werner M. Heigl
 * @version 2020.07.14
 */
package CommonsLang;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Illustrates how to parse a CSV string into a float array without using for loops.
 * 
 */
public class StringsToFloats {

  private static final String str = "1.1,2.2,3.3,4.4,5.5,6.6,7.7,8.8,9.9,10.1";

  /**
   * @param args A dummy argument.
   */
  public static void main(String[] args) {

    String[] tokens = str.split(",");
    
    List<Float> valuesList = Arrays.stream(tokens).map(Float::parseFloat).collect(Collectors.toList());
    
    float[] f = ArrayUtils.toPrimitive(valuesList.toArray(new Float[0]));

  }

}
