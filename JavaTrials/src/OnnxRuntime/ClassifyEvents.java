/**
 * ClassifyEvents.java
 *
 * @author Werner M. Heigl
 * @version 2020.07.20
 */
package OnnxRuntime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ai.onnxruntime.NodeInfo;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.Result;
import ai.onnxruntime.OrtSession.SessionOptions;
import ai.onnxruntime.OrtSession.SessionOptions.OptLevel;
import ai.onnxruntime.TensorInfo;
import edu.mines.jtk.util.ArrayMath;
import hdf.hdf5lib.*;

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

  //@formatter:off
    try (OrtEnvironment env = OrtEnvironment.getEnvironment();
	OrtSession.SessionOptions opts = new SessionOptions()) {
//@formatter:on

      opts.setOptimizationLevel(OptLevel.BASIC_OPT);

      System.out.println("Loading model from " + MODEL);

      try (OrtSession session = env.createSession(MODEL, opts)) {

	// get input shape
	System.out.println("Inputs: " + session.getInputInfo().values());
	String key = (String) session.getInputInfo().keySet().toArray()[0]; // only one input layer
	TensorInfo info = (TensorInfo) session.getInputInfo().get(key).getInfo();
	long[] dims = info.getShape();
	System.out.println("Input shape: " + Arrays.toString(dims));
	int dim1 = (int) dims[1];
	int dim2 = (int) dims[2];
	int dim3 = (int) dims[3];

	// load data frame and wrap in a rank-4 tensor
	float[][] data_frame = ClassifyEvents.loadH5(filename);

	// sub-sample to 250 samples
	float[][] subSampled = new float[dim1][dim2];
	for (int i = 0; i < subSampled.length; i++) {
	  subSampled[i] = ArrayMath.copy((int) dims[2], 0, 3, data_frame[i]);
	}
	float[][][] datum = new float[dim1][dim2][dim3];
	for (int i = 0; i < dim1; i++) {
	  for (int j = 0; j < dim2; j++) {
	    datum[i][j] = new float[] { subSampled[i][j] };
	  }
	}

	System.out.println("Tensor.info: " + OnnxTensor.createTensor(env, datum).getInfo());

	// get number of output nodes and setup confusion matrix
	System.out.println("Outputs: " + session.getOutputInfo().values());
	List<NodeInfo> outputValues = new ArrayList<NodeInfo>(session.getOutputInfo().values());
	TensorInfo outputInfo = (TensorInfo) outputValues.get(0).getInfo();
	int nout = (int) outputInfo.getShape()[1];
	int[][] confusionMatrix = new int[nout][nout];
	int correctCount = 0;

	String inputName = session.getInputNames().iterator().next();
	System.out.println("Input Name: " + inputName);

	try (OnnxTensor test = OnnxTensor.createTensor(env, new float[][][][] { datum });
	    Result output = session.run(Collections.singletonMap(inputName, test))) {

	  float[][] scores = (float[][]) output.get(0).getValue();
	  System.out.println("Scores: " + Arrays.toString(scores[0]));
	}
      }
    }

    System.out.println("Done!");

  }

  private static float[][] loadH5(String filename) {

    final String DATASETNAME = "NanoSeisSingleFrameData/TraceData/TracesSamples";

    int file_id = -1;
    int dataset_id = -1;
    int space_id = -1;
    int attr_id = -1;
    float[][] dset_data = null;

    try {

      // open resources
      file_id = H5.H5Fopen(filename, HDF5Constants.H5F_ACC_RDONLY, HDF5Constants.H5P_DEFAULT);
      if (file_id >= 0) {
	dataset_id = H5.H5Dopen(file_id, DATASETNAME, HDF5Constants.H5P_DEFAULT);
      }

      // get the dataspace dimensions
      if (dataset_id >= 0) {
	space_id = H5.H5Dget_space(dataset_id);
	int ndims = H5.H5Sget_simple_extent_ndims(space_id);
	System.out.println("No. of dimensions: " + ndims);
	long[] dims = new long[ndims];
	long[] maxdims = new long[ndims];
	H5.H5Sget_simple_extent_dims(space_id, dims, maxdims);
	System.out.println("Dimensions: " + Arrays.toString(dims));
	System.out.println("Max. Dimensions: " + Arrays.toString(maxdims));
	dset_data = new float[(int) dims[0]][(int) dims[1]];
      }

      // get the dataspace attribute
      if (dataset_id >= 0) {
	attr_id = H5.H5Aopen_by_name(dataset_id, ".", "ActualTracesInFrame", HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
	long[] ntraces = new long[1];
	H5.H5Aread(attr_id, HDF5Constants.H5T_NATIVE_LONG, ntraces);
	System.out.println("Traces in file: " + ntraces[0]);
	if (attr_id >= 0) {
	  H5.H5Aclose(attr_id);
	}
      }

      // read the data frame
      if (dataset_id >= 0) {
	H5.H5Dread(dataset_id, HDF5Constants.H5T_NATIVE_FLOAT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT,
	    dset_data);
      }

      // close resources
      if (dataset_id >= 0) {
	H5.H5Dclose(dataset_id);
      }
      if (file_id >= 0) {
	H5.H5Fclose(file_id);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return dset_data;
  }

}
