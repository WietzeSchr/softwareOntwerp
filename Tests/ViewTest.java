import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ViewTest {

    @Test
    void testSetters() {
        GameView gv = new GameView(10, 20, new Point(5, 15));
        FileBufferView fbv = new FileBufferView(1, 10, new Point(15, 25),
                new FileBuffer(new String[] {"hallo", ""}, "test"));
        gv.setPosition(4);
        assertEquals(gv.getPosition(), 4);
        fbv.initViewPosition(3);
        assertEquals(fbv.getPosition(), 3);
        assertEquals(gv.countViews(), 1);
        assertEquals(fbv.getFocusedView(3), fbv);
        assertNull(fbv.getFocusedView(4));
    }

    @Test
    void getNextPreviousFocus() {
        GameView gv = new GameView(10, 20, new Point(5, 15));
        FileBufferView fbv = new FileBufferView(1, 10, new Point(15, 25),
                new FileBuffer(new String[] {"hallo", ""}, "test"));
        assertEquals(gv.getNextFocus(1), 1);
        assertEquals(fbv.getPreviousFocus(2), 1);
    }

    @Test
    void closeView() throws IOException {
        GameView gv = new GameView(10, 20, new Point(5, 15));
        gv.setPosition(1);
        FileBufferView fbv = new FileBufferView(1, 10, new Point(15, 25),
                new FileBuffer(new String[] {"hallo", ""}, "test"));
        fbv.setPosition(1);
        TerminalHandler th1 = new TerminalHandler();
        assertNull(gv.closeView(1, th1));
        assertNull(fbv.closeView(1, th1));
    }

    @Test
    void testRotateView() {
        FileBufferView fbv = new FileBufferView(1, 10, new Point(15, 25),
                new FileBuffer(new String[] {"hallo", ""}, "test"));
        fbv.setPosition(1);
        assertEquals(fbv.rotateView(1, 1), fbv);
        assertEquals(fbv.rotateSiblings(1,1, 2, null), fbv);
        assertEquals(fbv.rotateSiblingsFlip(1,1, 2, null), fbv);
        assertEquals(fbv.rotateNonSiblings(1, 1, null, null, null), fbv);
        assertEquals(fbv.rotateNonSiblingsPromote(1, 1, null, null, null), fbv);
        assertEquals(fbv.flip(), fbv);
    }

    @Test
    void testInsertViews() {
        FileBufferView fbv = new FileBufferView(1, 10, new Point(15, 25),
                new FileBuffer(new String[] {"hallo", ""}, "test"));
        fbv.setPosition(1);
        GameView gv = new GameView(1, 1, new Point(1, 1));
        CompositeLayout result = (CompositeLayout) fbv.insertViews(1, null, new View[] {gv});
        assertEquals(result.getClass(), SideBySideLayout.class);
        assertArrayEquals(result.getSubLayouts(), new Layout[] {fbv, gv});
        assertEquals(fbv.insertViews(2,null, new View[] {gv}), fbv);
    }

    @Test
    void testUpdateSize() {
        GameView gv = new GameView(10, 20, new Point(5, 15));
        gv.setPosition(1);
        gv.updateSize(20, 10, new Point(10, 10));
        assertEquals(gv.getHeigth(), 20);
        assertEquals(gv.getWidth(), 10);
        assertEquals(gv.getLeftUpperCorner(), new Point(10, 10));
    }
}