/**
 * ListOfFiles.java
 *
 * @author Werner M. Heigl
 * @version 2019.02.07
 */
package Collections;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class illustrates how to create a list of files of a certain type in a
 * given directory and its sub-directories.
 * 
 */
public class ListOfFiles {

	private static Logger LOG = Logger.getLogger(ListOfFiles.class.getName());

	private static int MAX_DEPTH = Integer.MAX_VALUE;

	public static void main(String[] args) {

		LOG.setLevel(Level.INFO);

		String directory = System.getProperty("user.home");
		Path searchPath = Paths.get(directory + "/KatyRoboticsInvoice.pdf");
		int maxDepth = MAX_DEPTH;
		String suffix = ".pdf";

		LOG.info("Searching for " + suffix + " in " + directory);

		try {
			List<Path> listOfFiles = Files
					.find(searchPath, maxDepth,
							(p, bfa) -> bfa.isRegularFile() && p.getFileName().toString().endsWith(suffix))
					.collect(Collectors.toList());
			listOfFiles.parallelStream().forEach(System.out::println);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
