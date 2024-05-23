import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class LayoutManagerTest {

    @Test
    void testConstructor() throws IOException {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1), f1);
        LayoutManager lm1 = new LayoutManager(fbv1, 1, "\n");

        assertEquals(lm1.getLayout(), fbv1);
        assertEquals(lm1.getFocus(), 1);
        assertEquals(lm1.getNewLine(), "\n");
        assertEquals(lm1.getFocusedView(), fbv1);
        assertEquals(lm1.getCursor(), new Point(1,1));
        assertEquals(lm1.getDelay(), 0);
        assertEquals(lm1.getCurrentBuffer(), f1);
        //assertEquals(lm1.getBufferByName("test1"), f1);
    }



    @Test
    void testEditContent() throws IOException {
        // FileBufferView
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        LayoutManager lm1 = new LayoutManager(fbv1, 1, "\n");

        assertArrayEquals(lm1.getCurrentBuffer().getContent(), new String[] {"rij1", "rij2"});
        lm1.enterPressed();
        assertEquals(lm1.getCursor(), new Point(2,1));
        assertArrayEquals(lm1.getCurrentBuffer().getContent(), new String[] {"", "rij1", "rij2"});
        lm1.addNewChar('a');
        assertArrayEquals(lm1.getCurrentBuffer().getContent(), new String[] {"", "arij1", "rij2"});
        lm1.arrowPressed(Direction.SOUTH);
        //lm1.deleteChar();
        //assertArrayEquals(lm1.getCurrentBuffer().getContent(), new String[] {"arij1", "rij2"});

        GameView gv1 = new GameView(20, 30, new Point(1,1));
        lm1 = new LayoutManager(gv1, 1, "\n");
        gv1.getGame().setSnake(null);
        lm1.enterPressed();
        assertNotNull(gv1.getGame().getSnake());
    }

    @Test
    void testOpenViews() throws IOException {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        LayoutManager lm1 = new LayoutManager(fbv1, 1, "\n");

        GameView gv1 = new GameView(20, 30, new Point(1,1));
        lm1.openViews(new View[] {gv1});
        assertEquals(lm1.getFocusedView(), fbv1);
        assertTrue(lm1.getLayout() instanceof SideBySideLayout);
        assertEquals(lm1.getLayout().getSubLayouts()[0], fbv1);
        assertEquals(lm1.getLayout().getSubLayouts()[1], gv1);
    }

    @Test
    void testRearangeLayout() throws IOException {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
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
        assertTrue(lm1.getLayout() instanceof StackedLayout);
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{gv1, fbv1});
        lm1.rotateView(1);
        assertTrue(lm1.getLayout() instanceof SideBySideLayout);
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{gv1, fbv1});
        lm1.rotateView(1);
        assertTrue(lm1.getLayout() instanceof StackedLayout);
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{fbv1, gv1});
        lm1.rotateView(1);
        assertTrue(lm1.getLayout() instanceof SideBySideLayout);
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{fbv1, gv1});

        lm1.rotateView(-1);
        assertTrue(lm1.getLayout() instanceof StackedLayout);
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{fbv1, gv1});
        lm1.rotateView(-1);
        assertTrue(lm1.getLayout() instanceof SideBySideLayout);
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{gv1, fbv1});
        lm1.rotateView(-1);
        assertTrue(lm1.getLayout() instanceof StackedLayout);
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{gv1, fbv1});
        lm1.rotateView(-1);
        assertTrue(lm1.getLayout() instanceof SideBySideLayout);
        assertArrayEquals(lm1.getLayout().getSubLayouts(), new Layout[]{fbv1, gv1});
    }


}
