import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBufferTest {

    @Test
    void testConstructor() throws IOException {
        FileBuffer fb1 = new FileBuffer(new String[] {"testtest", "1", "", "2"}, "test1.txt");
        File f = new File("test1.txt");
        f.save("\n", new String[] {"1", "abc"});
        FileBuffer fb2 = new FileBuffer("test1.txt", "\n");
        assertArrayEquals(fb1.getContent(), new String[] {"testtest", "1", "", "2"});
        assertFalse(fb1.getDirty());
        assertArrayEquals(fb2.getContent(), new String[] {"1", "abc"});
        assertFalse(fb2.getDirty());
    }

    @Test
    void testGettersAndSetters() {
        FileBuffer fb1 = new FileBuffer(new String[] {"testtest", "1", "", "2"}, "test1.txt");
        fb1.setContent(new String[] {"hallo", ""});
        assertEquals(fb1.getContent().length, 2);
        assertEquals(fb1.getContent()[0], "hallo");
        assertEquals(fb1.getContent()[1], "");
        fb1.setFile(new File("test2.txt"));
        assertEquals(fb1.getFile().getPath(), "test2.txt");
        fb1.setDirty(true);
        assertTrue(fb1.getDirty());
        fb1.setDirty(false);
        assertFalse(fb1.getDirty());
    }

    @Test
    void testDerivedAttributes() {
        FileBuffer fb1 = new FileBuffer(new String[] {"ha", "", "llo", ""}, "test1.txt");
        assertEquals(fb1.getRowCount(), 4);
        assertEquals(fb1.countCharacters(), 5);
        assertEquals(fb1.getColumnCount(), 3);
    }

    @Test
    void testInsertLineBreak() {
        FileBuffer fb1 = new FileBuffer(new String[] {"hallo", ""}, "test1.txt");
        fb1.insertLineBreak(new Point(1,3));
        assertArrayEquals(fb1.getContent(), new String[] {"ha", "llo", ""});
        assertTrue(fb1.getDirty());
        fb1.insertLineBreak(new Point(1,3));
        assertArrayEquals(fb1.getContent(), new String[] {"ha", "", "llo", ""});
    }

    @Test
    void testAddNewChar() {
        FileBuffer fb1 = new FileBuffer(new String[] {"ha", "", "llo", ""}, "test1.txt");
        FileBuffer fb2 = new FileBuffer(new String[] {}, "test2.txt");
        fb2.addNewChar('c', new Point(1,1));
        assertArrayEquals(fb2.getContent(), new String[] {"c"});
        fb2.addNewChar('x', new Point(2,1));
        assertArrayEquals(fb2.getContent(), new String[] {"c", "x"});
        fb1.addNewChar('w', new Point(2,1));
        assertArrayEquals(fb1.getContent(), new String[] {"ha", "w", "llo", ""});
        fb1.addNewChar('o', new Point(3,2));
        assertArrayEquals(fb1.getContent(), new String[] {"ha", "w", "lolo", ""});
    }

    @Test
    void testDeleteChar() {
        FileBuffer fb1 = new FileBuffer(new String[] {"ha", "w", "lolo", ""}, "test1.txt");
        fb1.deleteChar(new Point(3,3));
        assertArrayEquals(fb1.getContent(), new String[] {"ha", "w", "llo", ""});
        fb1.deleteChar(new Point(2,1));
        assertArrayEquals(fb1.getContent(), new String[] {"haw", "llo", ""});
        assertTrue(fb1.getDirty());
        fb1.deleteChar(new Point(3,1));
        assertArrayEquals(fb1.getContent(), new String[] {"haw", "llo"});
    }

    @Test
    void testGetNewInsertionPoint() {
        FileBuffer fb1 = new FileBuffer(new String[] {"ha", "w", "lolo", ""}, "test1.txt");
        assertNull(fb1.getNewInsertionPoint(new Point(0,1)));
        assertNull(fb1.getNewInsertionPoint(new Point(1,0)));
        assertNull(fb1.getNewInsertionPoint(new Point(5,1)));
        assertEquals(fb1.getNewInsertionPoint(new Point(1,2)), new Point(1,2));
        assertEquals(fb1.getNewInsertionPoint(new Point(4, 3)), new Point(4,1));
    }
}