/**
 * Tensors.java
 *
 * @author Werner M. Heigl
 * @version 2020.07.20
 */
package OnnxRuntime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ai.onnxruntime.NodeInfo;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.TensorInfo;
import ai.onnxruntime.OrtSession.SessionOptions;
import ai.onnxruntime.OrtSession.SessionOptions.OptLevel;

/**
 * Illustrates how to construct ONNX tensors and how the dimensions of multi-dimensional primitive arrays determine the
 * shape of the tensor.
 * <p>
 * ONNX tensors are equivalent to arrays of 3D arrays where each 3D array is a 2D array of vectors and each vector
 * represents a color such as RGB. If one works with raw data instead of colors the vector consists of only one element.
 * 
 */
public class Tensors {

  private static final String MODEL = "/home/werner/ML_Data/CNN_Model_67.onnx";

  /**
   * @param args A dummy argument.
   * @throws OrtException if the Onnx runtime throws an exception
   */
  public static void main(String[] args) throws OrtException {

    // Onnx runtime setup
    OrtEnvironment env = OrtEnvironment.getEnvironment();
    OrtSession.SessionOptions opts = new SessionOptions();
    opts.setOptimizationLevel(OptLevel.BASIC_OPT);
    OrtSession session = env.createSession(MODEL, opts);

              // 1D
    float[] ar1 = { 1.0f, 2.0f, 3.0f, 4.0f, 5.0f };
    OnnxTensor t1 = OnnxTensor.createTensor(env, ar1);
    System.out.println("t1.shape: " + t1.getInfo());

    // 2D
    float[][] ar2 = { ar1, ar1 };
    OnnxTensor t2 = OnnxTensor.createTensor(env, ar2);
    System.out.println("t2.shape: " + t2.getInfo());

    // 3D
    float[][][] ar3 = { { ar1, ar1 } };
    OnnxTensor t3 = OnnxTensor.createTensor(env, ar3);
    System.out.println("t3.shape: " + t3.getInfo());

    // 4D
    float[][][][] ar4 = { { { ar1, ar1 } } };
    OnnxTensor t4 = OnnxTensor.createTensor(env, ar4);
    System.out.println("t4.shape: " + t4.getInfo());

    // a 2D data array wrapped into a rank-3 tensor
    float[][][] ar5 = new float[ar2.length][ar2[0].length][1];
    for (int i = 0; i < ar5.length; i++) {
      for (int j = 0; j < ar5[0].length; j++) {
	ar5[i][j] = new float[] { ar2[i][j] };
      }
    }
    OnnxTensor t5 = OnnxTensor.createTensor(env, ar5);
    System.out.println("t5.shape: " + t5.getInfo());
    
  }

}
