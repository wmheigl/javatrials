package Transforms;

import org.jtransforms.fft.FloatFFT_2D;
import org.jtransforms.utils.CommonUtils;

import edu.mines.jtk.util.ArrayMath;

public class FFT_2D {

  //@formatter:off
    
  // found this at http://fourier.eng.hmc.edu/e101/lectures/Image_Processing/node6.html
  private static float[][] stump = new float[][] {
    { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f },
    { 0.0f, 0.0f, 70.0f, 80.0f, 90.0f, 0.0f, 0.0f, 0.0f },
    { 0.0f, 0.0f, 90.0f, 100.0f, 110.0f, 0.0f, 0.0f, 0.0f },
    { 0.0f, 0.0f, 110.0f, 120.0f, 130.0f, 0.0f, 0.0f, 0.0f },
    { 0.0f, 0.0f, 130.0f, 140.0f, 150.0f, 0.0f, 0.0f, 0.0f },
    { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f },
    { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f },
    { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f } };
    //@formatter:on

  public static void main(String[] args) {

    // get FFT lengths
    int rows = CommonUtils.nextPow2(stump.length);
    int cols = CommonUtils.nextPow2(stump[0].length);

    // set up work arrays
    float[][] rx = new float[rows][2 * cols];
    ArrayMath.copy(stump[0].length, stump.length, stump, rx);
    System.out.println("Input:");
    ArrayMath.dump(stump);

    // set up operators
    FloatFFT_2D fft = new FloatFFT_2D(rows, cols);

    // forward FFT
    fft.realForwardFull(rx);

    // in-place scaling for comparison with results on website
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < 2 * cols; c++) {
	rx[r][c] *= (float) 1.0 / rows;
      }
    }
    System.out.println("Real part:");
    ArrayMath.dump(ArrayMath.copy(rows, cols, 0, 0, 2, 1, rx));
    System.out.println("Imaginary part:");
    ArrayMath.dump(ArrayMath.copy(rows, cols, 1, 0, 2, 1, rx));

    // inverse FFT
    // remove scaling
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < 2 * cols; c++) {
	rx[r][c] *= rows;
      }
    }
    fft.complexInverse(rx, true);
    
    ArrayMath.copy(stump[0].length, stump.length, rx, stump);
    System.out.println("Recovered input:");
    ArrayMath.dump(stump);

  }

}
