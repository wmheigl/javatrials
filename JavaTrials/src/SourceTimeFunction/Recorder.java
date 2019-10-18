/**
 * Recorder.java
 *
 * @version 2019.02.08
 * @author Werner M. Heigl
 * 
 */
package SourceTimeFunction;

import edu.mines.jtk.dsp.ButterworthFilter;
import edu.mines.jtk.dsp.ButterworthFilter.Type;
import edu.mines.jtk.util.Cdouble;

/**
 * This class implements the response of a recording instrument as a low-pass Butterworth filter.
 *
 */
public class Recorder implements Filter {

  /** The filter representing the response of the recorder. */
  private ButterworthFilter bwFilter;

  /**
   * Sole constructor.
   * 
   * @param fc The cutoff (half-power) frequency, in cycles per sample. At this cutoff frequency,
   *          the filter amplitude squared equals 0.5. The cutoff frequency must be greater than 0.0
   *          and less than 0.5. The value of 0.5 corresponds to Nyquist frequency.
   * @param np Number of poles in the recursive filter.
   */
  public Recorder(double fc, int np) {
    bwFilter = new ButterworthFilter(fc, np, Type.LOW_PASS);
  }

  @Override
  public double value(double t) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Cdouble spectrum(double w) {
    // TODO Auto-generated method stub
    return null;
  }

}
