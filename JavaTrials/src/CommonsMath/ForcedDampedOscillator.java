/**
 * ForcedDampedOscillator.java
 *
 * @author Werner M. Heigl
 * @version 2019.03.27
 */
package CommonsMath;

import java.util.Arrays;

import javax.swing.SwingUtilities;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.ContinuousOutputModel;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math3.ode.nonstiff.GraggBulirschStoerIntegrator;
import org.apache.commons.math3.ode.nonstiff.HighamHall54Integrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.util.FastMath;

import edu.mines.jtk.mosaic.SimplePlot;

//@formatter:off
/**
 * Implements the damped harmonic oscillator with external forcing as a system of two first-order
 * ordinary differential equations.
 * <p>
 * This was motivated by equation 2 of
 * <p>
 * Erol Kalkan (2016), An Automatic P‐Phase Arrival‐Time Picker. Bulletin of the Seismological
 * Society of America, v. 106(3), p. 971-986, <a href="https://doi.org/10.1785/0120150111"
 * >https://doi.org/10.1785/0120150111</a>.
 * <p>
 * See also <a href=
 * "https://www.mathworks.com/matlabcentral/fileexchange/70343-an-automated-s-phase-arrival-time-picker-with-snr-output"
 * >MATLAB Central File Exchange</a>
 *
 */
// @formatter:on
public class ForcedDampedOscillator implements FirstOrderDifferentialEquations {

  /** Dimension of the problem. */
  private int n;
  /** Natural frequency. */
  private double omegan;
  /** Damping term. */
  private double c;
  /** Stiffness term. */
  private double k;
  /** Damping constant. */
  private double xi;

  /**
   * Sole constructor.
   * 
   * @param xi damping constant
   * @param tn natural period
   */
  public ForcedDampedOscillator(final double xi, final double tn) {
	assert tn != 0.0 : "Natural period tn must not be zero";
	omegan = 2.0 * FastMath.PI / tn;
	this.xi = xi;
	c = 2.0 * xi * omegan;
	k = omegan * omegan;
	n = 2;
  }

  @Override
  public int getDimension() {
	return n;
  }

  @Override
  public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException,
	  DimensionMismatchException {
	double arg = 2.0 * FastMath.PI * 5.0 * t;
	yDot[0] = y[1];
	yDot[1] = -k * y[0] - c * y[1] - FastMath.sin(arg); // last term is external forcing
  }


  public static void main(String[] args) {

	SwingUtilities.invokeLater(createAndShowPlot);

  }

  private static Runnable createAndShowPlot = () -> {
	double xi = 0.6; // damping constant
	double tn = 0.01; // natural period
	double omegan = 2.0 * FastMath.PI / tn;
	double[] y0 = new double[] { 0.0, 0.0 }; // initial state: [u(0), uDot(0)]
	double[] y = new double[y0.length];
	double t0 = 0.0; // initial condition
	double t1 = 2.0; // final condition
	int n = 1000;
	
	FirstOrderDifferentialEquations ode = new ForcedDampedOscillator(xi, tn);
//	FirstOrderIntegrator itg = new HighamHall54Integrator(0, 1, 1e-10, 1e-10);
	FirstOrderIntegrator itg = new DormandPrince54Integrator(0, 1.0, 1.0e-10, 1.0e-10);
//	FirstOrderIntegrator itg = new DormandPrince853Integrator(0, 1.0, 1.0e-10, 1.0e-10);
//	FirstOrderIntegrator itg = new GraggBulirschStoerIntegrator(0, 1, 1e-10, 1e-10);
	ContinuousOutputModel stepHandler = new ContinuousOutputModel();
	stepHandler.init(t0, y0, t1);
	itg.addStepHandler(stepHandler);
	itg.integrate(ode, t0, y0, t1, y); // now y contains final state at time t=t1
	System.out.println("State vector after " + t1 + " s : " + Arrays.toString(y));
	System.out.println("Evaluations: " + itg.getEvaluations());
	
	double dt = (t1 - t0) / n;
	double[] t = new double[n];
	double[] yCont = new double[n];
	double[] Edi = new double[n];
	for (int i = 0; i < n; i++) {
	  stepHandler.setInterpolatedTime(t0 + i * dt);
	  t[i] = stepHandler.getInterpolatedTime();
	  yCont[i] = stepHandler.getInterpolatedState()[0];
	  double yDot = stepHandler.getInterpolatedState()[1];
	  Edi[i] = 2 * xi * omegan * yDot * yDot;
	}
	SimplePlot.asPoints(t, yCont);
  };

}
