import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import static org.junit.jupiter.api.Assertions.*;
public class BoxTest {
    Box b1 = new Box(3, 4, new Point(1, 3));
    @Test
    void getHeight(){
        assertEquals(b1.getHeight(), 3);
    }
    @Test
    void getWidth(){
        assertEquals(b1.getWidth(), 4);
    }
    @Test
    void getLeftUpperCorner(){
        assertEquals(b1.getLeftUpperPoint(), new Point(1,3));
    }
    @Test
    void getRightLowerCorner(){
        assertEquals(b1.getRightLowerPoint(), new Point(3,6));
    }
}
