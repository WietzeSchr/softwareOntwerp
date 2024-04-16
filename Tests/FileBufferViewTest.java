import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBufferViewTest {
    @Test
    void testConstructor() throws FileNotFoundException {
        FileBuffer buffer = new FileBuffer(new String[] {"test12", "", "test123"}, "test1.txt");
        FileBufferView fbv1 = new FileBufferView(5,10,new Point(20, 10), buffer);
        FileBufferView fbv2 = new FileBufferView(10, 10, new Point(10, 20), "test1.txt", "\n");
        fbv1.setPosition(1);
        assertEquals(fbv1.getInsertionPoint(), new Point(1,1));
        assertEquals(fbv1.getHorizontalScrollState(), 1);
        assertEquals(fbv1.getVerticalScrollState(), 1);
        assertEquals(fbv1.getBuffer(), buffer);
        assertTrue(fbv1.lastEditIsEmptyEdit());
        fbv2.setPosition(1);
        assertEquals(fbv1.getInsertionPoint(), new Point(1,1));
        assertEquals(fbv1.getHorizontalScrollState(), 1);
        assertEquals(fbv1.getVerticalScrollState(), 1);
        assertEquals(fbv1.getTick(), 0);
        assertEquals(fbv1.getNextDeadline(), System.currentTimeMillis());
        assertTrue(fbv1.lastEditIsEmptyEdit());
        assertThrows(FileNotFoundException.class,
                () -> new FileBufferView(10, 20, new Point(10, 20), "test2.txt", "\n"));
    }

    @Test
    void testSetters() {
        FileBuffer buffer = new FileBuffer(new String[] {"test12", "", "test123"}, "test1.txt");
        FileBufferView fbv1 = new FileBufferView(5,10,new Point(20, 10), buffer);
        fbv1.setPosition(2);
        fbv1.setInsertionPoint(new Point(2,1));
        assertEquals(fbv1.getInsertionPoint(), new Point(2,1));
        fbv1.setInsertionPoint(new Point(0, 2));
        assertEquals(fbv1.getInsertionPoint(), new Point(2,1));
        fbv1.setInsertionPoint(new Point(1,8));
        assertEquals(fbv1.getInsertionPoint(), new Point(1,7));
        FileBuffer buffer2 = new FileBuffer(new String[] {"ttt", "tt", ""}, "test2");
        fbv1.setBuffer(buffer2);
        assertEquals(fbv1.getBuffer(), buffer2);
        assertEquals(fbv1.getPosition(), 2);
        fbv1.setHorizontalScrollState(3);
        fbv1.setVerticalScrollState(4);
        assertEquals(fbv1.getHorizontalScrollState(), 3);
        assertEquals(fbv1.getVerticalScrollState(), 4);
    }

    @Test
    void testDerivedAttributes() {
        FileBuffer buffer = new FileBuffer(new String[] {"test12", "", "test123"}, "Home/Documents/test1.txt");
        FileBufferView fbv1 = new FileBufferView(5,10,new Point(20, 10), buffer);
        FileBuffer buffer2 = new FileBuffer(new String[] {"test12", "", "test123"}, "Home\\Documents\\test2.txt");
        FileBufferView fbv2 = new FileBufferView(5,10,new Point(20, 10), buffer2);
        fbv2.setVerticalScrollState(2);
        fbv2.setHorizontalScrollState(4);
        fbv1.setPosition(1);
        assertArrayEquals(fbv1.getContent(), new String[] {"test12", "", "test123"});
        assertEquals(fbv1.getRowCount(), 3);
        assertEquals(fbv1.getColumnCount(), 7);
        assertEquals(fbv1.getCharacterCount(), 13);
        assertEquals(fbv1.getPath(), "Home/Documents/test1.txt");
        assertEquals(fbv1.getFileName(), "test1.txt");
        assertEquals(fbv2.getFileName(), "test2.txt");
        fbv1.setInsertionPoint(new Point(3,4));
        assertEquals(fbv1.getInsertionPoint(), new Point(3, 4));
        assertEquals(fbv1.getCursor(), new Point(22, 13));
    }

    @Test
    void testMoveInsertionPoint() {
        FileBuffer buffer = new FileBuffer(new String[] {"test12", "", "test123"}, "test1.txt");
        FileBufferView fbv1 = new FileBufferView(5,10,new Point(20, 10), buffer);
        fbv1.setPosition(1);
        fbv1.move(Direction.NORD);
        assertEquals(fbv1.getInsertionPoint(), new Point(1,1));
        fbv1.move(Direction.WEST);
        assertEquals(fbv1.getInsertionPoint(), new Point(1,1));
        fbv1.move(Direction.EAST);
        assertEquals(fbv1.getInsertionPoint(), new Point(1,2));
        fbv1.move(Direction.EAST);
        assertEquals(fbv1.getInsertionPoint(), new Point(1,3));
        fbv1.move(Direction.SOUTH);
        assertEquals(fbv1.getInsertionPoint(), new Point(2,1));
    }

    @Test
    void testAddNewLineBreak() {
        FileBuffer buffer = new FileBuffer(new String[] {"test12", "", "test123"}, "test1.txt");
        FileBufferView fbv1 = new FileBufferView(5,10,new Point(20, 10), buffer);
        fbv1.setPosition(1);
        fbv1.addNewLineBreak();
        assertArrayEquals(fbv1.getContent(), new String[] {"", "test12", "", "test123"});
        assertTrue(fbv1.lastEditEquals((char) 13, false, new Point(1,1), new Point(2,1)));
        assertTrue(fbv1.getBuffer().getDirty());
    }

    @Test
    void testAddNewChar() {
        FileBuffer buffer = new FileBuffer(new String[] {"test12", "", "test123"}, "test1.txt");
        FileBufferView fbv1 = new FileBufferView(5,10,new Point(20, 10), buffer);
        fbv1.setPosition(1);
        fbv1.move(Direction.SOUTH);
        fbv1.addNewChar('c');
        assertArrayEquals(fbv1.getContent(), new String[] {"test12", "c", "test123"});
        assertTrue(fbv1.lastEditEquals('c', false, new Point(2,1), new Point(2,2)));
        assertTrue(fbv1.getBuffer().getDirty());
    }

    @Test
    void testDeleteChar() {
        FileBuffer buffer = new FileBuffer(new String[] {"test12", "", "test123"}, "test1.txt");
        FileBufferView fbv1 = new FileBufferView(5,10,new Point(20, 10), buffer);
        fbv1.setPosition(1);
        fbv1.deleteChar();
        assertTrue(fbv1.lastEditIsEmptyEdit());
        fbv1.move(Direction.EAST);
        fbv1.deleteChar();
        assertTrue(fbv1.getBuffer().getDirty());
        assertArrayEquals(fbv1.getContent(), new String[] {"est12", "", "test123"});
        assertTrue(fbv1.lastEditEquals('t', true, new Point(1,2), new Point(1,1)));
        fbv1.move(Direction.SOUTH);
        fbv1.deleteChar();
        assertArrayEquals(fbv1.getContent(), new String[] {"est12", "test123"});
        assertTrue(fbv1.lastEditEquals((char) 13, true, new Point(2,1), new Point(1,6)));
    }

    @Test
    void testCloseBuffer() throws IOException {
        FileBuffer buffer = new FileBuffer(new String[] {"test12", "", "test123"}, "test1.txt");
        FileBufferView fbv1 = new FileBufferView(5,10,new Point(20, 10), buffer);
        fbv1.setPosition(1);
        assertNull(fbv1.closeView(1));
        assertEquals(fbv1.closeView(2, null), fbv1);
    }

    @Test
    void testSaveBuffer() throws IOException {
        FileBuffer buffer = new FileBuffer(new String[] {"test12", "", "test123"}, "test1.txt");
        FileBufferView fbv1 = new FileBufferView(5,10,new Point(20, 10), buffer);
        fbv1.setPosition(1);
        fbv1.move(Direction.EAST);
        fbv1.deleteChar();
        assertTrue(fbv1.getBuffer().getDirty());
        fbv1.saveBuffer("\n");
        assertFalse(fbv1.getBuffer().getDirty());
    }

    @Test
    void testUndoRedo() {
        FileBuffer buffer = new FileBuffer(new String[] {"test12", "", "test123"}, "test1.txt");
        FileBufferView fbv1 = new FileBufferView(5,10,new Point(20, 10), buffer);
        fbv1.setPosition(1);
        fbv1.addNewChar('x');
        fbv1.move(Direction.SOUTH);
        fbv1.deleteChar();
        fbv1.deleteChar();
        fbv1.move(Direction.WEST);
        fbv1.addNewLineBreak();
        fbv1.move(Direction.SOUTH);
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest", "1", "test123"});
        fbv1.redo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest", "1", "test123"});
        fbv1.undo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest1", "test123"});
        assertEquals(fbv1.getInsertionPoint(), new Point(1,6));
        fbv1.undo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest12", "test123"});
        assertEquals(fbv1.getInsertionPoint(), new Point(1,8));
        fbv1.redo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest1", "test123"});
        assertEquals(fbv1.getInsertionPoint(), new Point(1,7));
        fbv1.undo();
        fbv1.undo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest12", "", "test123"});
        assertEquals(fbv1.getInsertionPoint(), new Point(2,1));
        fbv1.undo();
        assertArrayEquals(fbv1.getContent(), new String[] {"test12", "", "test123"});
        assertEquals(fbv1.getInsertionPoint(), new Point(1, 1));
        assertFalse(fbv1.getBuffer().getDirty());
        fbv1.undo();
        assertArrayEquals(fbv1.getContent(), new String[] {"test12", "", "test123"});
        assertEquals(fbv1.getInsertionPoint(), new Point(1, 1));
        fbv1.redo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest12", "", "test123"});
        assertEquals(fbv1.getInsertionPoint(), new Point(1,2));
        assertTrue(fbv1.getBuffer().getDirty());
        fbv1.redo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest12", "test123"});
        assertEquals(fbv1.getInsertionPoint(), new Point(1,8));
        fbv1.redo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest1", "test123"});
        assertEquals(fbv1.getInsertionPoint(), new Point(1,7));
        fbv1.redo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest", "1", "test123"});
        assertEquals(fbv1.getInsertionPoint(), new Point(2,1));
    }

    @Test
    void testUpdateScrollState() {
        FileBuffer buffer = new FileBuffer(new String[] {"test12", "", "test123", "1", "2"}, "test1.txt");
        FileBufferView fbv1 = new FileBufferView(5,5,new Point(20, 10), buffer);
        fbv1.setPosition(1);
        fbv1.setInsertionPoint(new Point(1,5));
        assertEquals(fbv1.getInsertionPoint(), new Point(1, 5));
        fbv1.updateScrollStates();
        assertEquals(fbv1.getHorizontalScrollState(), 5);
        fbv1.setInsertionPoint(new Point(1,1));
        assertEquals(fbv1.getInsertionPoint(), new Point(1, 1));
        fbv1.updateScrollStates();
        assertEquals(fbv1.getHorizontalScrollState(), 1);
        fbv1.setInsertionPoint(new Point(5, 1));
        assertEquals(fbv1.getInsertionPoint(), new Point(5,1));
        fbv1.updateScrollStates();
        assertEquals(fbv1.getVerticalScrollState(), 5);
        fbv1.setInsertionPoint(new Point(1,1));
        assertEquals(fbv1.getInsertionPoint(), new Point(1,1));
        fbv1.updateScrollStates();
        assertEquals(fbv1.getVerticalScrollState(), 1);
    }

    @Test
    void testMakeShow() {
        FileBuffer buffer = new FileBuffer(new String[] {"test12", "", "test123", "1", "2"}, "test1.txt");
        FileBuffer buffer2 = new FileBuffer(new String[] {"test12", "", "test123", "1", "2"}, "test2.txt");
        FileBufferView fbv1 = new FileBufferView(5,5,new Point(20, 10), buffer);
        FileBufferView fbv2 = new FileBufferView(6,10,new Point(20, 10), buffer2);
        fbv1.setPosition(1);
        fbv2.getBuffer().setDirty(true);
        assertArrayEquals(fbv1.makeShow(), new String[] {"test", null, "test", "1"});
        assertArrayEquals(fbv2.makeShow(), new String[] {"test12", null, "test123", "1", "2"});
        assertEquals(fbv1.makeHorizontalScrollBar(), "test1.txt, r: 5, char: 15, insert: (1, 1) ");
        assertEquals(fbv2.makeHorizontalScrollBar(), "* test2.txt, r: 5, char: 15, insert: (1, 1) ");
        assertArrayEquals(fbv1.makeVerticalScrollBar(), new char[] {'#', '#', '#', '#'});
        assertArrayEquals(fbv2.makeVerticalScrollBar(), new char[] {'#', '#', '#', '#', '#'});
    }
}