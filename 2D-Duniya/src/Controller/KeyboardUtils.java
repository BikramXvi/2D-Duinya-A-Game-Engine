package Controller;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * Utility class for enabling keyboard navigation in Swing text components.
 */
public class KeyboardUtils {

    /**
     * Enables keyboard navigation for a text component, allowing Enter to click a button,
     * Up arrow to focus the previous component, and Down arrow to focus the next component.
     * @param field the text component to enable navigation for
     * @param prevField the previous component to focus on Up key (can be null)
     * @param nextField the next component to focus on Down key (can be null)
     * @param button the button to click on Enter key
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
