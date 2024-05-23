import javax.swing.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class WindowManager {

    private ArrayList<SwingWindow> swingWindows = new ArrayList<>();
    private Dictionary<InputInterface, LayoutManager> layoutManagers = new Hashtable<>();

    int width;
    int heigth;

    JFrame dummyFrame;

    public WindowManager(int width, int heigth, LayoutManager layoutManager, InputInterface terminalHandler){
        dummyFrame = new JFrame();
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

    public LayoutManager getLayoutManager(InputInterface inputHandler){
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
        try {
            layoutManagers.put(newWindow, new LayoutManager(fbv, 1, newLine));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeWindow(SwingWindow window) {
        swingWindows.remove(window);
        layoutManagers.remove(window);
        //window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
    }

    public void close(){
        dummyFrame.dispatchEvent(new WindowEvent(dummyFrame, WindowEvent.WINDOW_CLOSING));
    }
}
