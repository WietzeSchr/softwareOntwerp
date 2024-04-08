import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class FileBufferTest {

    @Test
    void testConstructor() {
        FileBuffer fb1 = new FileBuffer(new String[] {"testtest", "1", "", "2"}, "test1.txt");
        assertArrayEquals(fb1.getContent(), new String[] {"testtest", "1", "", "2"});
        assertFalse(fb1.getDirty());
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

    }
}