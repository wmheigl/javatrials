/**
 * SplitPanes.java
 *
 * @author Werner M. Heigl
 * @version 2019.03.15
 */
package Swing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

/**
 * This illustrates nested split panes.
 * 
 * See <a href=
 * "https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html#SplitPaneDemo2">Swing
 * Tutorial</a>.
 */
public class SplitPanes extends JFrame {

	private static final long serialVersionUID = 1L;

	public SplitPanes() {
		super("Split Panes");

		JPanel contentPane = new JPanel(new BorderLayout());

		JToolBar toolBar = new JToolBar();
		toolBar.add(new JButton("First toolbar button"));
		contentPane.add(toolBar, BorderLayout.NORTH);

		JSplitPane topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		topSplitPane.setLeftComponent(new JLabel("Top Left"));
		topSplitPane.setRightComponent(new JLabel("Top Right"));
		topSplitPane.setOneTouchExpandable(true);
		topSplitPane.setDividerLocation(375);
		topSplitPane.setResizeWeight(1.0);
		topSplitPane.setBorder(null);

		JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		bottomSplitPane.setLeftComponent(new JLabel("Bottom Left"));
		bottomSplitPane.setRightComponent(new JLabel("Bottom Right"));
		bottomSplitPane.setOneTouchExpandable(true);
		bottomSplitPane.setDividerLocation(375);
		bottomSplitPane.setResizeWeight(1.0);
		bottomSplitPane.setBorder(null);

		JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainSplitPane.setTopComponent(topSplitPane);
		mainSplitPane.setBottomComponent(bottomSplitPane);
		mainSplitPane.setPreferredSize(new Dimension(500, 350));
		mainSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setDividerLocation(0.5);
		mainSplitPane.setResizeWeight(0.5);
		contentPane.add(mainSplitPane, BorderLayout.CENTER);

		JPanel messagePanel = new JPanel(new BorderLayout());
		messagePanel.setBorder(BorderFactory.createTitledBorder("Status"));
		messagePanel.add(new JLabel("Message text"), BorderLayout.WEST);
		messagePanel.setVisible(true);
		contentPane.add(messagePanel, BorderLayout.SOUTH);

		this.setContentPane(contentPane);
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked
	 * from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new SplitPanes();
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
