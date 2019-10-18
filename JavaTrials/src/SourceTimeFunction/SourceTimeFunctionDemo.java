/**
 * SourceTimeFunction.java
 *
 * @author Werner M. Heigl
 * @version 2019.01.16
 */
package SourceTimeFunction;

import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import org.apache.commons.math3.util.MathArrays;
import edu.mines.jtk.dsp.Fft;
import edu.mines.jtk.dsp.Sampling;
import edu.mines.jtk.mosaic.AxisScale;
import edu.mines.jtk.mosaic.PlotFrame;
import edu.mines.jtk.mosaic.PlotFrame.Split;
import edu.mines.jtk.mosaic.PlotPanel;
import edu.mines.jtk.mosaic.PointsView;
import edu.mines.jtk.util.Cdouble;

/**
 * This class creates an interactive(?) display of time and frequency representations of Brune's
 * moment rate function, geophone response and their convolution.
 * 
 */
public final class SourceTimeFunctionDemo {

  /** Class name for logger initialization. */
  private static final String CLASS_NAME = SourceTimeFunctionDemo.class.getName();
  /** Logger for diagnostic information. */
  private static final Logger LOG = Logger.getLogger(CLASS_NAME);

  /** Plot panel holding the time domain representations. */
  private PlotPanel leftPanel;
  /** Plot panel holding the frequency domain representations. */
  private PlotPanel rightPanel;
  /** The geophone that would record the moment rate function. */
  private Geophone geophone;
  /** Moment rate function at the source point. */
  private MomentRate momentRate;
  /** Time sampling. */
  private Sampling timeSampling;
  /** Frequency sampling. */
  private Sampling freqSampling;
  /** Max. sample time. */
  private double maxSampleTime;
  /** Time sampling frequency. */
  private int sampleRate;
  /** FFT operator. */
  Fft fft;

  /**
   * Sole constructor.
   */
  public SourceTimeFunctionDemo() {
    LOG.setLevel(Level.INFO);

    geophone = new Geophone(52, 10, 0.7);
    momentRate = new MomentRate(1, 10, 1e6, 2500, 1800);

    // 1. Define the sampling
    maxSampleTime = 1.0; // in seconds
    sampleRate = 10000; // sampling frequency (per second)
    timeSampling = getTimeSampling(maxSampleTime, sampleRate);
    fft = new Fft(timeSampling);
    freqSampling = fft.getFrequencySampling1();
    LOG.info(getSamplingInfo());

    // 2. Compute time and frequency domain representations
    float[] brunePulse = getImpulseResponse(momentRate);
    float[] bruneAmpSpec = getAmplitudeSpectrum(momentRate);
    float[] geophonePulse = getImpulseResponse(geophone);
    float[] geophoneAmpSpec = getAmplitudeSpectrum(geophone);
    float[] convolutionPulse = getConvolutionPulse(momentRate, geophone);
    float[] convolutionAmpSpec = getConvolutionAmplitudeSpectrum(momentRate, geophone);

    // 3. Create the GUI components.
    leftPanel = new PlotPanel(3, 1);
    leftPanel.setTitle("Time Domain");
    leftPanel.setHLimits(0, 0.15);
    leftPanel.setHLabel("Time (s)");
    PointsView pv11 = leftPanel.addPoints(0, 0, timeSampling, brunePulse);
    PointsView pv21 = leftPanel.addPoints(1, 0, timeSampling, geophonePulse);
    PointsView pv31 = leftPanel.addPoints(2, 0, timeSampling, convolutionPulse);
    rightPanel = new PlotPanel(3, 1);
    rightPanel.setTitle("Frequency Domain");
    rightPanel.setHLabel("Frequency (Hz)");
    PointsView pv12 = rightPanel.addPoints(0, 0, freqSampling, bruneAmpSpec);
    pv12.setScales(AxisScale.LOG10, AxisScale.LOG10);
    PointsView pv22 = rightPanel.addPoints(1, 0, freqSampling, geophoneAmpSpec);
    pv22.setScales(AxisScale.LOG10, AxisScale.LOG10);
    PointsView pv32 = rightPanel.addPoints(2, 0, freqSampling, convolutionAmpSpec);
    pv32.setScales(AxisScale.LOG10, AxisScale.LOG10);

  }

  public static final int nearestPowerOfTwo(int n) {
    int num = 1;
    while (num < n) {
      num <<= 1;
    }
    return num;
  }

  public static void main(String[] args) {
    SourceTimeFunctionDemo stf = new SourceTimeFunctionDemo();
    PlotFrame frame = makeView(stf);
    frame.setVisible(true);
  }

  /**
   * Create the view.
   */
  private static PlotFrame makeView(SourceTimeFunctionDemo stf) {
    PlotFrame frame = new PlotFrame(stf.leftPanel, stf.rightPanel, Split.HORIZONTAL);
    frame.setSize(1300, 800);
    frame.setLocation(100, 0);
    frame.setTitle("Source Time Function");
    frame.setDefaultCloseOperation(PlotFrame.EXIT_ON_CLOSE);
    // frame.setJMenuBar(getMenuBar());
    return frame;
  };

  private static JMenuBar getMenuBar() {
    JMenu menu = new JMenu("A Menu");
    JMenuBar menuBar = new JMenuBar();
    menu.setMnemonic(KeyEvent.VK_A);
    // menu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has
    // menu items");
    menuBar.add(menu);
    return menuBar;
  }

  private float[] getConvolutionPulse(Filter filter1, Filter filter2) {
    double[] convolutionDbl = MathArrays.convolve(filter1.impulseResponse(timeSampling),
        filter2.impulseResponse(timeSampling)); // input * impulse_response
    float[] convolutionFlt = new float[timeSampling.getCount()];
    for (int i = 0; i < convolutionFlt.length; i++) {
      convolutionFlt[i] = (float) convolutionDbl[i];
    }
    return convolutionFlt;
  }

  private float[] getConvolutionAmplitudeSpectrum(Filter filter1, Filter filter2) {
    Cdouble[] tf1 = filter1.transferFunction(freqSampling);
    Cdouble[] tf2 = filter2.transferFunction(freqSampling);
    float[] ampSpec = new float[freqSampling.getCount()];
    for (int i = 0; i < ampSpec.length; i++) {
      ampSpec[i] = (float) Cdouble.mul(tf1[i], tf2[i]).abs();
    }
    return ampSpec;
  }

  private float[] getImpulseResponse(Filter filter) {
    double[] impRespDbl = filter.impulseResponse(timeSampling);
    float[] impRespFlt = new float[timeSampling.getCount()];
    for (int i = 0; i < timeSampling.getCount(); ++i) {
      impRespFlt[i] = (float) impRespDbl[i];
    }
    return impRespFlt;
  }

  private float[] getAmplitudeSpectrum(Filter filter) {
    double[] ampSpecDbl = filter.amplitudeSpectrum(freqSampling);
    float[] ampSpecFlt = new float[freqSampling.getCount()];
    for (int i = 0; i < freqSampling.getCount(); ++i) {
      ampSpecFlt[i] = (float) ampSpecDbl[i];
    }
    return ampSpecFlt;
  }

  private static Sampling getTimeSampling(double tmax, double fs) {
    int n = nearestPowerOfTwo((int) Math.round(tmax * fs));
    double dt = 1.0 / fs;
    return new Sampling(n, dt, 0);
  }

  private String getSamplingInfo() {
    String str = "Sampling --";
    str += "  maxSampleTime=" + maxSampleTime + "  sampleRate=" + sampleRate + "  nyq="
        + 0.5 * sampleRate + "\n";
    str += "                   nt=" + timeSampling.getCount() + "  dt=" + timeSampling.getDelta()
        + "  nf=" + freqSampling.getCount() + "  df=" + freqSampling.getDelta() + "\n";
    return str;
  }

}
