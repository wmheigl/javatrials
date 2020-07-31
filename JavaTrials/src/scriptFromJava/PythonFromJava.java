/**
 * PythonFromJava.java
 *
 * @author Werner M. Heigl
 * @version 2020.07.30
 */
package scriptFromJava;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.FloatBuffer;
import java.util.Arrays;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;

/**
 * Illustrates ways of calling a Python script from a Java class.
 * 
 */
public class PythonFromJava {

  /* Reference level is the top directory of the project */
  private static final String SCRIPT_DIR = "./src/scriptFromJava/Resources/";
  private static final String SCRIPT = "hello.py";

  /**
   * @param args A dummy argument.
   */
  public static void main(String[] args) {

    // withProcessBuilder();

    // withCommonsExec();

    withCommonsExecSharedMemoryFile();

  }

  /**
   * Using a shared memory file to pass larger amounts of data to the Python side.
   * This has to be done via an intermediate representation that both sides can handle.
   * Here it is done via a CSV string.
   */
  private static void withCommonsExecSharedMemoryFile() {
    System.out.println("Using: Apache Commons Exec & shared memory file");
    String sharedMemoryFile = "/dev/shm/shared_mem_file.csv";
//    String testFile = "/home/werner/testfile.txt";
    float[] data = new float[] { 1.0f, 2.0f, 3.0f, 4.0f };
    String csvData = Arrays.toString(data).replaceAll("\\[|\\]", "").replace(" ", "");
    try (PrintWriter out = new PrintWriter(sharedMemoryFile)) {
      out.print(csvData);
      out.flush();
      CommandLine cmd = new CommandLine("python3");
      cmd.addArgument(SCRIPT_DIR + SCRIPT);
      cmd.addArgument("--file").addArgument(sharedMemoryFile);
      cmd.addArgument("--type").addArgument("float");
      Executor exec = new DefaultExecutor();
      System.out.print("Result:  ");
      exec.execute(cmd); // synchronous execution
      System.out.println("Back in Java");
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println();
  }

  /**
   * This approach can be synchronous or asynchronous. Sub-process output and error are not captured here but it is
   * possible using ExecuteStreamHandler.
   */
  private static void withCommonsExec() {
    System.out.println("Using: Apache Commons Exec");
    try {
      CommandLine cmd = new CommandLine("python");
      cmd.addArgument(SCRIPT_DIR + SCRIPT);
      Executor exec = new DefaultExecutor();
      System.out.print("Result:  ");
      exec.execute(cmd); // synchronous execution
      // DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
      // exec.execute(cmd, resultHandler); // asynchronous execution
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println();
  }

  /**
   * This method illustrates the use the ProcessBuilder class. It is not synchronized.
   */
  private static void withProcessBuilder() {
    System.out.println("Using: Java ProcessBuilder");
    try {
      ProcessBuilder pb = new ProcessBuilder("rm", "-f", SCRIPT_DIR + SCRIPT + 'c');
      Process proc = pb.start();
      proc.waitFor();
      pb = new ProcessBuilder("python", "-m", "compileall", SCRIPT_DIR + SCRIPT);
      proc = pb.start();
      proc.waitFor();
      pb = new ProcessBuilder("python", SCRIPT_DIR + SCRIPT + 'c');
      proc = pb.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
      System.out.println("Result:  " + reader.readLine());
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println();
  }

}
