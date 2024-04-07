import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SnakeTest {

    @Test
    void gettersAndSetters() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(points, new Point(1,0));
        assertEquals(snake.getHead(), new Point(1,1));
        assertEquals(snake.getBody().length, 4);
        assertArrayEquals(snake.getBody(), new Point[] {new Point(1,2), new Point(1,3),new Point(1,4),new Point(1,5)});
        assertEquals(snake.getDir(), new Point(1,0));
        snake.setHead(new Point(2,5));
        assertEquals(snake.getHead(), new Point(2,5));
        snake.setHead(new Point(1,1));
        snake.setDir(new Point(-1,0));
        assertEquals(snake.getDir(), new Point(-1,0));
        snake.setDir(new Point(0,1));
        assertEquals(snake.getDir(), new Point(-1,0));
        snake.setDir(new Point(0,-1));
        assertEquals(snake.getDir(), new Point(0,-1));
    }

    @Test
    void moveHead() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(points, new Point(1,0));
        assertEquals(snake.moveHead(), new Point(2,1));
    }

    @Test
    void moveBody() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(points, new Point(1,0));
        assertEquals(snake.getBody().length, 4);
        assertArrayEquals(snake.moveBody(), new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4)});
    }

    @Test
    void extendBody() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(points, new Point(1,0));
        assertEquals(snake.extendBody().length, 5);
        assertArrayEquals(snake.extendBody(), points);
    }

    @Test
    void abstractList() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(points, new Point(1,0));
        snake.setDir(new Point(1, 0));
        assertArrayEquals(snake.abstractList(), points);
    }

    @Test
    void bodyContains() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(points, new Point(1,0));
        assertTrue(snake.bodyContains(new Point(1,4)));
        assertFalse(snake.bodyContains(new Point(1,1)));
        assertFalse(snake.bodyContains(new Point(2,1)));
    }

    @Test
    void bodyContainsHead() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(points, new Point(1,0));
        assertFalse(snake.bodyContainsHead());
        snake.setHead(new Point(1,2));
        assertTrue(snake.bodyContainsHead());
    }

    @Test
    void contains() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(points, new Point(1,0));
        assertTrue(snake.contains(new Point(1,4)));
        assertTrue(snake.contains(new Point(1,1)));
        assertFalse(snake.contains(new Point(2,1)));
    }

    @Test
    void charAt() {
    }

    @Test
    void equals() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(points, new Point(1,0));
        Point[] points2 = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake2 = new Snake(points2, new Point(1, 0));
        assertEquals(snake, snake2);
        snake.setHead(new Point(2,2));
        assertNotEquals(snake, snake2);
        snake2.setHead(new Point(1,1));
        snake2.setBody(new Point[] {new Point(1,2),
                new Point(1,3), new Point(1,4)});
        assertNotEquals(snake, snake2);
        snake.setBody(new Point[] {new Point(1,2),
                new Point(1,3), new Point(2,3)});
        assertNotEquals(snake, snake2);
    }
}