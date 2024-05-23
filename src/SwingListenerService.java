import java.io.IOException;

public class SwingListenerService {

    private SwingListener swingListener;

    SwingListenerService(SwingListener textr){
        setSwingListener(textr);
    }

    public void setSwingListener(SwingListener l) {
        swingListener = l;
    }

    public void removeSwingListener(){swingListener = null;}


    void fireKeyEvent(int key) throws IOException {
        swingListener.respondTo(key);
    }

    void fireFocusEvent(InputInterface focussed){
        swingListener.updateKeyboardFocus(focussed);
    }

    void fireCloseEvent(SwingWindow window){
        swingListener.removeWindow(window);
        fireFocusEvent(null);
    }
}
