/**
 * ScoreMnistCsv.java
 *
 * @author Werner M. Heigl
 * @version 2020.07.28
 */
package OnnxRuntime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.Result;
import ai.onnxruntime.OrtSession.SessionOptions;
import ai.onnxruntime.OrtSession.SessionOptions.ExecutionMode;
import ai.onnxruntime.OrtSession.SessionOptions.OptLevel;

/**
 * Runs predictions on MNIST test data in CSV format.
 * <p>
 * Data files can be found at
 * <a href="https://pjreddie.com/projects/mnist-in-csv">https://pjreddie.com/projects/mnist-in-csv/</a>.
 * 
 */
public class ScoreMnistCsv {

  private static final String MODEL = "/home/werner/ML_Data/mlp_mnist_python_model_trained.onnx";
//  private static final String DATA_SMALL = "/home/werner/ML_Data/mnist_csv/mnist_test_10.csv";
  private static final String DATA = "/home/werner/ML_Data/mnist_csv/mnist_test.csv";

  /**
   * @param args A dummy argument.
   * @throws OrtException if the was an error in the native code
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static void main(String[] args) throws OrtException, FileNotFoundException, IOException {

//@formatter:off
    try (OrtEnvironment env = OrtEnvironment.getEnvironment();
	OrtSession.SessionOptions opts = new SessionOptions()) {
//@formatter:on

      opts.setExecutionMode(ExecutionMode.PARALLEL);
      opts.setOptimizationLevel(OptLevel.ALL_OPT);
      // opts.setExecutionMode(ExecutionMode.SEQUENTIAL);
      // opts.setOptimizationLevel(OptLevel.BASIC_OPT);

      System.out.println("Loading model from " + MODEL);

      try (OrtSession session = env.createSession(MODEL, opts)) {

	System.out.println("Inputs: " + session.getInputInfo().values());
	System.out.println("Outputs: " + session.getOutputInfo().values());

	List<Pair<Integer, float[][]>> data = ScoreMnistCsv.loadCSV(DATA);

	int correctCount = 0;
	int[][] confusionMatrix = new int[10][10];

	String inputName = session.getInputNames().iterator().next();
	System.out.println("Input Name: " + inputName);

	for (Pair<Integer, float[][]> datum : data) {

	  try (OnnxTensor test = OnnxTensor.createTensor(env, datum.getRight());
	      Result output = session.run(Collections.singletonMap(inputName, test))) {

	    float[][] scores = (float[][]) output.get(0).getValue();

	    int predLabel = ScoreMnistCsv.pred(scores[0]);

	    if (predLabel == datum.getLeft()) {
	      correctCount++;
	    }

	    confusionMatrix[datum.getLeft()][predLabel]++;

	  }
	}

	System.out.println("Final accuracy = " + ((float) correctCount) / data.size());

	// confusion matrix (vertical axis: actual category, horizontal axis: predicted category)
	StringBuilder sb = new StringBuilder();
	sb.append("Label");
	for (int i = 0; i < confusionMatrix.length; i++) {
	  sb.append(String.format("%1$5s", "" + i));
	}
	sb.append("\n");

	for (int i = 0; i < confusionMatrix.length; i++) {
	  sb.append(String.format("%1$5s", "" + i));
	  for (int j = 0; j < confusionMatrix[i].length; j++) {
	    sb.append(String.format("%1$5s", "" + confusionMatrix[i][j]));
	  }
	  sb.append("\n");
	}
	System.out.println("Confusion Matrix");
	System.out.println("-----------------");
	System.out.println(sb.toString());

      }

      System.out.println("Done!");

    }
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

  /**
   * Loads a CSV file.
   * 
   * @param dataPath The path to the CSV file.
   * @return A list of tuples (label, values).
   * @throws FileNotFoundException if the CSV file at {@code dataPath} does not exist
   * @throws IOException if an error occurs in the input stream created from the CSV file
   */
  private static List<Pair<Integer, float[][]>> loadCSV(String dataPath) {
    List<Pair<Integer, float[][]>> data = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(dataPath))) {
      String line;
      while ((line = reader.readLine()) != null) {
	String[] tokens = line.split(",");
	Integer label = Integer.valueOf(tokens[0]);
	String[] vals = Arrays.copyOfRange(tokens, 1, tokens.length);
	List<Float> valsList = Arrays.stream(vals).map(s -> Float.valueOf(s) / 255).collect(Collectors.toList());
	float[] values = ArrayUtils.toPrimitive(valsList.toArray(new Float[0]));
	data.add(Pair.of(label, new float[][] { values })); // Tensors specified in ONNX model are 2D.
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    System.out.println("Loaded " + data.size() + " features & labels, from '" + dataPath + "'.");
    return data;
  }
}
