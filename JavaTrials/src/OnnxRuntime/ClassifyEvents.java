/**
 * ClassifyEvents.java
 *
 * @author Werner M. Heigl
 * @version 2020.07.16
 */
package OnnxRuntime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ai.onnxruntime.NodeInfo;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.SessionOptions;
import ai.onnxruntime.OrtSession.SessionOptions.OptLevel;
import ai.onnxruntime.TensorInfo;
import hdf.hdf5lib.*;
import hdf.hdf5lib.exceptions.HDF5Exception;

/**
 * Midkiff event classification with Ben's CNN.
 * 
 */
public class ClassifyEvents {

  private static final String MODEL = "/home/werner/ML_Data/CNN_Model_67.onnx";
  private static final String DATA_DIR = "/meg2/ben/promax_data_home/MachineLearning_Synth_Field/PSEvents_2mil_750x500/";
  private static final String DATA_FILE = "FFID-000001_AMP-1.0000_CON-0.0_X-3142050.0_Y-1331668.8_ELEV--2368.1_T-1591734621255_CLASS-0.h5";

  /**
   * @param args A dummy argument.
   * @throws OrtException if there was an error in the native code
   */
  public static void main(String[] args) throws OrtException {

    final String filename = DATA_DIR + DATA_FILE;
    
    try {
      
      int file_id = H5.H5Fopen(filename, HDF5Constants.H5F_ACC_RDONLY, HDF5Constants.H5P_DEFAULT);

      if (file_id >= 0)
        H5.H5Fclose(file_id);

    } catch (HDF5Exception e) {
      e.printStackTrace();
    }
    
  //@formatter:off
    try (OrtEnvironment env = OrtEnvironment.getEnvironment();
	OrtSession.SessionOptions opts = new SessionOptions()) {
//@formatter:on

      opts.setOptimizationLevel(OptLevel.BASIC_OPT);

      System.out.println("Loading model from " + MODEL);

      try (OrtSession session = env.createSession(MODEL, opts)) {

	System.out.println("Inputs: " + session.getInputInfo().values());
	System.out.println("Outputs: " + session.getOutputInfo().values());

	// get number of output nodes and setup confusion matrix
	List<NodeInfo> values = new ArrayList<NodeInfo>(session.getOutputInfo().values());
	TensorInfo outputInfo = (TensorInfo) values.get(0).getInfo();
	int nout = (int) outputInfo.getShape()[1];
	int[][] confusionMatrix = new int[nout][nout];
	int correctCount = 0;

      }
    }

    System.out.println("Done!");

  }

}
