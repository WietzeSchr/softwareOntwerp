import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;
import java.io.IOException;

import static java.awt.event.KeyEvent.VK_1;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SwingWindowTest {
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
    void testConstructor() throws IOException {
        Textr test1 = makeTestTextr();
        SwingWindow sw1 = new SwingWindow(50, 50, test1);

        assertEquals(sw1.getArea(), new Point(50, 50));
    }
}
