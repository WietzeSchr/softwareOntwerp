import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DirectoryViewTest {

    DirectoryView makeTest1() throws IOException {
        DirectoryView test1 = new DirectoryView(10,20, new Point(1,1), "/../testTxt/", new LayoutManager(null, 1, "\n"));
        test1.getManager().setLayout(test1);
        return test1;
    }

    DirectoryView makeTest2() throws IOException {
        FileBuffer bufferContainingJson = new FileBuffer(new String[] {"{", "  \"foo\": \"bar\"", "}"}, "testJson");
        JsonObject json = SimpleJsonParser.parseJsonObject("{\n  \"foo\": \"bar\"\n}", bufferContainingJson);
        DirectoryView test2 = new DirectoryView(10,20, new Point(1,1), json, new LayoutManager(null, 1, "\n"));
        test2.getManager().setLayout(test2);
        return test2;
    }

    @Test
    void testConstructor() throws IOException {
        DirectoryView test1 = makeTest1();
        DirectoryView test2 = makeTest2();
        assertEquals(test1.getLine(), 1);
        assertEquals(test2.getLine(), 1);
        assertEquals(test1.getHeigth(), 10);
        assertEquals(test2.getHeigth(), 10);
        assertEquals(test1.getWidth(), 20);
        assertEquals(test2.getWidth(), 20);
        assertEquals(test1.getLeftUpperCorner(), new Point(1,1));
        assertEquals(test2.getLeftUpperCorner(), new Point(1,1));
        assertEquals(test1.getFileSystemNode().getClass(), Directory.class);
        assertEquals(test2.getFileSystemNode().getClass(), JsonObject.class);
        assertEquals(test1.getFileSystemNode().toString(), "testTxt/");
        assertEquals(test2.getFileSystemNode().toString(), "root/");
        test1.setLine(0);
        assertEquals(test1.getLine(), 1);
        test1.setLine(6);
        assertEquals(test1.getLine(), 1);
        test1.setLine(4);
        assertEquals(test1.getLine(), 4);
    }

    @Test
    void testMove() throws IOException {
        DirectoryView test1 = makeTest1();
        DirectoryView test2 = makeTest2();
        test1.move(Direction.NORD);
        assertEquals(test1.getLine(), 1);
        test1.move(Direction.SOUTH);
        assertEquals(test1.getLine(), 2);
        test1.move(Direction.EAST);
        assertEquals(test1.getLine(), 2);
        test1.move(Direction.WEST);
        assertEquals(test1.getLine(), 2);
        test2.move(Direction.SOUTH);
        assertEquals(test2.getLine(), 1);
        test1.setLine(5);
        assertEquals(test1.getLine(), 5);
        test1.move(Direction.SOUTH);
        assertEquals(test1.getLine(), 5);
        test1.move(Direction.NORD);
        assertEquals(test1.getLine(), 4);
    }

    @Test
    void testEnterPressed() throws IOException {
        DirectoryView test1 = makeTest1();
        DirectoryView test2 = makeTest2();
        LayoutManager manager1 = test1.getManager();
        LayoutManager manager2 = test2.getManager();
        test1.enterPressed("\n");
        assertEquals(manager1.getLayout().getClass(), DirectoryView.class);
        DirectoryView newView1 = (DirectoryView) manager1.getLayout();
        assertEquals(newView1.getFileSystemNode().getName(), "softwareOntwerp");
        test2.enterPressed("\n");
        assertEquals(manager2.getLayout().getClass(), FileBufferView.class);
        FileBufferView newView2 = (FileBufferView) manager2.getLayout();
        assertEquals(newView2.getPathString(), "/root/foo");
    }

    @Test
    void testOpenFile() throws IOException {
        DirectoryView test1 = makeTest1();
        DirectoryView test2 = makeTest2();
    }

    @Test
    void testMakeShow() throws IOException {
        DirectoryView test1 = makeTest1();
        DirectoryView test2 = makeTest2();
        assertArrayEquals(test1.makeShow(), new String[] {"..", "test1.txt", "test2.txt", "testfolder1/", "testfolder2/"});
        assertEquals(test1.makeHorizontalScrollBar(), "home/IdeaProjects/softwareOntwerp/testTxt/ ");
        assertArrayEquals(test1.makeVerticalScrollBar(), new char[] {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#'});
    }

    @Test
    void testGetCursor() throws IOException {
        DirectoryView test1 = makeTest1();
        test1.setLeftUpperCorner(new Point(5,5));
        assertEquals(test1.getCursor(), new Point(5,5));
        test1.move(Direction.SOUTH);
        assertEquals(test1.getCursor(), new Point(6,5));
        test1.move(Direction.EAST);
        assertEquals(test1.getCursor(), new Point(6,5));
    }
}
