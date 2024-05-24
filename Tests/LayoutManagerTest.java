import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class LayoutManagerTest {

    @Test
    void testConstructor() throws IOException {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2"}, "test1");
        FileBufferView fbv1 = new FileBufferView(10,10,new Point(1,1), f1);
        LayoutManager lm1 = new LayoutManager(fbv1, 1, "\n");

        assertEquals(lm1.getLayout(), fbv1);
        assertEquals(lm1.getFocus(), 1);
        assertEquals(lm1.getNewLine(), "\n");
        assertEquals(lm1.getFocusedView(), fbv1);
        assertEquals(lm1.getCursor(), new Point(1,1));
        assertEquals(lm1.getDelay(), 0);
        assertEquals(lm1.getCurrentBuffer(), f1);
        String pathName = lm1.getCurrentBuffer().getPathString();
        assertEquals(lm1.getBufferByName(pathName), f1);
    }



    @Test
    void testEditContent() throws IOException {
        // FileBufferView
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2"}, "test1");
        FileBufferView fbv1 = new FileBufferView(10,10,new Point(1,1), f1);
        LayoutManager lm1 = new LayoutManager(fbv1, 1, "\n");

        assertArrayEquals(lm1.getCurrentBuffer().getContent(), new String[] {"rij1", "rij2"});
        lm1.enterPressed();
        assertEquals(lm1.getCursor(), new Point(2,1));
        assertArrayEquals(lm1.getCurrentBuffer().getContent(), new String[] {"", "rij1", "rij2"});
        lm1.addNewChar('a');
        assertArrayEquals(lm1.getCurrentBuffer().getContent(), new String[] {"", "arij1", "rij2"});
        assertEquals(lm1.getCursor(), new Point(2,2));
        lm1.deleteChar();
        assertArrayEquals(lm1.getCurrentBuffer().getContent(), new String[] {"", "rij1", "rij2"});
        assertEquals(lm1.getCursor(), new Point(2,1));
        lm1.arrowPressed(Direction.SOUTH);
        assertEquals(lm1.getCursor(), new Point(3,1));

        //GameView
        GameView gv1 = new GameView(20, 30, new Point(1,1));
        lm1 = new LayoutManager(gv1, 1, "\n");
        gv1.getGame().setSnake(null);
        lm1.enterPressed();
        assertNotNull(gv1.getGame().getSnake());
    }



    @Test
    void testAddView() throws IOException {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2"}, "test1");
        FileBufferView fbv1 = new FileBufferView(10,10,new Point(1,1),f1 );
        LayoutManager lm1 = new LayoutManager(fbv1, 1, "\n");


        //openView
        GameView gv1 = new GameView(20, 30, new Point(1,1));
        lm1.openViews(new View[] {gv1});
        assertEquals(lm1.getFocusedView(), fbv1);
        assertInstanceOf(SideBySideLayout.class, lm1.getLayout());
        assertEquals(lm1.getLayout().getSubLayouts()[0], fbv1);
        assertEquals(lm1.getLayout().getSubLayouts()[1], gv1);

        //closeView
        assertEquals(lm1.getFocusedView(), fbv1);
        lm1.changeFocusNext();
        lm1.closeView(null);
        assertEquals(fbv1, lm1.getLayout());

        // Duplicate
        lm1.duplicateView();
        assertInstanceOf(SideBySideLayout.class, lm1.getLayout());
        assertEquals(lm1.getFocusedView(), fbv1);
        lm1.changeFocusNext();
        assertEquals(lm1.getFocusedView(), lm1.getLayout().getSubLayouts()[1]);
        assertEquals(lm1.getCurrentBuffer(), f1);
        lm1.closeView(null);

        // GameView
        lm1.openGameView();
        assertInstanceOf(SideBySideLayout.class, lm1.getLayout());
        assertEquals(lm1.getFocusedView(), fbv1);
        lm1.changeFocusNext();
        assertEquals(lm1.getFocusedView(), lm1.getLayout().getSubLayouts()[1]);
        assertInstanceOf(GameView.class, lm1.getFocusedView());
        lm1.closeView(null);

        // DirectoryView
        lm1.openDirectoryView();
        assertInstanceOf(SideBySideLayout.class, lm1.getLayout());
        assertEquals(lm1.getFocusedView(), fbv1);
        lm1.changeFocusNext();
        assertEquals(lm1.getFocusedView(), lm1.getLayout().getSubLayouts()[1]);
        assertInstanceOf(DirectoryView.class, lm1.getFocusedView());
        lm1.closeView(null);

        //close last view
        lm1.closeView(null);
        assertNull(lm1.getLayout());
    }

    @Test
    void testParseJson() throws IOException {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2"}, "test1");
        FileBufferView fbv1 = new FileBufferView(10,10,new Point(1,1),f1 );
        LayoutManager lm1 = new LayoutManager(fbv1, 1, "\n");

        //parse Fail
        lm1.parseJson();
        assertEquals(lm1.getLayout(), fbv1);

        //parse Succes
        lm1.openDirectoryView();
        lm1.changeFocusNext();
        assertInstanceOf(DirectoryView.class, lm1.getFocusedView());
//        lm1.parseJson();
//        assertEquals(lm1.getLayout().countViews(), 3);
    }

    @Test
    void testReplace() throws IOException {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2"}, "test1");
        FileBufferView fbv1 = new FileBufferView(10,10,new Point(1,1),f1 );
        LayoutManager lm1 = new LayoutManager(fbv1, 1, "\n");
        GameView gv1 = new GameView(10, 10, new Point(1,1));

        assertEquals(lm1.getLayout(), fbv1);
        lm1.replace(fbv1, gv1);
        assertEquals(lm1.getLayout(), gv1);
    }

    @Test
    void testRearangeLayout() throws IOException {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2"}, "test1");
        FileBufferView fbv1 = new FileBufferView(10,10,new Point(1,1),f1 );
        GameView gv1 = new GameView(20, 30, new Point(1,1));
        SideBySideLayout sbs1 = new SideBySideLayout(20, 60,
                                                    new Point(1,1),  new Layout[] {fbv1, gv1});
        LayoutManager lm1 = new LayoutManager(sbs1, 1, "\n");

        //focus
        assertEquals(lm1.getFocusedView(), fbv1);
        lm1.changeFocusPrevious();
        assertEquals(lm1.getFocusedView(), fbv1);
        lm1.changeFocusNext();
        assertEquals(lm1.getFocusedView(), gv1);
        lm1.changeFocusNext();
        assertEquals(lm1.getFocusedView(), gv1);
        lm1.changeFocusPrevious();
        assertEquals(lm1.getFocusedView(), fbv1);

        //rotate
        assertEquals(lm1.getLayout(), sbs1);
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{fbv1, gv1});
        lm1.rotateView(1);
        assertInstanceOf(StackedLayout.class, lm1.getLayout());
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{gv1, fbv1});
        lm1.rotateView(1);
        assertInstanceOf(SideBySideLayout.class, lm1.getLayout());
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{gv1, fbv1});
        lm1.rotateView(1);
        assertInstanceOf(StackedLayout.class, lm1.getLayout());
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{fbv1, gv1});
        lm1.rotateView(1);
        assertInstanceOf(SideBySideLayout.class, lm1.getLayout());
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{fbv1, gv1});

        lm1.rotateView(-1);
        assertInstanceOf(StackedLayout.class, lm1.getLayout());
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{fbv1, gv1});
        lm1.rotateView(-1);
        assertInstanceOf(SideBySideLayout.class, lm1.getLayout());
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{gv1, fbv1});
        lm1.rotateView(-1);
        assertInstanceOf(StackedLayout.class, lm1.getLayout());
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{gv1, fbv1});
        lm1.rotateView(-1);
        assertInstanceOf(SideBySideLayout.class, lm1.getLayout());
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{fbv1, gv1});
    }

    @Test
    void testUndoRedo() throws IOException {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2"}, "test1");
        FileBufferView fbv1 = new FileBufferView(10,10, new Point(1,1),f1 );
        LayoutManager lm1 = new LayoutManager(fbv1, 1, "\n");
        lm1.undo();
        assertArrayEquals(lm1.getCurrentBuffer().getContent(), new String[] {"rij1", "rij2"});
        lm1.redo();
        assertArrayEquals(lm1.getCurrentBuffer().getContent(), new String[] {"rij1", "rij2"});
        lm1.addNewChar('a');
        assertArrayEquals(lm1.getCurrentBuffer().getContent(), new String[] {"arij1", "rij2"});
        lm1.undo();
        assertArrayEquals(lm1.getCurrentBuffer().getContent(), new String[] {"rij1", "rij2"});

    }


}
