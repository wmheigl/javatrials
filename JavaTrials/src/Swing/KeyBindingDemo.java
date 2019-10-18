package Swing;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class KeyBindingDemo {
  JButton clicker;

  public KeyBindingDemo() {
    JFrame frame = new JFrame();

    JPanel contentPane = (JPanel) frame.getContentPane();
    addKeyBind(contentPane, "F10");
    clicker = new JButton("Clicker");
    contentPane.add(clicker);

    frame.setLayout(new GridBagLayout());
    frame.setSize(300, 300);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }

  Action disableButtonAction = new AbstractAction() {
	private static final long serialVersionUID = 1L;

	@Override
    public void actionPerformed(ActionEvent e) {
      clicker.setEnabled(!clicker.isEnabled());
    }
  };

  private static final String DISABLE_CLICKER = "disableClicker";

  private void addKeyBind(JComponent contentPane, String key) {
    InputMap iMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap aMap = contentPane.getActionMap();
    iMap.put(KeyStroke.getKeyStroke(key), DISABLE_CLICKER);
    aMap.put(DISABLE_CLICKER, disableButtonAction);
  }

  public static void main(String[] args) {
    new KeyBindingDemo();
  }
}