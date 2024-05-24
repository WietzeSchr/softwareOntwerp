import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class FileSystemNodeTest {

    @Test
    void testGetEntry() {
        Directory dir = new Directory("/../testTxt/");
        assertNull(dir.getEntry(1));
        assertEquals(dir.getEntry(2).toString(), "test1.txt");
        assertEquals(dir.getEntry(3).toString(), "test2.txt");
        assertEquals(dir.getEntry(4).toString(), "testfolder1/");
        assertEquals(dir.getEntry(5).toString(), "testfolder2/");
    }

    @Test
    void testOpenEntry() throws FileNotFoundException {
        Directory dir = new Directory("/../testTxt/");
        DirectoryView view = (DirectoryView) dir.openEntry(null, 1, null, "\n");
        assertEquals(view.getFileSystemNode().getName(), "softwareOntwerp");
        DirectoryView view1 = (DirectoryView) dir.openEntry(null, 4, null, "\n");
        assertEquals(view1.getFileSystemNode().getName(), "testfolder1");
        FileBufferView fbv1 = (FileBufferView) dir.openEntry(null, 2, null, "\n");
        assertEquals(fbv1.getFileName(), "test1.txt");
    }

    @Test
    void testMakeContent() {
        Directory dir = new Directory("/../testTxt/");
        assertArrayEquals(dir.makeContent(), new String[] {"..", "test1.txt", "test2.txt", "testfolder1/", "testfolder2/"});
    }

    @Test
    void testGetRoot() {
        Directory dir = new Directory("/../testTxt/");
        FileSystemNode root = dir.getRoot();
        assertEquals(root.getPathString(), "testTxt");
    }
}
