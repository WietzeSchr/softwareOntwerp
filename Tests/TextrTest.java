import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
public class TextrTest {

    @Test
    void testInspectBufferContent() {

    }

    @Test
    void testEditBufferContent() {
        File f1 = new File("test1", new FileBuffer(new String[] {"rij1", "rij2","rij3"}));
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        File f2 = new File("test2", new FileBuffer(new String[] {"t", "te", "tes", "test"}));
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        File f3 = new File("test3", new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}));
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});
        Textr test1 = new Textr("\n", sl1);
        test1.initViewPositions();
        test1.updateSize(20, 40);
        // Tests Add New Char
        test1.getFocusedView().getBuffer().setInsertionPoint(new Point(1,5));
        test1.addNewChar('!');
        assertEquals(fbv1.getContent()[0], "rij1!");
        // Tests Add New Line Break
        test1.addNewLineBreak();
        assertEquals(fbv1.getContent().length, 4);
        assertEquals(fbv1.getContent()[1], "");
        // Tests Delete Char
    }

    @Test
    void testChangeFocus() {

    }

    @Test
    void testCloseBuffer() {

    }

    @Test
    void testSafeBuffer() {

    }

    @Test
    void testRearrangeLayout() {

    }
}
