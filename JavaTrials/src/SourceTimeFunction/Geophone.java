/**
 * Geophone.java
 *
 * @version 2019.02.08
 * @author Werner M. Heigl
 */
package SourceTimeFunction;

import edu.mines.jtk.util.Cdouble;

/**
 * This class encapsulates geophones, i.e. motion sensors that are based on the motion of an
 * inertial mass relative to the motion of its casing (and thus ground motion if the casing is
 * coupled to the ground). The motion of the inertial mass is measured via a current induced in a
 * coil that actually makes up the inertial mass. According to Farady's law the voltage appearing at
 * the geophone's terminals is proportional to the inertial mass' velocity.
 */
public class Geophone implements Filter {

  /** Sensitivity (in V/(m/s). */
  private double sensitivity;
  /** Natural frequency (in radian). */
  private double naturalFrequency;
  /** Damping constant squared. */
  private double dampingConstant;

  /**
   * Sole constructor.
   * 
   * @param sensitivity Sensitivity or gain constant (in V/m/s)
   * @param naturalFrequency Natural frequency (in Hz)
   * @param dampingConstant Damping constant (as a fraction of critical damping)
   */
  public Geophone(double sensitivity, double naturalFrequency, double dampingConstant) {
    this.sensitivity = sensitivity;
    this.naturalFrequency = 2 * Math.PI * naturalFrequency;
    this.dampingConstant = dampingConstant;
  }

  @Override
  public double value(double t) {
    double val = 0;
    double w0 = naturalFrequency;
    double h = dampingConstant;
    double arg = Math.sqrt(2 - 2 * h * h) * t * w0 / Math.sqrt(2);
    double term1 = Math.sqrt(2 - 2 * h * h) * Math.sqrt(2) * Math.PI * (2 * h * h - 1) * w0
        * Math.sin(arg);
    double term2 = 4 * Math.PI * h * (h * h - 1) * w0 * Math.cos(arg);
    val = sensitivity * Math.exp(-h * t * w0) * (term1 + term2) / (2 * Math.PI * (h * h - 1));
    return val;
  }

  @Override
  public Cdouble spectrum(double w) {
    double w0 = naturalFrequency;
    double h = dampingConstant;
    double denom = (w0 * w0 - w * w) * (w0 * w0 - w * w) + 4 * h * h * w * w * w0 * w0;
    double real = sensitivity * w * w * (w0 * w0 - w * w) / denom;
    double imag = -sensitivity * 2 * h * w * w * w * w0 / denom;
    return new Cdouble(real, imag);
  }

}
