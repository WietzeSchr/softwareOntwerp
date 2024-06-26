import org.junit.jupiter.api.Test;

import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
        result.getLayoutManager().initViewPositions();
        result.getLayout().updateSize(20,40);
        return result;
    }
    @Test
    void testInspectBufferContent() {
        Textr test1 = makeTestTextr();
        test1.getLayout().updateSize(10, 20);
        //testMoveInsertionPoint
        FileBufferView focus = (FileBufferView)test1.getLayoutManager().getFocusedView();
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
        focus = (FileBufferView)test1.getLayoutManager().getFocusedView();
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
        focus = (FileBufferView)test1.getLayoutManager().getFocusedView();
        assertEquals(focus.getHorizontalScrollState(), 1);
        assertEquals(focus.getVerticalScrollState(), 1);

        focus.setInsertionPoint(new Point(4,1));
        test1.arrowPressed(Direction.SOUTH);
        assertEquals(focus.getCursor(), new Point(1,1));
        assertEquals(focus.getVerticalScrollState(), 5);

        focus.setInsertionPoint(new Point(6,9));
        test1.arrowPressed(Direction.EAST);
        assertEquals(focus.getCursor(), new Point(2,1));
        assertEquals(focus.getHorizontalScrollState(), 10);
        String[] show = focus.makeShow();
        assertEquals(show[1], "0abc");
        focus.setInsertionPoint(new Point(5,1));
        test1.arrowPressed(Direction.NORD);
        assertEquals(focus.getHorizontalScrollState(), 1);
        assertEquals(focus.getVerticalScrollState(), 1);
    }

    @Test
    void testEditBufferContent() throws FileNotFoundException {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});
        Textr test1 = new Textr("\n", sl1);
        test1.getLayoutManager().initViewPositions();
        test1.getLayout().updateSize(10, 20);

        // Tests Add New Char
        FileBufferView focus = (FileBufferView)test1.getLayoutManager().getFocusedView();
        focus.setInsertionPoint(new Point(1,5));
        test1.addNewChar('!');
        assertEquals(fbv1.getContent()[0], "rij1!");

        // Tests Add New Line Break
        test1.enterPressed();
        assertEquals(fbv1.getContent().length, 4);
        assertEquals(fbv1.getContent()[0], "rij1!");
        assertEquals(fbv1.getContent()[1], "");

        // Tests Delete Char
        test1.deleteChar();
        assertEquals(fbv1.getContent().length, 3);
        assertEquals(fbv1.getContent()[0], "rij1!");
        assertEquals(fbv1.getContent()[1], "rij2");
        assertEquals(fbv1.getContent()[2], "rij3");
        test1.arrowPressed(Direction.EAST);
        test1.arrowPressed(Direction.EAST);
        test1.arrowPressed(Direction.EAST);
        test1.arrowPressed(Direction.EAST);
        test1.arrowPressed(Direction.EAST);
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
        assertEquals(test1.getFocus(), 3);
        test1.changeFocusPrevious();
        assertEquals(test1.getFocus(), 2);
        test1.changeFocusPrevious();
        assertEquals(test1.getFocus(), 1);
        test1.changeFocusPrevious();
        assertEquals(test1.getFocus(), 1);
    }

    @Test
    void testCloseBuffer() throws IOException {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});
        Textr test1 = new Textr("\n", sl1);
        test1.getLayout().updateSize(20,40);

        //testClose cleanBuffer
        FileBufferView focus = (FileBufferView)test1.getLayoutManager().getFocusedView();
        assertFalse(focus.getBuffer().getDirty());
        test1.closeView(test1.getInputHandler());
        focus = (FileBufferView)test1.getLayoutManager().getFocusedView();
        StackedLayout newLayout = new StackedLayout(20, 40, new Point(1,1), new Layout[] {fbv2, fbv3});
        assertEquals(newLayout, test1.getLayout());
        assertEquals(focus, fbv2);
        assertEquals(focus.getHeigth(), 10);
        assertEquals(focus.getWidth(), 40);

        //test DirtyBuffer
    }

    @Test
    void testSafeBuffer() throws IOException {
        /*FileBuffer fb1 = new FileBuffer(new String[] {"test"}, "testFile.txt");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),fb1);
        Textr test1 = new Textr("\n", fbv1);
        test1.initViewPositions();
        test1.updateSize(20, 40);
        File f1 = new File("IdeaProjects/softwareOntwerp/Tests/testFile.txt");

        FileBuffer focus = ((FileBufferView)test1.getFocusedView()).getBuffer();
        assertFalse(focus.getDirty());
        assertEquals(Arrays.toString(f1.load("\n")), Arrays.toString(fbv1.getContent()));
        test1.addNewChar('a');
        assertTrue(focus.getDirty());
        test1.saveBuffer();
        assertEquals(Arrays.toString(f1.load("\n")), Arrays.toString(fbv1.getContent()));
        assertFalse(focus.getDirty());

        test1.deleteChar();
        test1.saveBuffer();*/
    }

    @Test
    void testRearrangeLayout() {

    }

    @Test
    void testDuplicateView() throws FileNotFoundException {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3", "rij4", "rij5"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});
        Textr test1 = new Textr("\n", sl1);
        test1.getLayoutManager().initViewPositions();
        test1.getLayout().updateSize(10, 20);

        //test duplicate
        FileBufferView focus = (FileBufferView) test1.getLayoutManager().getFocusedView();
        test1.duplicateView();
        Layout[] focusSiblings = focus.getParent().getSubLayouts();
        assertEquals(focusSiblings.length, 3);
        assertEquals(focusSiblings[0], fbv1);
        assertEquals(focusSiblings[2], fbv2);
        //test synchronisation of two Buffers
        FileBufferView focusDupe = (FileBufferView) focusSiblings[1];
        assertEquals(focus.getBuffer(), focusDupe.getBuffer());
        test1.addNewChar('a');
        assertEquals(focus.getBuffer().getContent(), focusDupe.getBuffer().getContent());
        //testScrolling
        focus.setInsertionPoint(new Point(4,1));
        test1.arrowPressed(Direction.SOUTH);
        assertEquals(focus.getVerticalScrollState(), 5);
        test1.changeFocusNext();
        assertEquals(test1.getLayoutManager().getFocusedView(), focusDupe);
        focusDupe.setInsertionPoint(new Point(2,1));
        assertEquals(focus.getBuffer().getRowCount(), 5);
        assertEquals(Arrays.toString(focus.makeShow()), Arrays.toString(new String[]{"rij5", null, null, null}));
        assertEquals(Arrays.toString(focusDupe.makeShow()), Arrays.toString(new String[] {"arij1", "rij2", "rij3", "rij4"}));
        test1.enterPressed();
        assertEquals(Arrays.toString(focus.makeShow()), Arrays.toString(new String[] {"rij5", null, null, null}));
        assertEquals(Arrays.toString(focusDupe.makeShow()), Arrays.toString(new String[] {"arij1", null, "rij2", "rij3"}));
        assertEquals(focus.getBuffer().getContent(), focusDupe.getBuffer().getContent());

    }

    @Test
    void testNewGame() throws IOException {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3", "rij4", "rij5"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        FileBuffer f2 = new FileBuffer(new String[] {"t", "te", "tes", "test"}, "test2");
        FileBufferView fbv2 = new FileBufferView(1, 1, new Point(1,1), f2);
        SideBySideLayout sbsl1 = new SideBySideLayout(1, 1, new Point(1,1), new Layout[] {fbv1, fbv2});
        FileBuffer f3 = new FileBuffer(new String[] {"h", "ha","hal", "hall", "hallo"}, "test3");
        FileBufferView fbv3 = new FileBufferView(1,1,new Point(1,1),f3 );
        StackedLayout sl1 = new StackedLayout(1, 1, new Point(1,1), new Layout[] {sbsl1, fbv3});
        Textr test1 = new Textr("\n", sl1);
        test1.getLayoutManager().initViewPositions();
        test1.getLayout().updateSize(80, 60);

        FileBufferView focus = (FileBufferView) test1.getLayoutManager().getFocusedView();
        test1.openGameView();
        test1.changeFocusNext();
        Layout[] focusSiblings = focus.getParent().getSubLayouts();
        assertEquals(focusSiblings.length, 3);
        assertEquals(focusSiblings[0], fbv1);
        assertEquals(focusSiblings[2], fbv2);
        GameView gameView = (GameView) test1.getLayoutManager().getFocusedView();
        assertEquals(gameView.getHeigth(), 40);
        assertEquals(gameView.getWidth(), 20);
        Snake snake = gameView.getGame().getSnake();
        assertEquals(snake.getHead(), new Point(19,14));
        assertEquals(gameView.getGame().getScore(), 0);
        test1.closeView(test1.getInputHandler());

        focus = (FileBufferView) test1.getLayoutManager().getFocusedView();
        assertEquals(focus, fbv2);
        assertEquals(focus.getParent().countViews(), 2);
        test1.changeFocusNext();

        test1.openGameView();
        test1.changeFocusNext();
        gameView = (GameView) test1.getLayoutManager().getFocusedView();
        assertEquals(gameView.getHeigth(), 40);
        assertEquals(gameView.getWidth(), 30);
        snake = gameView.getGame().getSnake();
        assertEquals(snake.getHead(), new Point(20,15));
    }

    @Test
    void testSnake() throws IOException {
        GameView gv1 = new GameView(20, 30, new Point(1,1));
        Textr test1 = new Textr("\n", gv1);
        test1.getLayoutManager().initViewPositions();
        //test updateSize
        Game g1 = gv1.getGame();
        Snake s1 = g1.getSnake();
        assertEquals(s1.getHead(), new Point(9, 14));
        test1.getLayout().updateSize(10, 20);
        assertEquals(((GameView)test1.getLayout()).getGame().getGrid().length, 9);
        assertEquals(((GameView)test1.getLayout()).getGame().getGrid()[0].length, 19);
        s1 = ((GameView)test1.getLayout()).getGame().getSnake();
        assertEquals(s1.getHead(), new Point(5, 10));
        test1.getLayout().updateSize(6,20);
        s1 = ((GameView)test1.getLayout()).getGame().getSnake();
        assertEquals(s1.getHead(), new Point(3, 10));
        test1.getLayout().updateSize(6, 8);
        s1 = ((GameView)test1.getLayout()).getGame().getSnake();
        assertEquals(s1.getHead(), new Point(3, 4));
        //test input
        gv1 = new GameView(20, 30, new Point(1,1));
        test1 = new Textr("\n", gv1);
        assertEquals(gv1.getGame().getSnake().getHead(), new Point(9, 14));
        int score = ((GameView)test1.getLayoutManager().getFocusedView()).getGame().getScore();
        assertEquals(score, 0);
        ((GameView)test1.getLayoutManager().getFocusedView()).getGame().setGridAt(0, new Point(9,15));
        test1.tick();
        score = ((GameView)test1.getLayoutManager().getFocusedView()).getGame().getScore();
        assertEquals(score, 1);
        s1 = gv1.getGame().getSnake();
        assertEquals(s1.getHead(), new Point(9, 15));
        ((GameView)test1.getLayoutManager().getFocusedView()).getGame().setGridAt(0, new Point(10,15));
        test1.arrowPressed(Direction.SOUTH);
        assertEquals(s1.getHead(), new Point(10, 15));
        score = ((GameView)test1.getLayoutManager().getFocusedView()).getGame().getScore();
        assertEquals(score, 2);
        test1.arrowPressed(Direction.NORD);
        assertEquals(s1.getHead(), new Point(10, 15));
        test1.arrowPressed(Direction.EAST);
        assertEquals(s1.getHead(), new Point(10, 16));
        test1.arrowPressed(Direction.SOUTH);
        assertEquals(s1.getHead(), new Point(11, 16));
        test1.arrowPressed(Direction.WEST);
        assertEquals(s1.getHead(), new Point(11, 15));

        //test eatApple
        score = ((GameView)test1.getLayoutManager().getFocusedView()).getGame().getScore();
        int snakeLength = ((GameView)test1.getLayoutManager().getFocusedView()).getGame().getSnake().getBody().size();
        ((GameView)test1.getLayoutManager().getFocusedView()).getGame().setGridAt(1, new Point(11,14));
        test1.tick();
        assertEquals(((GameView)test1.getLayoutManager().getFocusedView()).getGame().getScore(), score+11);
        assertEquals(((GameView)test1.getLayoutManager().getFocusedView()).getGame().getSnake().getBody().size(), snakeLength+1);

        //test gameOver
        test1.arrowPressed(Direction.NORD);
        test1.arrowPressed(Direction.EAST);
        assertNull(((GameView) test1.getLayoutManager().getFocusedView()).getGame().getSnake());

        gv1 = new GameView(20, 30, new Point(1,1));
        test1 = new Textr("\n", gv1);
        test1.arrowPressed(Direction.NORD);
        test1.tick();
        test1.tick();
        test1.tick();
        test1.tick();
        test1.tick();
        test1.tick();
        test1.tick();
        test1.tick();
        assertNull(((GameView) test1.getLayoutManager().getFocusedView()).getGame().getSnake());

        gv1 = new GameView(20, 30, new Point(1,1));
        test1 = new Textr("\n", gv1);
        test1.getLayout().updateSize(20, 2);
        assertNull(((GameView) test1.getLayoutManager().getFocusedView()).getGame().getSnake());
    }

    @Test
    void testUndoRedo() throws IOException {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3", "rij4", "rij5"}, "test1");
        FileBufferView fbv1 = new FileBufferView(1,1,new Point(1,1),f1 );
        Textr test1 = new Textr("\n", fbv1);
        test1.getLayoutManager().initViewPositions();
        test1.getLayout().updateSize(10, 20);

        //test undo/redo
        FileBufferView focus = (FileBufferView) test1.getLayoutManager().getFocusedView();
        test1.addNewChar('a');
        assertArrayEquals(focus.getBuffer().getContent(), new String[] {"arij1", "rij2","rij3", "rij4", "rij5"});
        assertTrue(focus.getBuffer().getDirty());
        test1.undo();
        assertArrayEquals(focus.getBuffer().getContent(), new String[] {"rij1", "rij2","rij3", "rij4", "rij5"});
        assertFalse(focus.getBuffer().getDirty());
        test1.redo();
        assertArrayEquals(focus.getBuffer().getContent(), new String[] {"arij1", "rij2","rij3", "rij4", "rij5"});
        assertTrue(focus.getBuffer().getDirty());
        test1.undo();
        assertArrayEquals(focus.getBuffer().getContent(), new String[] {"rij1", "rij2","rij3", "rij4", "rij5"});
        assertFalse(focus.getBuffer().getDirty());

        //test when saved
        test1.addNewChar('a');
        assertArrayEquals(focus.getBuffer().getContent(), new String[] {"arij1", "rij2","rij3", "rij4", "rij5"});
        test1.saveBuffer();
        test1.undo();
        assertArrayEquals(focus.getBuffer().getContent(), new String[] {"arij1", "rij2","rij3", "rij4", "rij5"});
        assertFalse(focus.getBuffer().getDirty());
        //assertInstanceOf(FileBufferView.EmptyEdit.class, focus.getLastEdit());
        test1.redo();
        assertArrayEquals(focus.getBuffer().getContent(), new String[] {"arij1", "rij2","rij3", "rij4", "rij5"});
        assertFalse(focus.getBuffer().getDirty());
        //assertInstanceOf(FileBufferView.EmptyEdit.class, focus.getLastEdit());
    }

    @Test
    void testOpenFile() throws IOException {
        FileBuffer f1 = new FileBuffer("testTxt/test2.txt", "\n");
        FileBufferView fbv1 = new FileBufferView(5,5, new Point(1,1), f1);
        LayoutManager manager = new LayoutManager(fbv1, 1, "\n");
        manager.openDirectoryView();
    }

    @Test
    void testOpenWindow() {
        FileBuffer f1 = new FileBuffer(new String[] {"rij1", "rij2","rij3", "rij4", "rij5"}, "test1");
        FileBufferView fbv1 = new FileBufferView(20,20,new Point(1,1),f1 );
        GameView gv1 = new GameView(20,20, new Point(1,1));
        SideBySideLayout sbs1 = new SideBySideLayout(1,1, new Point(1,1), new Layout[] {fbv1, gv1});
        Textr test1 = new Textr("\n", sbs1);
        test1.getLayout().updateSize(20, 40);

        //open in fileBufferView
        assertInstanceOf(TerminalHandler.class, test1.getInputHandler());
        TerminalHandler th1 = (TerminalHandler) test1.getInputHandler();
        assertEquals(test1.getLayoutManager().getFocusedView(), fbv1);
        assertEquals(test1.getInputHandler(),th1);

        test1.openWindow();
        assertEquals(test1.getWindowManager().getWindowCount(), 1);
        SwingWindow w1 = test1.getWindowManager().getSwingWindows()[0];
        assertEquals(test1.getInputHandler(), w1);
        assertEquals(test1.getLayout(), fbv1);
        w1.testClose();

        assertEquals(test1.getWindowManager().getWindowCount(), 0);
        assertEquals(test1.getInputHandler(), th1);

        //don't open when not in fileBufferView
        assertEquals(test1.getLayoutManager().getFocusedView(), fbv1);
        test1.changeFocusNext();
        assertEquals(test1.getLayoutManager().getFocusedView(), gv1);
        test1.openWindow();
        assertEquals(test1.getWindowManager().getWindowCount(), 0);
        assertArrayEquals(test1.getWindowManager().getSwingWindows(), new ArrayList<SwingWindow>().toArray());
    }

    @Test
    void testSwingWindowInput() throws IOException {
        FileBuffer f1 = new FileBuffer(new String[] {}, "test1");
        FileBufferView fbv1 = new FileBufferView(20,20,new Point(1,1),f1 );
        GameView gv1 = new GameView(20,20, new Point(1,1));
        SideBySideLayout sbs1 = new SideBySideLayout(1,1, new Point(1,1), new Layout[] {fbv1, gv1});
        Textr test1 = new Textr("\n", sbs1);
        test1.getLayout().updateSize(20, 40);

        TerminalHandler th1 = (TerminalHandler) test1.getInputHandler();

        test1.openWindow();
        SwingWindow w1 = test1.getWindowManager().getSwingWindows()[0];

        // focus
        w1.testKeyInput(14); //CTRL N
        assertEquals(test1.getLayoutManager().getFocusedView(), fbv1);
        w1.testKeyInput(16); //CTRL P
        assertEquals(test1.getLayoutManager().getFocusedView(), fbv1);

        // duplicate
        w1.testKeyInput(4); //CTRL D
        assertInstanceOf(SideBySideLayout.class, test1.getLayout());
        assertInstanceOf(FileBufferView.class, test1.getLayout().getSubLayouts()[1]);
        Buffer b1 = test1.getLayoutManager().getCurrentBuffer();
        w1.testKeyInput(14); //CTRL N
        assertEquals(test1.getLayoutManager().getCurrentBuffer(), b1);

        // rotate
        w1.testKeyInput(18); //CTRL R
        assertInstanceOf(StackedLayout.class, test1.getLayout());
        w1.testKeyInput(20); // CTRL T
        assertInstanceOf(SideBySideLayout.class, test1.getLayout());
        w1.testKeyInput(-6); // Shift F4
        assertEquals(test1.getLayout(), fbv1);

        // openGameView
        w1.testKeyInput(7); //CTRL G
        assertInstanceOf(SideBySideLayout.class, test1.getLayout());
        assertInstanceOf(GameView.class, test1.getLayout().getSubLayouts()[1]);

        w1.testKeyInput(-6); //Shift F4
        assertEquals(test1.getLayout(), fbv1);

        // parseJSon
        //w1.testKeyInput(10); //CTRL J

        //w1.testKeyInput(-6); //Shift F4
        assertEquals(test1.getLayout(), fbv1);

        // adding characters
        w1.testKeyInput(104); //h
        w1.testKeyInput(101); //e
        w1.testKeyInput(108); //l
        w1.testKeyInput(108); //l
        w1.testKeyInput(111); //o
        w1.testKeyInput(13); //ENTER
        w1.testKeyInput(120); //x

        assertArrayEquals(test1.getLayoutManager().getCurrentBuffer().getContent(),
                new String[] {"hello", "x"});

        // undo / redo
        w1.testKeyInput(26); //CTRL Z
        assertArrayEquals(test1.getLayoutManager().getCurrentBuffer().getContent(),
                new String[] {"hello", ""});
        w1.testKeyInput(21); //CTRL U
        assertArrayEquals(test1.getLayoutManager().getCurrentBuffer().getContent(),
                new String[] {"hello", "x"});

        // openWindow
        w1.testKeyInput(23); //CTRL W
        assertEquals(test1.getWindowManager().getWindowCount(), 2);
        SwingWindow w2 = test1.getWindowManager().getSwingWindows()[1];
        w2.testClose();
        assertEquals(test1.getWindowManager().getWindowCount(), 1);

        w1.testClose();
        assertEquals(test1.getWindowManager().getWindowCount(), 0);

        assertEquals((TerminalHandler) test1.getInputHandler(), th1);

    }

    @Test
    void testJsonLocks() throws IOException {
        FileBufferView fbv1 = new FileBufferView(5,5, new Point(1,1), "/home/wietze/IdeaProjects/softwareOntwerp/testJson/jsonTest1.txt", "\n");
        Textr test1 = new Textr("\n", fbv1);
        test1.getLayoutManager().initViewPositions();
        test1.parseJson();
        test1.closeView(test1.getInputHandler());
        test1.enterPressed();
    }
}
