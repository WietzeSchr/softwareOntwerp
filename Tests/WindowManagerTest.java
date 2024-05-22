import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WindowManagerTest {
    @Test
    void testConstructor(){
        Buffer b1 = new FileBuffer(new String[] {"testtest", "1", "", "2"}, "test1.txt");
        FileBufferView fbv1 = new FileBufferView(10, 10, new Point(0, 0), b1);
        LayoutManager lm1 = new LayoutManager(fbv1, 1, "\n");
        TerminalInterface ih1 = new TerminalHandler();
        WindowManager wm1 = new WindowManager(10, 10, lm1, ih1);

        assertEquals(wm1.getLayoutManager(ih1), lm1);
        assertEquals(wm1.getWindowCount(), 0);
    }

    @Test
    void testOpenWindow(){
        TerminalInterface ih1 = new TerminalHandler();

        //opening window in FileBufferView in terminal
        Buffer b1 = new FileBuffer(new String[] {"testtest", "1", "", "2"}, "test1.txt");
        FileBufferView fbv1 = new FileBufferView(10, 10, new Point(0, 0), b1);
        LayoutManager lm1 = new LayoutManager(fbv1, 1, "\n");
        WindowManager wm1 = new WindowManager(10, 10, lm1, ih1);
        Textr test1 = new Textr("\n", fbv1);

        wm1.openWindow(test1,lm1, "\n");
        assertNotEquals(wm1.getLayoutManager(test1.getInputHandler()), lm1);
        assertEquals(wm1.getWindowCount(), 1);

        //opening window in GameView in terminal
        GameView gv1 = new GameView(10, 10, new Point(0, 0));
        LayoutManager lm2 = new LayoutManager(gv1, 1, "\n");
        WindowManager wm2 = new WindowManager(10, 10, lm2, ih1);
        Textr test2 = new Textr("\n", gv1);

        wm2.openWindow(test2, lm2, "\n");
        assertEquals(wm2.getWindowCount(), 0);
    }

    @Test
    void testCloseWindow(){
        TerminalInterface ih1 = new TerminalHandler();

        Buffer b1 = new FileBuffer(new String[] {"testtest", "1", "", "2"}, "test1.txt");
        FileBufferView fbv1 = new FileBufferView(10, 10, new Point(0, 0), b1);
        LayoutManager lm1 = new LayoutManager(fbv1, 1, "\n");
        WindowManager wm1 = new WindowManager(10, 10, lm1, ih1);
        Textr test1 = new Textr("\n", fbv1);

        wm1.openWindow(test1,lm1, "\n");
        SwingWindow sw1 = wm1.getSwingWindows()[0];
        assertEquals(wm1.getWindowCount(), 1);
        wm1.closeWindow(sw1);
        assertEquals(wm1.getWindowCount(), 0);
    }
}
