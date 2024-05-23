import javax.swing.*;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

public class WindowManager {

    private ArrayList<SwingWindow> swingWindows = new ArrayList<>();
    private Dictionary<TerminalInterface, LayoutManager> layoutManagers = new Hashtable<>();

    int width;
    int heigth;

    public WindowManager(int width, int heigth, LayoutManager layoutManager, TerminalInterface terminalHandler){
        JFrame dummyFrame = new JFrame();
        dummyFrame.pack();
        this.width = width;
        this.heigth = heigth;
        this.layoutManagers.put(terminalHandler, layoutManager);
    }

    /***********
     * Getters *
     ***********/

    public int getWindowCount(){
        return swingWindows.size();
    }

    public LayoutManager getLayoutManager(TerminalInterface inputHandler){
        return layoutManagers.get(inputHandler);
    }

    public SwingWindow[] getSwingWindows(){
        SwingWindow[] temp = new SwingWindow[swingWindows.size()];
        for(int i=0; i<swingWindows.size(); i++){
            temp[i] = swingWindows.get(i);
        }
        return temp;
    }


    void openWindow(Textr listener, LayoutManager layoutManager, String newLine){
        Buffer currBuffer = layoutManager.getCurrentBuffer();
        if(currBuffer == null) {
            return;
        }
        SwingWindow newWindow = new SwingWindow(width, heigth, listener);
        swingWindows.add(newWindow);
        Layout lay = layoutManager.getLayout();
        FileBufferView fbv = new FileBufferView(lay.getHeigth(), lay.getWidth(), lay.getLeftUpperCorner(), currBuffer);
        layoutManagers.put(newWindow, new LayoutManager(fbv, 1, newLine));
    }

    public void closeWindow(SwingWindow window) {
        swingWindows.remove(window);
        layoutManagers.remove(window);
        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
    }
}
