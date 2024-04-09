import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class StackedLayoutTest {
    @Test
    void stackedLayoutTest() {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        sl1.initViewPosition(1);
        assertEquals(sl1.getSubLayouts()[0], fbv1);
        assertEquals(sl1.getSubLayouts()[1], fbv2);
        assertEquals(fbv1.getPosition(), 1);
        assertEquals(fbv2.getPosition(), 2);
        // Tests voor update size (+ calcSubSize + calcLeftUpperCorner)
        sl1.updateSize(10,20, new Point(1,1));
        assertEquals(sl1.getHeigth(), 10);
        assertEquals(sl1.getWidth(), 20);
        assertEquals(sl1.getLeftUpperCorner(), new Point(1,1));
        assertEquals(fbv1.getHeigth(), 5);
        assertEquals(fbv1.getWidth(), 20);
        assertEquals(fbv1.getLeftUpperCorner(), new Point(1,1));
        assertEquals(fbv2.getHeigth(), 5);
        assertEquals(fbv2.getWidth(), 20);
        assertEquals(fbv2.getLeftUpperCorner(), new Point(6,1));
        assertEquals(sl1.calcSubSize(), new Point(5,20));
    }

    @Test
    void testConstructor() {

    }

    @Test
    void testSetters() {

    }

    @Test
    void testRotateSiblings() {

    }

    @Test
    void testRotateSiblingsFlip() {

    }

    @Test
    void testRotateNonSiblings() {

    }

    @Test
    void testRotateNonSiblingsPromote() {

    }

    @Test
    void testOpenNewGame() {

    }

    @Test
    void testCalcSubSize() {

    }

    @Test
    void testCalcLeftUpperCorner() {

    }
}