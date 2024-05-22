import java.io.IOException;

public class SwingListenerService {

    private InputListener inputListener;
    private KeyBoardFocusListener keyBoardFocusListener;

    SwingListenerService(Textr textr){
        setInputListener(textr);
        setKeyBoardFocusListener(textr);
    }

    public void setKeyBoardFocusListener(KeyBoardFocusListener k) {
        keyBoardFocusListener = k;
    }

    public void removeKeyBoardFocusListener(){
        keyBoardFocusListener = null;
    }

    public void setInputListener(InputListener i) {
        inputListener = i;
    }

    public void removeInputListener(){
        inputListener = null;
    }

    void fireKeyEvent(int key) throws IOException {
        inputListener.respondTo(key);
    }

    void fireFocusEvent(TerminalInterface focussed){
        keyBoardFocusListener.updateKeyboardFocus(focussed);
    }
}
