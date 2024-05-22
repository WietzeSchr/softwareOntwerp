import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public class WindowManager {

    ArrayList<SwingWindow> swingWindows = new ArrayList<>();

    int width;
    int heigth;

    public WindowManager(int width, int heigth){
        JFrame dummyFrame = new JFrame();
        dummyFrame.pack();
        this.width = width;
        this.heigth = heigth;
    }

    void openWindow(Textr listener){
        SwingWindow newWindow = new SwingWindow(width, heigth, listener);
        swingWindows.add(newWindow);
    }

    public void closeWindow(SwingWindow window) {
        swingWindows.remove(window);
        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
    }

    public int getWindowCount(){
        return swingWindows.size();
    }
}
