import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class TextrTest {

    @Test
    void testInspectBufferContent() {

    }

    @Test
    void testEditBufferContent() {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});
        Textr test1 = new Textr("\n", sl1);
        test1.initViewPositions();
        test1.updateSize(20, 40);
        // Tests Add New Char
        test1.getFocusedView().setInsertionPoint(new Point(1,5));
        test1.addNewChar('!');
        assertEquals(fbv1.getContent()[0], "rij1!");
        // Tests Add New Line Break
        test1.addNewLineBreak();
        assertEquals(fbv1.getContent().length, 4);
        assertEquals(fbv1.getContent()[0], "rij1!");
        assertEquals(fbv1.getContent()[1], "");
        // Tests Delete Char
        test1.deleteChar();
        assertEquals(fbv1.getContent().length, 3);
        assertEquals(fbv1.getContent()[0], "rij1!");
        assertEquals(fbv1.getContent()[1], "rij2");
        assertEquals(fbv1.getContent()[2], "rij3");
        test1.deleteChar();
        assertEquals(fbv1.getContent().length, 3);
        assertEquals(fbv1.getContent()[0], "rij1");
        assertEquals(fbv1.getContent()[1], "rij2");
        assertEquals(fbv1.getContent()[2], "rij3");
    }

    @Test
    void testChangeFocus() {
        // Initialisatie Textr
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});
        Textr test1 = new Textr("\n", sl1);
        test1.initViewPositions();
        test1.updateSize(20, 40);
        // Tests
        test1.changeFocusNext();
        assertEquals(test1.getFocus(), 2);
        test1.changeFocusNext();
        assertEquals(test1.getFocus(), 3);
        test1.changeFocusNext();
        assertEquals(test1.getFocus(), 1);
        test1.changeFocusPrevious();
        assertEquals(test1.getFocus(), 3);
        test1.changeFocusPrevious();
        assertEquals(test1.getFocus(), 2);
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
