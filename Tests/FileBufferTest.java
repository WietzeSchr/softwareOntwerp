import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class FileBufferTest {
    @Test
    void testConstructor() throws FileNotFoundException {
        // Tests voor de constructor
        FileBuffer fb1 = new FileBuffer(new String[] {"testtest", "1", "", "2"});
        assertEquals(fb1.getContent().length, 4);
        assertEquals(fb1.getContent()[0], "testtest");
        assertEquals(fb1.getContent()[1], "1");
        assertEquals(fb1.getContent()[2], "");
        assertEquals(fb1.getContent()[3], "2");
        assertEquals(fb1.getInsertionPoint(), new Point(1, 1));
        assertFalse(fb1.getDirty());
        // Tests voor setters
        fb1.setContent(new String[] {"hallo", ""});
        assertEquals(fb1.getContent().length, 2);
        assertEquals(fb1.getContent()[0], "hallo");
        assertEquals(fb1.getContent()[1], "");
        fb1.setDirty(true);
        assertTrue(fb1.getDirty());
        fb1.setDirty(false);
        assertFalse(fb1.getDirty());
        fb1.setInsertionPoint(new Point(3,3));
        assertEquals(fb1.getInsertionPoint(), new Point(1,1));
        fb1.setInsertionPoint(new Point(1,6));
        assertEquals(fb1.getInsertionPoint(), new Point(1, 6));
        // Tests voor isLineSeparator

        // Tests voor insertLineBreak
        fb1.moveInsertionPoint(new Point(0, -3));
        assertEquals(fb1.getInsertionPoint(), new Point(1, 3));
        fb1.insertLineBreak();
        assertEquals(fb1.getContent().length, 3);
        assertEquals(fb1.getContent()[0], "ha");
        assertEquals(fb1.getContent()[1], "llo");
        assertEquals(fb1.getContent()[2], "");
        assertEquals(fb1.getInsertionPoint(), new Point(2, 1));
        assertTrue(fb1.getDirty());
        fb1.setInsertionPoint(new Point(1,3));
        assertEquals(fb1.getInsertionPoint(), new Point(1,3));
        fb1.insertLineBreak();
        assertEquals(fb1.getContent().length, 4);
        assertEquals(fb1.getContent()[0], "ha");
        assertEquals(fb1.getContent()[1], "");
        assertEquals(fb1.getContent()[2], "llo");
        assertEquals(fb1.getContent()[3], "");
        assertEquals(fb1.getInsertionPoint(), new Point(2,1));
        // Tests voor getColumnCount
        assertEquals(fb1.getColumnCount(), 3);
        // Tests voor countCharacters
        assertEquals(fb1.countCharacters(), 5);
        // Tests voor moveInsertionPoint
        fb1.moveInsertionPoint(new Point(1, 0));
        assertEquals(fb1.getInsertionPoint(), new Point(3,1));
        fb1.moveInsertionPoint(new Point(0,3));
        assertEquals(fb1.getInsertionPoint(), new Point(3,4));
        fb1.moveInsertionPoint(new Point(-1, 0));
        assertEquals(fb1.getInsertionPoint(), new Point(2,1));
        // Tests voor addNewChar
        fb1.setDirty(false);
        assertFalse(fb1.getDirty());
        fb1.addNewChar('w');
        assertEquals(fb1.getContent().length, 4);
        assertEquals(fb1.getContent()[0], "ha");
        assertEquals(fb1.getContent()[1], "w");
        assertEquals(fb1.getContent()[2], "llo");
        assertEquals(fb1.getContent()[3], "");
        assertEquals(fb1.getInsertionPoint(), new Point(2,2));
        fb1.moveInsertionPoint(new Point(1,0));
        fb1.addNewChar('o');
        assertEquals(fb1.getContent().length, 4);
        assertEquals(fb1.getContent()[0], "ha");
        assertEquals(fb1.getContent()[1], "w");
        assertEquals(fb1.getContent()[2], "lolo");
        assertEquals(fb1.getContent()[3], "");
        assertEquals(fb1.countCharacters(), 7);
        assertEquals(fb1.getInsertionPoint(), new Point(3,3));
        // Tests voor deleteChar
        fb1.deleteChar();
        assertEquals(fb1.getContent().length, 4);
        assertEquals(fb1.getContent()[0], "ha");
        assertEquals(fb1.getContent()[1], "w");
        assertEquals(fb1.getContent()[2], "llo");
        assertEquals(fb1.getContent()[3], "");
        assertEquals(fb1.getInsertionPoint(), new Point(3,2));
        fb1.setDirty(false);
        assertFalse(fb1.getDirty());
        fb1.setInsertionPoint(new Point(2,1));
        assertEquals(fb1.getInsertionPoint(), new Point(2,1));
        fb1.deleteChar();
        assertEquals(fb1.getContent().length, 3);
        assertEquals(fb1.getContent()[0], "haw");
        assertEquals(fb1.getContent()[1], "llo");
        assertEquals(fb1.getContent()[2], "");
        assertTrue(fb1.getDirty());
        fb1.setInsertionPoint(new Point(3,1));
        assertEquals(fb1.getInsertionPoint(), new Point(3,1));
        fb1.deleteChar();
        assertEquals(fb1.getContent().length, 2);
        assertEquals(fb1.getContent()[0], "haw");
        assertEquals(fb1.getContent()[1], "llo");
    }
}