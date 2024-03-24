import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBufferViewTest {
    @Test
    void fileBufferViewTests() throws IOException {
        // Tests voor de constructor
        FileBufferView fbv1 = new FileBufferView(5,10,new Point(20, 10), "test1.txt", "\n");
        fbv1.setPosition(1);
        assertEquals(fbv1.getContent().length, 3);
        assertEquals(fbv1.getContent()[0], "test12");
        assertEquals(fbv1.getContent()[1], "");
        assertEquals(fbv1.getContent()[2], "test123");
        assertEquals(fbv1.getPath(), "test1.txt");
        assertEquals(fbv1.getPosition(), 1);
        assertEquals(fbv1.getHorizontalScrollState(), 1);
        assertEquals(fbv1.getVerticalScrollState(), 1);
        assertEquals(fbv1.getLeftUpperCorner(), new Point(20,10));
        // Tests voor setters
        fbv1.setHeigth(4);
        fbv1.setWidth(5);
        assertEquals(fbv1.getHeigth(), 4);
        assertEquals(fbv1.getWidth(), 5);
        // Tests voor getCursor
        fbv1.setInsertionPoint(new Point(3,4));
        assertEquals(fbv1.getInsertionPoint(), new Point(3, 4));
        assertEquals(fbv1.getCursor(), new Point(22, 13));
        // Tests voor updateScrollStates
        fbv1.addNewChar('c');
        assertEquals(fbv1.getHorizontalScrollState(), 5);
        assertTrue(fbv1.getBuffer().getDirty());
        fbv1.addNewLineBreak();
        assertEquals(fbv1.getVerticalScrollState(), 4);
        assertEquals(fbv1.getHorizontalScrollState(), 1);
        assertEquals(fbv1.getCursor(), new Point(20, 10));
        fbv1.saveBuffer("\n");
        assertFalse(fbv1.getBuffer().getDirty());
    }
}