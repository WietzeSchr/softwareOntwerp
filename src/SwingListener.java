import java.io.IOException;

public interface SwingListener {
    void updateKeyboardFocus(InputInterface focussed, boolean testing);

    void removeWindow(SwingWindow swingWindow);
    void updateKeyboardFocus(InputInterface focussed);
    void respondTo(int key) throws IOException;
}
