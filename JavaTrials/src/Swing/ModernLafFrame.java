/**
 * ModernLafFrame.java
 *
 * @author Werner M. Heigl
 * @version 2019.03.15
 */
package Swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * This class illustrates how to create a modern look and feel.
 * 
 * See <a href=
 * "https://stackoverflow.com/questions/3035880/how-can-i-create-a-bar-in-the-bottom-of-a-java-app-like-a-status-bar">Stackoverflow</a>
 */
public class ModernLafFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public ModernLafFrame() {

		JPanel outerPanel = new JPanel(new GridBagLayout());
		outerPanel.setPreferredSize(new Dimension(300, 300));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;

		JPanel menuJPanel = new JPanel();
		menuJPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.RED));
		outerPanel.add(menuJPanel, gbc);

		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;

		JPanel contentJPanel = new JPanel();
		contentJPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLUE));
		outerPanel.add(contentJPanel, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 0, 0);

		outerPanel.add(new JSeparator(JSeparator.HORIZONTAL), gbc);
		outerPanel.add(new JPanel(), gbc);

		this.getContentPane().add(outerPanel);
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked
	 * from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new ModernLafFrame();
		frame.setLocation(250, 250);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread: creating and showing this
		// application's GUI.
		javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI());
	}

}
