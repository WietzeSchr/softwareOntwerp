import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class SideBySideLayoutTest {
    @Test
    void sideBySideLayoutTest() {
        // Tests voor constructors
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        sbsl1.initViewPosition(1);
        assertEquals(sbsl1.getSubLayouts()[0], fbv1);
        assertEquals(sbsl1.getSubLayouts()[1], fbv2);
        assertEquals(fbv1.getPosition(), 1);
        assertEquals(fbv2.getPosition(), 2);
        // Tests voor update size (+ calcSubSize + calcLeftUpperCorner)
        sbsl1.updateSize(10,20, new Point(1,1));
        assertEquals(sbsl1.getHeigth(), 10);
        assertEquals(sbsl1.getWidth(), 20);
        assertEquals(sbsl1.getLeftUpperCorner(), new Point(1,1));
        assertEquals(fbv1.getHeigth(), 10);
        assertEquals(fbv1.getWidth(), 10);
        assertEquals(fbv1.getLeftUpperCorner(), new Point(1,1));
        assertEquals(fbv2.getHeigth(), 10);
        assertEquals(fbv2.getWidth(), 10);
        assertEquals(fbv2.getLeftUpperCorner(), new Point(1,11));
        assertEquals(sbsl1.calcSubSize(), new Point(10,10));
        // Tests voor rotateView

    }
}