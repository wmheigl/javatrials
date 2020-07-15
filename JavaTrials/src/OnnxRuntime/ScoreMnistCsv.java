/**
 * ScoreMnistCsv.java
 *
 * @author Werner M. Heigl
 * @version 2020.07.14
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
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.Result;
import ai.onnxruntime.OrtSession.SessionOptions;
import ai.onnxruntime.OrtSession.SessionOptions.OptLevel;

/**
 * Runs predictions on MNIST test data in CSV format.
 * <p>
 * Data files can be found at
 * <a href="https://pjreddie.com/projects/mnist-in-csv">https://pjreddie.com/projects/mnist-in-csv/</a>.
 * 
 */
public class ScoreMnistCsv {

  private static final Logger LOG = Logger.getLogger(ScoreMNIST.class.getName());

  private static final String MODEL = "/home/werner/ML_Data/mlp_mnist_python_model_trained.onnx";
  private static final String DATA = "/home/werner/ML_Data/mnist_csv/mnist_test_10.csv";

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

      opts.setOptimizationLevel(OptLevel.BASIC_OPT);

      LOG.info("Loading model from " + MODEL);

      try (OrtSession session = env.createSession(MODEL, opts)) {

	LOG.info("Inputs: " + session.getInputInfo().values());
	LOG.info("Outputs: " + session.getOutputInfo().values());

	List<Pair<Integer, float[][]>> data = ScoreMnistCsv.loadCSV(DATA);

	int correctCount = 0;
	int[][] confusionMatrix = new int[10][10];

	String inputName = session.getInputNames().iterator().next();
	LOG.info("Input Name: " + inputName);

	for (Pair<Integer, float[][]> datum : data) {

	  try (OnnxTensor test = OnnxTensor.createTensor(env, datum.getRight());
	      Result output = session.run(Collections.singletonMap(inputName, test))) {

	  }
	}
      }
    }
  }

  /**
   * Loads a CSV file.
   * 
   * @param dataPath The path to the CSV file.
   * @return A list of tuples of type (label, values).
   * @throws FileNotFoundException if the CSV file at {@code dataPath} does not exist
   * @throws IOException if an error occurs in the input stream created from the CSV file
   */
  private static List<Pair<Integer, float[][]>> loadCSV(String dataPath) {
    List<Pair<Integer, float[][]>> data = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(dataPath))) {
      String line;
      while ((line = reader.readLine()) != null) {
	String[] tokens = line.split(",");
	String[] vals = Arrays.copyOfRange(tokens, 1, tokens.length);
	List<Float> valsList = Arrays.stream(vals).map(Float::valueOf).collect(Collectors.toList());
	float[] values = ArrayUtils.toPrimitive(valsList.toArray(new Float[0]));
	Integer label = Integer.valueOf(tokens[0]);
	data.add(Pair.of(label, new float[][] { values })); // Tensors specified in ONNX model are 2D.
      }
    } catch (Exception e) {
      LOG.info(e.getMessage());
    }

    LOG.info("Loaded " + data.size() + " features & labels, from '" + dataPath + "'.");
    return data;
  }
}
