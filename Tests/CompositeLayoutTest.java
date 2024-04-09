import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.awt.*;
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

    }

    @Test
    void testSetters() {

    }

    @Test
    void testCountSubLayouts() {

    }

    @Test
    void testCloseView() {

    }

    @Test
    void testRotateView() {

    }

    @Test
    void testInitViewPositions() {

    }

    @Test
    void testGetFocusedView() {

    }

    @Test
    void testContains() {

    }

    @Test
    void testCountViews() {

    }

    @Test
    void testUpdateSize() {

    }
}