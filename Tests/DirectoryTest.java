import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DirectoryTest {

    @Test
    void testConstructor() {
        Directory dir = new Directory("/../testTxt/");
        assertEquals(dir.getEntries().length, 5);
        assertNull(dir.getEntries()[0]);
        assertEquals(dir.getEntries()[1].getName(), "test1.txt");
        assertEquals(dir.getEntries()[2].getName(), "test2.txt");
        assertEquals(dir.getEntries()[3].toString(), "testfolder1/");
        assertEquals(dir.getEntries()[4].toString(), "testfolder2/");
    }
}
