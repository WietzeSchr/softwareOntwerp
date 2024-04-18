import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class CompositeLayoutTest {

    @Test
    void compositeLayoutTest() throws IOException {
        // Tests voor de constructor
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});
        // Tests voor initViewPosition
        sl1.initViewPosition(1);
        assertEquals(sl1.countViews(), 3);
        assertEquals(fbv1.getPosition(), 1);
        assertEquals(fbv2.getPosition(), 2);
        assertEquals(fbv3.getPosition(), 3);
        // Tests voor getFocusedView
        assertEquals(sl1.getFocusedView(3), fbv3);
        assertNull(sbsl1.getFocusedView(3));
        // Tests voor countViews
        assertEquals(sl1.countViews(), 3);
        assertEquals(sbsl1.countViews(), 2);
        // Tests voor updateSize
        sl1.updateSize(20,40, new Point(1,1));
        assertEquals(fbv1.getHeigth(), 10);
        assertEquals(fbv1.getWidth(), 20);
        assertEquals(fbv1.getLeftUpperCorner(), new Point(1,1));
        assertEquals(fbv2.getHeigth(), 10);
        assertEquals(fbv2.getWidth(), 20);
        assertEquals(fbv2.getLeftUpperCorner(), new Point(1,21));
        assertEquals(fbv3.getHeigth(), 10);
        assertEquals(fbv3.getWidth(), 40);
        assertEquals(fbv3.getLeftUpperCorner(), new Point(11,1));
        // Tests voor closeBuffer
        sl1.closeView(1, sbsl1);
        assertEquals(sl1.countViews(), 2);
        sl1.initViewPosition(1);
        assertEquals(sl1.getSubLayouts()[0], fbv2);
        assertEquals(fbv2.getPosition(), 1);
        assertEquals(sl1.getSubLayouts()[1], fbv3);
        assertEquals(fbv3.getPosition(), 2);
    }

    @Test
    void testConstructor() {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});
        assertArrayEquals(sbsl1.getSubLayouts(), new Layout[] {fbv1, fbv2});
        assertArrayEquals(sl1.getSubLayouts(), new Layout[] {sbsl1, fbv3});
        assertEquals(sbsl1.countSubLayouts(), 2);
        assertEquals(sbsl1.countViews(), 2);
        assertEquals(sl1.countViews(), 3);
        assertEquals(sbsl1.countSubLayouts(), 2);
        assertEquals(fbv1.getParent(), sbsl1);
        assertEquals(sbsl1.getParent(), sl1);
        assertEquals(fbv2.getParent(), sbsl1);
    }

    @Test
    void testSetters() {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        FileBuffer f4 = new FileBuffer(new String[] {}, "");
        FileBufferView fbv4 = new FileBufferView(1, 1, new Point(1, 1), f4);
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});
        sbsl1.setSubLayouts(new Layout[] {fbv1, fbv2, fbv3});
        sl1.setSubLayouts(new Layout[] {sbsl1, fbv4});
        assertArrayEquals(sbsl1.getSubLayouts(), new Layout[] {fbv1, fbv2, fbv3});
        assertArrayEquals(sl1.getSubLayouts(), new Layout[] {sbsl1, fbv4});
    }

    @Test
    void testCloseView() throws IOException {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});
        sl1.initViewPosition(1);
        CompositeLayout result = (CompositeLayout) sl1.closeView(1, sbsl1);
        assertEquals(result.getClass(), StackedLayout.class);
        assertArrayEquals(result.getSubLayouts(), new Layout[] {fbv2, fbv3});
    }

    @Test
    void testRotateView() {
        FileBuffer f1 = new FileBuffer(new String[]{"rij1", "rij2", "rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1, 1, new Point(1, 1), f1);
        FileBuffer f2 = new FileBuffer(new String[]{"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1, 1), f2);
        FileBuffer f3 = new FileBuffer(new String[]{"dit", "is", "een", "test"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1, 1, new Point(1, 1), f3);
        FileBuffer f4 = new FileBuffer(new String[]{""}, "4");
        FileBufferView fbv4 = new FileBufferView(1, 1, new Point(1, 1), f4);
        FileBuffer f5 = new FileBuffer(new String[] {}, "5");
        FileBufferView fbv5 = new FileBufferView(1, 1, new Point(1,1), f5);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[] {fbv3,fbv4, fbv5});
        StackedLayout sl2 = new StackedLayout(1, 1, new Point(1, 1), new Layout[] {fbv2, sbsl1});
        SideBySideLayout sbsl3 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[] {fbv1, sl2});
        sbsl3.initViewPosition(1);
        sbsl3.updateSize(20, 20, new Point(1, 1));
        sbsl3.rotateView(1, 2);
        CompositeLayout result = sbsl3.rotateNonSiblings(1, 2, fbv3, sl2, sbsl1);
        assertEquals(result.getClass(), SideBySideLayout.class);
        assertEquals(result.getSubLayouts()[0], fbv1);
        assertEquals(result.getSubLayouts()[1].getClass(), StackedLayout.class);
        sl2 = (StackedLayout) result.getSubLayouts()[1];
        assertEquals(sl2.getSubLayouts()[0], fbv2);
        assertEquals(sl2.getSubLayouts()[1], fbv3);
        assertEquals(sl2.getSubLayouts()[2].getClass(), SideBySideLayout.class);
        assertEquals(sl2.countSubLayouts(), 3);
        sbsl1 = (SideBySideLayout) sl2.getSubLayouts()[2];
        assertEquals(sbsl1.countSubLayouts(), 2);
        assertEquals(sbsl1.getSubLayouts()[0], fbv4);
        assertEquals(sbsl1.getSubLayouts()[1], fbv5);
    }

    @Test
    void testGetFocusedView() {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});
        sl1.initViewPosition(1);
        assertEquals(sl1.getFocusedView(2), fbv2);
    }

    @Test
    void testContains() {
        FileBuffer f1 = new FileBuffer(new String[]{"rij1", "rij2", "rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1, 1, new Point(1, 1), f1);
        FileBuffer f2 = new FileBuffer(new String[]{"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1, 1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[]{fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[]{"h", "ha", "hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1, 1, new Point(1, 1), f3);
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1, 1), new Layout[]{sbsl1, fbv3});
        assertTrue(sl1.contains(fbv3));
        assertFalse(sbsl1.contains(fbv3));
    }

    @Test
    void testUpdateSize() {
        FileBuffer f1 = new FileBuffer(new String[]{"rij1", "rij2", "rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1, 1, new Point(1, 1), f1);
        FileBuffer f2 = new FileBuffer(new String[]{"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1, 1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1, 1), new Layout[]{fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[]{"h", "ha", "hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1, 1, new Point(1, 1), f3);
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1, 1), new Layout[]{sbsl1, fbv3});
        sl1.updateSize(10, 20, new Point(1, 1));
        assertEquals(sl1.getHeigth(), 10);
        assertEquals(sl1.getWidth(), 20);
        assertEquals(sl1.getLeftUpperCorner(), new Point(1,1));
        assertEquals(sbsl1.getHeigth(), 5);
        assertEquals(sbsl1.getWidth(), 20);
        assertEquals(sbsl1.getLeftUpperCorner(), new Point(1,1));
        assertEquals(fbv1.getHeigth(), 5);
        assertEquals(fbv1.getWidth(), 10);
        assertEquals(fbv1.getLeftUpperCorner(), new Point(1,1));
        assertEquals(fbv2.getHeigth(), 5);
        assertEquals(fbv2.getWidth(), 10);
        assertEquals(fbv2.getLeftUpperCorner(), new Point(1,11));
        assertEquals(fbv3.getHeigth(), 5);
        assertEquals(fbv3.getWidth(), 20);
        assertEquals(fbv3.getLeftUpperCorner(), new Point(6,1));
    }
}