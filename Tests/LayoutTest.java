import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class LayoutTest {

    @Test
    void testConstructor() {
        FileBufferView fbv1 = new FileBufferView(10, 20, new Point(1, 1), new FileBuffer(new String[] {""}, "1"));
        assertEquals(fbv1.getHeigth(), 10);
        assertEquals(fbv1.getWidth(), 20);
        assertEquals(fbv1.getLeftUpperCorner(), new Point(1, 1));
        assertNull(fbv1.getParent());
    }

    @Test
    void testSetter() {
        FileBufferView fbv1 = new FileBufferView(10, 20, new Point(1, 1), new FileBuffer(new String[] {""}, "1"));
        fbv1.setHeigth(8);
        fbv1.setWidth(7);
        fbv1.setLeftUpperCorner(new Point(4,2));
        fbv1.setParent(new StackedLayout(1, 1, new Point(1, 1), new Layout[]{}));
        assertEquals(fbv1.getHeigth(), 8);
        assertEquals(fbv1.getWidth(), 7);
        assertEquals(fbv1.getLeftUpperCorner(), new Point(4, 2));
        assertEquals(fbv1.getParent().getClass(), StackedLayout.class);
    }

    @Test
    void testGetNePrFocus() {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});

        assertEquals(sl1.getNextFocus(3), 3);
        assertEquals(sl1.getNextFocus(2), 3);
        assertEquals(sl1.getPreviousFocus(1), 1);
        assertEquals(sl1.getPreviousFocus(2), 1);
    }

    @Test
    void testArrowPressed() {   // Tested in scenario class

    }

    @Test
    void testCloseView() throws IOException {
        FileBuffer f1 = new FileBuffer(new String[]{"rij1", "rij2", "rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1, 1, new Point(1, 1), f1);
        FileBuffer f2 = new FileBuffer(new String[]{"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1, 1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[]{fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[]{"h", "ha", "hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1, 1, new Point(1, 1), f3);
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1, 1), new Layout[]{sbsl1, fbv3});
        TerminalHandler th1 = new TerminalHandler();
        sl1.initViewPosition(1);
        sl1.updateSize(20, 20, new Point(1, 1));
        CompositeLayout result = (CompositeLayout) sl1.closeView(1, th1);
        assertEquals(result.getSubLayouts()[0].getHeigth(), 10);
        assertEquals(result.getSubLayouts()[1].getHeigth(), 10);
        assertEquals(result.getSubLayouts()[0].getWidth(), 20);
        assertEquals(result.getSubLayouts()[1].getWidth(), 20);
        assertEquals(result.getSubLayouts()[0].getLeftUpperCorner(), new Point(1, 1));
        assertEquals(result.getSubLayouts()[1].getLeftUpperCorner(), new Point(11, 1));
        assertArrayEquals(result.getSubLayouts(), new Layout[]{fbv2, fbv3});
    }

    @Test
    void testNewFocus() {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});
        assertEquals(sl1.getNewFocus(4), 3);
        assertEquals(sl1.getNewFocus(3), 3);
    }

    @Test
    void testNewGame() {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});
        sl1.updateSize(20, 30, new Point(1, 1));
        sl1.initViewPosition(1);
        StackedLayout result = (StackedLayout) sl1.newGame(2);
        assertEquals(result.getSubLayouts()[0].getClass(), SideBySideLayout.class);
        assertEquals(result.getSubLayouts()[1], fbv3);
        sbsl1 = (SideBySideLayout) result.getSubLayouts()[0];
        assertEquals(sbsl1.countSubLayouts(), 3);
        assertEquals(sbsl1.getSubLayouts()[0], fbv1);
        assertEquals(sbsl1.getSubLayouts()[1], fbv2);
        assertEquals(sbsl1.getSubLayouts()[2].getClass(), GameView.class);
        assertEquals(result.getSubLayouts()[0].getHeigth(), 10);
        assertEquals(result.getSubLayouts()[0].getWidth(), 30);
        assertEquals(result.getSubLayouts()[0].getLeftUpperCorner(), new Point(1, 1));
        assertEquals(result.getSubLayouts()[1].getHeigth(), 10);
        assertEquals(result.getSubLayouts()[1].getWidth(), 30);
        assertEquals(result.getSubLayouts()[1].getLeftUpperCorner(), new Point(11, 1));
        assertEquals(sbsl1.getSubLayouts()[0].getHeigth(), 10);
        assertEquals(sbsl1.getSubLayouts()[0].getWidth(), 10);
        assertEquals(sbsl1.getSubLayouts()[0].getLeftUpperCorner(), new Point(1, 1));
        assertEquals(sbsl1.getSubLayouts()[1].getHeigth(), 10);
        assertEquals(sbsl1.getSubLayouts()[1].getWidth(), 10);
        assertEquals(sbsl1.getSubLayouts()[1].getLeftUpperCorner(), new Point(1, 11));
        assertEquals(sbsl1.getSubLayouts()[2].getHeigth(), 10);
        assertEquals(sbsl1.getSubLayouts()[2].getWidth(), 10);
        assertEquals(sbsl1.getSubLayouts()[2].getLeftUpperCorner(), new Point(1, 21));
    }

    @Test
    void testNewBufferView() {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        GameView gv = new GameView(10, 10, new Point(1, 1));
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, gv});
        sl1.updateSize(20, 30, new Point(1, 1));
        sl1.initViewPosition(1);
        assertEquals(sl1.newBufferView(3), sl1);
        StackedLayout result = (StackedLayout) sl1.newBufferView(2);
        assertEquals(result.getSubLayouts()[0].getClass(), SideBySideLayout.class);
        assertEquals(result.getSubLayouts()[1], gv);
        sbsl1 = (SideBySideLayout) result.getSubLayouts()[0];
        assertEquals(sbsl1.countSubLayouts(), 3);
        assertEquals(sbsl1.getSubLayouts()[0], fbv1);
        assertEquals(sbsl1.getSubLayouts()[1], fbv2);
        assertEquals(sbsl1.getSubLayouts()[2].getClass(), FileBufferView.class);
        FileBufferView newView = (FileBufferView) sbsl1.getSubLayouts()[2];
        assertEquals(newView.getBuffer(), f2);
        assertEquals(result.getSubLayouts()[0].getHeigth(), 10);
        assertEquals(result.getSubLayouts()[0].getWidth(), 30);
        assertEquals(result.getSubLayouts()[0].getLeftUpperCorner(), new Point(1, 1));
        assertEquals(result.getSubLayouts()[1].getHeigth(), 10);
        assertEquals(result.getSubLayouts()[1].getWidth(), 30);
        assertEquals(result.getSubLayouts()[1].getLeftUpperCorner(), new Point(11, 1));
        assertEquals(sbsl1.getSubLayouts()[0].getHeigth(), 10);
        assertEquals(sbsl1.getSubLayouts()[0].getWidth(), 10);
        assertEquals(sbsl1.getSubLayouts()[0].getLeftUpperCorner(), new Point(1, 1));
        assertEquals(sbsl1.getSubLayouts()[1].getHeigth(), 10);
        assertEquals(sbsl1.getSubLayouts()[1].getWidth(), 10);
        assertEquals(sbsl1.getSubLayouts()[1].getLeftUpperCorner(), new Point(1, 11));
        assertEquals(sbsl1.getSubLayouts()[2].getHeigth(), 10);
        assertEquals(sbsl1.getSubLayouts()[2].getWidth(), 10);
        assertEquals(sbsl1.getSubLayouts()[2].getLeftUpperCorner(), new Point(1, 21));
    }
}