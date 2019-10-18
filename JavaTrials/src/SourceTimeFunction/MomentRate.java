/**
 * MomentRate.java
 *
 * @version 2019.02.08
 * @author Werner M. Heigl
 */
package SourceTimeFunction;

import edu.mines.jtk.util.Cdouble;

/**
 * This class computes Brune's moment rate function and amplitude spectrum. Its purpose is to
 * provide test data for the classes {@link SpectrumEstimator} and {@link SourceParameters}.
 * <p>
 * References:
 * <ul>
 * <li>Brune, J. N., "Tectonic stress and the spectra of seismic shear waves from earthquakes", J.
 * Geophys. Res., 1970, v. 75, p. 4997-5009.
 * <li>Brune, J. N., "Correction", J. Geophys. Res., 1971, v. 76, p. 5002.
 * </ul>
 */
public final class MomentRate implements Filter {

  private static final double RADIATION_COEFFICIENT = 0.63;
  private static final double KAPPA = 0.37;

  /** Shear modulus (in Pa). */
  private final double shearModulus;
  /** Travel time of shear wave from source to receiver (in seconds). */
  private final double travelTime;
  /** Seismic moment (in Nm). */
  private final double seismicMoment;
  /** Corner frequency (in Hz). */
  private final double cornerFrequency;
  /** Low-frequency limit (in (m/s)/Hz). */
  private final double lowFrequencyLimit;
  /** Scale. */
  private final double scale;

  /**
   * Sole constructor.
   * 
   * @param offset Distance between source point and receiver (in meter).
   * @param sourceRadius Radius of circular slippage area (in meter).
   * @param stressDrop Change in stress on the slippage area (in Pascal).
   * @param density Mass density of the medium surrounding the slippage area (in kg/m<sup>3</sup>).
   * @param shearVelocity Shear velocity of the medium surrounding the slippage area (in m/s).
   * @return A new instance of MomentRate.
   */
  public MomentRate(double offset, double sourceRadius, double stressDrop, double density,
      double shearVelocity) {

    shearModulus = density * shearVelocity * shearVelocity;
    travelTime = offset / shearVelocity;
    seismicMoment = 16 * sourceRadius * sourceRadius * sourceRadius * stressDrop / 7;
    cornerFrequency = 2 * Math.PI * KAPPA * shearVelocity / sourceRadius;
    scale = RADIATION_COEFFICIENT * stressDrop * shearVelocity * sourceRadius
        / (shearModulus * offset);
    lowFrequencyLimit = scale / Math.pow(2 * Math.PI * cornerFrequency, 2);
  }

@Override
  public double value(double x) {
    double val = 0;
    if (x >= travelTime) {
      // val = scale * (x - travelTime) * Math.exp(-cornerFrequency * (x - travelTime));
      val = scale * (-cornerFrequency * (x - travelTime) + 1)
          * Math.exp(-cornerFrequency * (x - travelTime));
    }
    return val;
  }

@Override
public Cdouble spectrum(double x) {
    final double wc = cornerFrequency;
    double denom = x * x + wc * wc;
    denom *= denom;
    double real = -x
        * ((x * x - wc * wc) * Math.sin(travelTime * x) - 2 * wc * x * Math.cos(travelTime * x))
        / denom;
    double imag = -x
        * (2 * wc * x * Math.sin(travelTime * x) + (x * x - wc * wc) * Math.cos(travelTime * x))
        / denom;
    return new Cdouble(scale * real, scale * imag);
  }

}
