/**
 * RetrieveCsvFile.java
 *
 * @author Werner M. Heigl
 * @version 2020.07.15
 */
package Internet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * This class illustrates how to retrieve a CSV file from a website.
 * 
 */
public class RetrieveCsvFile {

  private static final String MNIST_TEST = "https://pjreddie.com/media/files/mnist_test.csv";
  /**
   * @param args A dummy argument.
   * @throws IOException if any errors are encountered during file retrieval
   */
  public static void main(String[] args) throws IOException {

    URL url = new URL(MNIST_TEST);

    // using Java IO
    String line;
    try (InputStream in = url.openStream()) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      line = reader.readLine();
      System.out.println(line);
    }
    
    // using Java NIO, copies bytewise into a file
    try (ReadableByteChannel readChannel = Channels.newChannel(new URL(MNIST_TEST).openStream())) {      
      File tmpfile = File.createTempFile("temp", null);
      System.out.println("temp file: " + tmpfile.toString());
      FileOutputStream fileOS = new FileOutputStream(tmpfile);
      FileChannel writeChannel = fileOS.getChannel();
      writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);
      tmpfile.deleteOnExit(); // not sure if necessary, temp files should get deleted by OS
    }
  }

}
