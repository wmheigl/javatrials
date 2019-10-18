/**
 * Logging.java
 *
 * @author Werner M. Heigl
 * @version 2019.05.03
 */
package Utils;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class illustrates various aspects of {@link java.util.logging}.
 * 
 */
public class Logging {

	/** Class name to be used in log messages. */
	private static final String CLASS_NAME = Logging.class.getName();
	/** Logger for diagnostic information. */
	private static final Logger LOG = Logger.getLogger(CLASS_NAME);

	public static void main(String[] args) {

		Level level = Level.FINER;
		/*
		 * the below illustrates how to get entering() and exiting() to work
		 * If no handler is specified the parent handler is used, which by default works at INFO level.
		 */
		LOG.setLevel(level);
		LOG.info("parent handlers: "+ LOG.getParent().getHandlers().length);
		Handler parentHandlers[] = LOG.getParent().getHandlers();
		for (Handler handler : parentHandlers) {
			handler.setLevel(level);
		}
//		LOG.setUseParentHandlers(false);
//		ConsoleHandler handler = new ConsoleHandler();
//		handler.setLevel(Level.FINER);
//		LOG.addHandler(handler);
		
		LOG.entering(CLASS_NAME, "main()");
		LOG.info("Example message");
		LOG.exiting(CLASS_NAME, "main()");
	}

}
