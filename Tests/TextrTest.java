import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
public class TextrTest {

    private Textr makeTestTextr(){
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3", "rij4", "rij5", "1234567890abc"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});
        Textr result = new Textr("\n", sl1);
        result.initViewPositions();
        result.updateSize(20,40);
        return result;
    }
    @Test
    void testInspectBufferContent() {
        Textr test1 = makeTestTextr();
        test1.updateSize(10, 20);
        //testMoveInsertionPoint
        FileBufferView focus = (FileBufferView)test1.getFocusedView();
        focus.setInsertionPoint(new Point(1, 1));
        test1.arrowPressed(Direction.NORD);
        assertEquals(focus.getInsertionPoint(), new Point(1,1));
        assertEquals(focus.getInsertionPoint(), focus.getCursor());
        test1.arrowPressed(Direction.WEST);
        assertEquals(focus.getInsertionPoint(), new Point(1,1));
        assertEquals(focus.getInsertionPoint(), focus.getCursor());
        test1.arrowPressed(Direction.SOUTH);
        assertEquals(focus.getInsertionPoint(), new Point(2,1));
        assertEquals(focus.getInsertionPoint(), focus.getCursor());
        test1.arrowPressed(Direction.EAST);
        assertEquals(focus.getInsertionPoint(), new Point(2,2));
        assertEquals(focus.getInsertionPoint(), focus.getCursor());

        test1.changeFocusNext();
        focus = (FileBufferView)test1.getFocusedView();
        focus.setInsertionPoint(new Point(10,12));
        assertEquals(focus.getInsertionPoint(), new Point(4,5));
        test1.arrowPressed(Direction.SOUTH);
        assertEquals(focus.getInsertionPoint(), new Point(4,5));
        test1.arrowPressed(Direction.EAST);
        assertEquals(focus.getInsertionPoint(), new Point(4,5));
        test1.arrowPressed(Direction.NORD);
        assertEquals(focus.getInsertionPoint(), new Point(3,4));
        test1.arrowPressed(Direction.WEST);
        test1.arrowPressed(Direction.WEST);
        test1.arrowPressed(Direction.WEST);
        assertEquals(focus.getInsertionPoint(), new Point(3, 1));
        //testScrollStates
        test1.changeFocusPrevious();
        focus = (FileBufferView)test1.getFocusedView();
        assertEquals(focus.getHorizontalScrollState(), 1);
        assertEquals(focus.getVerticalScrollState(), 1);

        focus.setInsertionPoint(new Point(5,1));
        test1.arrowPressed(Direction.SOUTH);
        assertEquals(focus.getCursor(), new Point(1,1));
        assertEquals(focus.getVerticalScrollState(), 6);

        focus.setInsertionPoint(new Point(6,10));
        test1.arrowPressed(Direction.EAST);
        assertEquals(focus.getCursor(), new Point(1,1));
        assertEquals(focus.getHorizontalScrollState(), 11);
        String[] show = focus.makeShow();
        assertEquals(show[0], "abc");

        test1.arrowPressed(Direction.NORD);
        assertEquals(focus.getHorizontalScrollState(), 1);
        assertEquals(focus.getVerticalScrollState(), 1);
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
        FileBufferView focus = (FileBufferView)test1.getFocusedView();
        focus.setInsertionPoint(new Point(1,5));
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
        Textr test1 = makeTestTextr();
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

        //testClose cleanBuffer
        FileBufferView focus = (FileBufferView)test1.getFocusedView();
        assertFalse(focus.getBuffer().getDirty());
        test1.closeView();
        focus = (FileBufferView)test1.getFocusedView();
        StackedLayout newLayout = new StackedLayout(1, 1, new Point(1,1), new Layout[] {fbv2, fbv3});
        assertEquals(newLayout, test1.getLayout());
        assertEquals(focus, fbv2);
        assertEquals(focus.getHeigth(), 10);
        assertEquals(focus.getWidth(), 40);

        //test DirtyBuffer
    }

    @Test
    void testSafeBuffer() throws IOException {
        FileBuffer fb1 = new FileBuffer(new String[] {"test"}, "Tests\\testFile.txt");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),fb1);
        Textr test1 = new Textr("\n", fbv1);
        test1.initViewPositions();
        test1.updateSize(20, 40);
        File f1 = new File("Tests\\testFile.txt");

        FileBuffer focus = ((FileBufferView)test1.getFocusedView()).getBuffer();
        assertFalse(focus.getDirty());
        assertEquals(Arrays.toString(f1.load("\n")), Arrays.toString(fbv1.getContent()));
        test1.addNewChar('a');
        assertTrue(focus.getDirty());
        test1.saveBuffer();
        assertEquals(Arrays.toString(f1.load("\n")), Arrays.toString(fbv1.getContent()));
        assertFalse(focus.getDirty());

        test1.deleteChar();
        test1.saveBuffer();
    }

    @Test
    void testRearrangeLayout() {

    }

    @Test
    void testDuplicateView() {
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

        FileBufferView focus = (FileBufferView) test1.getFocusedView();
        FileBufferView fbv1Dup = new FileBufferView(1, 1, new Point(1, 1), focus.getBuffer());
        SideBySideLayout sbsl2 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv1Dup, fbv2});

        test1.duplicateView();
        //print statements zijn tijdelijk om de structuur van de tree te bekijken
        CompositeLayout layer1 = (CompositeLayout) test1.getLayout();
        System.out.println(STR."Root: \{layer1}");
        Layout[] layer2 = layer1.getSubLayouts();
        System.out.println(STR."Layer2: \{Arrays.toString(layer2)}");
        CompositeLayout sbs1 = (CompositeLayout) layer2[0];
        Layout[] layer3 = sbs1.getSubLayouts();
        System.out.println(STR."Layer3: \{Arrays.toString(layer3)}");
        CompositeLayout sbs2 = (CompositeLayout) layer3[0];
        Layout[] layer4 = sbs2.getSubLayouts();
        System.out.println(STR."Layer4: \{Arrays.toString(layer4)}");
        //assertEquals(test1.getLayout(), new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl2, fbv3}));

    }

    @Test
    void testNewGame() {

    }

    @Test
    void testUndoRedo() {

    }
}
