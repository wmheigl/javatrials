/**
 * Butterworth.java
 *
 * @author Werner M. Heigl
 * @version 2019.02.08
 */
package Filters;

import javax.swing.SwingUtilities;
import edu.mines.jtk.dsp.ButterworthFilter;
import edu.mines.jtk.dsp.ButterworthFilter.Type;
import edu.mines.jtk.mosaic.SimplePlot;

public class Butterworth {

  public static void main(String[] args) {

    SwingUtilities.invokeLater(createAndShowPlot);

  }

  private static Runnable createAndShowPlot = () -> {
    float[] input = new float[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    float[] output = new float[input.length];
    ButterworthFilter bfilt = new ButterworthFilter(0.4, 5, Type.LOW_PASS);
    bfilt.applyForwardReverse(input, output);
    SimplePlot.asSequence(output);
  };

}
