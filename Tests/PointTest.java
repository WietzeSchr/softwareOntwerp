import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void getX() {
        Point p1 = new Point(3,5);
        Point p2 = new Point(8, -3);
        Point p3 = new Point(-7,1);
        assertEquals(p1.getX(), 3);
        assertEquals(p2.getX(), 8);
        assertEquals(p3.getX(), -7);
    }

    @Test
    void getY() {
        Point p1 = new Point(3,5);
        Point p2 = new Point(8, -3);
        Point p3 = new Point(-7,1);
        assertEquals(p1.getY(), 5);
        assertEquals(p2.getY(), -3);
        assertEquals(p3.getY(), 1);
    }

    @Test
    void add() {
        Point p1 = new Point(3,5);
        Point p2 = new Point(8, -3);
        Point p3 = new Point(-7,1);
        assertEquals(p1.add(p2), p2.add(p1));
        assertEquals(p1.add(p2), new Point(11, 2));
        assertEquals(p2.add(p3), new Point(1, -2));
    }

    @Test
    void minus() {
        Point p1 = new Point(3,5);
        Point p2 = new Point(8, -3);
        Point p3 = new Point(-7,1);
        assertEquals(p1.minus(p2), new Point(-5,8));
        assertEquals(p2.minus(p3), new Point(15,-4));
    }

    @Test
    void testClone() {
        Point p1 = new Point(3,5);
        assertEquals(p1.clone(), p1);
        assertNotEquals(p1, new File("file"));
    }

    @Test
    void testToString() {
        Point p1 = new Point(3,5);
        Point p2 = new Point(8, -3);
        Point p3 = new Point(-7,1);
        assertEquals(p1.toString(), "(3, 5)");
        assertEquals(p2.toString(), "(8, -3)");
        assertEquals(p3.toString(), "(-7, 1)");
    }
}