import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BufferTest {
    @Test
    void testUndoRedo() {
        FileBuffer buffer = new FileBuffer(new String[] {"test12", "", "test123"}, "test1.txt");
        FileBufferView fbv1 = new FileBufferView(5,10,new Point(20, 10), buffer);
        fbv1.setPosition(1);
        fbv1.addNewChar('x');
        fbv1.move(Direction.SOUTH);
        fbv1.deleteChar();
        fbv1.move(Direction.EAST);
        fbv1.move(Direction.EAST);
        fbv1.move(Direction.EAST);
        fbv1.move(Direction.EAST);
        fbv1.move(Direction.EAST);
        fbv1.move(Direction.EAST);
        fbv1.move(Direction.EAST);
        fbv1.deleteChar();
        fbv1.move(Direction.WEST);
        assertEquals(fbv1.getInsertionPoint(), new Point(1,6));
        fbv1.enterPressed("\n");
        fbv1.move(Direction.SOUTH);
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest", "1", "test123"});
        fbv1.redo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest", "1", "test123"});
        fbv1.undo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest1", "test123"});
        assertEquals(fbv1.getInsertionPoint(), new Point(2,1));
        fbv1.undo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest12", "test123"});
        fbv1.redo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest1", "test123"});
        fbv1.undo();
        fbv1.undo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest12", "", "test123"});
        fbv1.undo();
        assertArrayEquals(fbv1.getContent(), new String[] {"test12", "", "test123"});
        assertFalse(fbv1.getBuffer().getDirty());
        fbv1.undo();
        assertArrayEquals(fbv1.getContent(), new String[] {"test12", "", "test123"});
        fbv1.redo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest12", "", "test123"});
        assertTrue(fbv1.getBuffer().getDirty());
        fbv1.redo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest12", "test123"});
        fbv1.redo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest1", "test123"});
        fbv1.redo();
        assertArrayEquals(fbv1.getContent(), new String[] {"xtest", "1", "test123"});
    }
}
