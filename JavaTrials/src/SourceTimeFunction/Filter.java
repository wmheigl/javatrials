/**
 * Filter.java
 * 
 * @author Werner M. Heigl
 * @version 2019.02.08
 */
package SourceTimeFunction;

import java.util.stream.DoubleStream;
import edu.mines.jtk.dsp.Sampling;
import edu.mines.jtk.util.Cdouble;

/**
 * Filter encapsulates the behavior of elements of a linear system.
 *
 */
public interface Filter {

  /**
   * Evaluates the impulse response at time t.
   * 
   * @param t Time in seconds.
   * @return Impulse response evaluated at time {@code t}.
   */
  abstract double value(double t);
  
  /**
   * Evaluates the transfer function at frequency w.
   * 
   * @param w Frequency in radian.
   * @return Transfer function evaluated at frequency {@code w}.
   */
  abstract Cdouble spectrum(double w);
  
  /**
   * The impulse response of the filter.
   * 
   * @param t Time sampling (time is in seconds).
   * @return The impulse response, discretely sampled according to {@code t}.
   */
  public default double[] impulseResponse(Sampling t) {
    double[] ir = DoubleStream.of(t.getValues()).map(d -> value(d)).toArray();
    return ir;
  };
  
  /**
   * The transfer function of the filter.
   * 
   * @param f Frequency sampling (frequency is in Hertz).
   * @return The (complex) transfer function, discretely sampled according to {@code f}.
   */
  public default Cdouble[] transferFunction(Sampling f) {
    Cdouble[] tf = new Cdouble[f.getCount()];
    for (int i = 0; i < f.getCount(); ++i) {
      tf[i] = spectrum(2 * Math.PI * f.getValue(i));
    }
    return tf;
  };

  /**
   * The amplitude spectrum of the transfer function of the filter.
   * 
   * @param f Frequency sampling (frequency is in Hertz).
   * @return The amplitude spectrum.
   */
  public default double[] amplitudeSpectrum(Sampling f) {
    Cdouble[] tf = transferFunction(f);
    double[] ampSpec = new double[f.getCount()];
    for (int i = 0; i < f.getCount(); ++i) {
      ampSpec[i] = Cdouble.abs(tf[i]);
    }
    return ampSpec;
  }
}
