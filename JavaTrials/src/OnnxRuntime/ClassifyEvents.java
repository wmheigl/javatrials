/**
 * ClassifyEvents.java
 *
 * @author Werner M. Heigl
 * @version 2020.07.27
 */
package OnnxRuntime;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import ai.onnxruntime.NodeInfo;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.Result;
import ai.onnxruntime.OrtSession.SessionOptions;
import ai.onnxruntime.OrtSession.SessionOptions.ExecutionMode;
import ai.onnxruntime.OrtSession.SessionOptions.OptLevel;
import ai.onnxruntime.TensorInfo;
import edu.mines.jtk.util.ArrayMath;
import hdf.hdf5lib.*;

/**
 * Midkiff event classification with Ben's CNN.
 * 
 */
public class ClassifyEvents {

  // private static final String MODEL = "/home/werner/ML_Data/CNN_Model_67.onnx";
  private static final String MODEL = "/home/werner/ML_Data/CNN_Model_188.onnx";
  private static final String MODEL_OPT = "/home/werner/optimModel.onnx";
  private static final String DATA_DIR = "/meg2/ben/promax_data_home/MachineLearning_Synth_Field/PSEvents_2mil_750x500/";
  // private static final String DATA_DIR =
  // "/meg2/ben/promax_data_home/MachineLearning_Synth_Field/Noise_2mil_750x500/";

  /**
   * @param args A dummy argument.
   * @throws OrtException if there was an error in the native code
   */
  public static void main(String[] args) throws OrtException {

    List<Path> fileList = null;

    try {
      long maxSize = 2;// Long.MAX_VALUE;
      fileList = Files.find(Paths.get(DATA_DIR), 1, (p, bfa) -> bfa.isRegularFile() && p.getFileName().toString().endsWith(".h5"))
	  .limit(maxSize).collect(Collectors.toList());
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("No. of files: " + fileList.size());

//@formatter:off
    try (OrtEnvironment env = OrtEnvironment.getEnvironment();
	OrtSession.SessionOptions opts = new SessionOptions()) {
//@formatter:on

      opts.setExecutionMode(ExecutionMode.SEQUENTIAL);
      opts.setOptimizationLevel(OptLevel.BASIC_OPT);
      opts.setOptimizedModelFilePath(MODEL_OPT);
      // opts.addCUDA(); / need to compile from source with CUDA support

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

	// get number of output nodes and setup confusion matrix
	System.out.println("Outputs: " + session.getOutputInfo().values());
	List<NodeInfo> outputValues = new ArrayList<NodeInfo>(session.getOutputInfo().values());
	TensorInfo outputInfo = (TensorInfo) outputValues.get(0).getInfo();
	int nout = (int) outputInfo.getShape()[1];
	int[][] confusionMatrix = new int[nout][nout];
	int correctCount = 0;

	/*
	 * iterate over list of H5 files and keep track of scores
	 */

	ListIterator<Path> fileIterator = fileList.listIterator();
	while (fileIterator.hasNext()) {

	  /*
	   * load a data frame, pre-process and shape it into a rank-4 tensor
	   */

	  String fileName = fileIterator.next().toString();
	  // float[][] dataFrame = ClassifyEvents.loadH5(fileName);
	  float[][] dataFrame = ClassifyEvents.preProcess(ClassifyEvents.loadH5(fileName));
	  System.out.println("Min/Max: " + ArrayMath.min(dataFrame) + "/" + ArrayMath.max(dataFrame));

	  // sub-sample to 250 samples
	  float[][] subSampled = new float[dim1][dim2];
	  for (int i = 0; i < subSampled.length; i++) {
	    subSampled[i] = ArrayMath.copy((int) dims[2], 0, 3, dataFrame[i]);
	  }
	  float[][][] datum = new float[dim1][dim2][dim3];
	  for (int i = 0; i < dim1; i++) {
	    for (int j = 0; j < dim2; j++) {
	      datum[i][j] = new float[] { subSampled[i][j] };
	    }
	  }
	  // System.out.println("Min/Max: " + ArrayMath.min(datum) + "/" + ArrayMath.max(datum));

	  System.out.println("Tensor.info: " + OnnxTensor.createTensor(env, datum).getInfo());

	  /*
	   * Run the prediction
	   */
	  
	  String inputName = session.getInputNames().iterator().next();
	  // System.out.println("Input Name: " + inputName);

	  try (OnnxTensor test = OnnxTensor.createTensor(env, new float[][][][] { datum });
	      Result output = session.run(Collections.singletonMap(inputName, test))) {

	    // 0: noise, 1: PS Event, 2: S-only
	    float[][] scores = (float[][]) output.get(0).getValue();
	    // System.out.println("Scores: " + Arrays.toString(scores[0]));

	    int predLabel = ClassifyEvents.pred(scores[0]);

	    if (predLabel == 1) {
	      correctCount++;
	    }

	    confusionMatrix[1][predLabel]++;

	    if (fileIterator.previousIndex() % 200 == 0) {
	      System.out.println("File: " + fileName);
	      System.out.println("Scores: " + Arrays.toString(scores[0]));
	      System.out.println("Progress: " + fileIterator.previousIndex() + " files");
	    }

	  }

	} // end for-each (path)

	System.out.println();
	System.out.println("Final accuracy = " + ((float) correctCount) / fileList.size());

	// confusion matrix (vertical axis: actual category, horizontal axis: predicted category)
	StringBuilder sb = new StringBuilder();
	sb.append("Label");
	for (int i = 0; i < confusionMatrix.length; i++) {
	  sb.append(String.format("%1$6s", "" + i));
	}
	sb.append("\n");

	for (int i = 0; i < confusionMatrix.length; i++) {
	  sb.append(String.format("%1$6s", "" + i));
	  for (int j = 0; j < confusionMatrix[i].length; j++) {
	    sb.append(String.format("%1$6s", "" + confusionMatrix[i][j]));
	  }
	  sb.append("\n");
	}
	System.out.println("Confusion Matrix");
	System.out.println("-----------------");
	System.out.println(sb.toString());

      } // end try (session)

    } // end (env, opts)

    System.out.println("Done!");

  }

  /**
   * Transforms the input {@code array} along the fast dimension by converting elements to z-scores and re-scale their
   * absolute values to the interval [0,1].
   * 
   * @param array The input array.
   * @return the pre-processed input array
   */
  private static float[][] preProcess(float[][] array) {

    float[][] ppArray = array.clone();
    for (int i = 0; i < array.length; i++) {
      // get means and std. deviations
      float mean = ClassifyEvents.getMean(array[i]);
      float stdev = ArrayMath.sqrt(ClassifyEvents.getVariance(array[i], mean));
      ppArray[i] = ArrayMath.sub(array[i], mean);
      ppArray[i] = ArrayMath.div(ppArray[i], stdev);
      ppArray[i] = ArrayMath.abs(ppArray[i]);
      ppArray[i] = ClassifyEvents.rescale(ppArray[i], 0.01f, 0.99f);
    }
    return ppArray;
  }

  /**
   * Re-scales array values to the interval [lower, upper].
   * 
   * @param array The input {@code array}.
   * @param lower The lower limit of the interval.
   * @param upper The upper limit of the interval.
   * @return the rescaled array
   */
  private static float[] rescale(float[] array, float lower, float upper) {
    float[] scaledArray = array.clone();
    float min = ArrayMath.min(array);
    float max = ArrayMath.max(array);
    scaledArray = ArrayMath.sub(array, min);
    scaledArray = ArrayMath.div(scaledArray, max - min);
    scaledArray = ArrayMath.mul(scaledArray, upper - lower);
    scaledArray = ArrayMath.add(scaledArray, lower);
    return scaledArray;
  }

  /**
   * @param fs The input array.
   * @param mean The mean of the input array.
   * @return the variance
   */
  private static float getVariance(float[] fs, float mean) {
    float sum = 0;
    for (float f : fs) {
      sum += (f - mean) * (f - mean);
    }
    return sum / (fs.length - 1);
  }

  /**
   * @param fs The input array.
   * @return the arithmetic mean of the input array.
   */
  private static float getMean(float[] fs) {
    return ArrayMath.sum(fs) / fs.length;
  }

  /**
   * Find the maximum score and return its index.
   *
   * @param scores The scores.
   * @return The index of the max.
   */
  public static int pred(float[] scores) {
    float maxVal = Float.NEGATIVE_INFINITY;
    int idx = 0;
    for (int i = 0; i < scores.length; i++) {
      if (scores[i] > maxVal) {
	maxVal = scores[i];
	idx = i;
      }
    }
    return idx;
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
	// System.out.println("No. of dimensions: " + ndims);
	long[] dims = new long[ndims];
	long[] maxdims = new long[ndims];
	H5.H5Sget_simple_extent_dims(space_id, dims, maxdims);
	// System.out.println("Dimensions: " + Arrays.toString(dims));
	// System.out.println("Max. Dimensions: " + Arrays.toString(maxdims));
	dset_data = new float[(int) dims[0]][(int) dims[1]];
      }

      // get the dataspace attribute
      if (dataset_id >= 0) {
	attr_id = H5.H5Aopen_by_name(dataset_id, ".", "ActualTracesInFrame", HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
	long[] ntraces = new long[1];
	H5.H5Aread(attr_id, HDF5Constants.H5T_NATIVE_LONG, ntraces);
	// System.out.println("Traces in file: " + ntraces[0]);
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
