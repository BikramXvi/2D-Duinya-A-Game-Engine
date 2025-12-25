package Controller;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.*;

public class KeyboardUtils {

    /**
     * Single method for all text components (JTextField, JPasswordField, etc.)
     */
    public static void enableKeyboardNavigation(JTextComponent field, JComponent prevField, JComponent nextField, JButton button) {
        field.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();

                if (code == KeyEvent.VK_ENTER) {
                    button.doClick();  // press the button
                } else if (code == KeyEvent.VK_UP && prevField != null) {
                    prevField.requestFocus();
                } else if (code == KeyEvent.VK_DOWN && nextField != null) {
                    nextField.requestFocus();
                }
            }
        });
    }
}
