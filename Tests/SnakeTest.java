import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SnakeTest {

    @Test
    void gettersAndSetters() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.SOUTH);
        Point[] points2 = new Point[] {new Point(1,2), new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake2 = new Snake(new Point(1,1), new ArrayList<>(List.of(points2)), Direction.SOUTH);
        assertEquals(snake, snake2);
        assertEquals(snake.getHead(), new Point(1,1));
        assertEquals(snake.getBody().size(), 4);
        assertArrayEquals(snake.getBody().toArray(), new Point[] {new Point(1,2), new Point(1,3),new Point(1,4),new Point(1,5)});
        assertEquals(snake.getDir(), new Point(1,0));
        snake.setHead(new Point(2,5));
        assertEquals(snake.getHead(), new Point(2,5));
        snake.setHead(new Point(1,1));
        snake.setDir(Direction.NORD);
        assertEquals(snake.getDir(), new Point(-1,0));
        snake.setDir(Direction.EAST);
        assertEquals(snake.getDir(), new Point(-1,0));
        snake.setDir(Direction.WEST);
        assertEquals(snake.getDir(), new Point(0,-1));
    }

    @Test
    void testMove() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.SOUTH);
        snake.move();
        assertEquals(snake.getHead(), new Point(2, 1));
        assertArrayEquals(snake.getBody().toArray(), new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)});
    }

    @Test
    void testRemoveTail() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.SOUTH);
        snake.removeTail();
        assertEquals(snake.getHead(), new Point(1,1));
        assertArrayEquals(snake.getBody().toArray(), new Point[] {new Point(1,2),
                new Point(1,3), new Point(1,4)});
    }

    @Test
    void abstractList() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.SOUTH);
        assertArrayEquals(snake.abstractList(), points);
    }

    @Test
    void bodyContains() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.SOUTH);
        assertTrue(snake.bodyContains(new Point(1,4)));
        assertFalse(snake.bodyContains(new Point(1,1)));
        assertFalse(snake.bodyContains(new Point(2,1)));
    }

    @Test
    void bodyContainsHead() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.SOUTH);
        assertFalse(snake.bodyContainsHead());
        snake.setHead(new Point(1,2));
        assertTrue(snake.bodyContainsHead());
    }

    @Test
    void contains() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.SOUTH);
        assertTrue(snake.contains(new Point(1,4)));
        assertTrue(snake.contains(new Point(1,1)));
        assertFalse(snake.contains(new Point(2,1)));
    }

    @Test
    void charAt() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.SOUTH);
        assertEquals(snake.charAt(new Point(1,4)), 'o');
        assertEquals(snake.charAt(new Point(1,5)), '-');
        assertEquals(snake.charAt(new Point(1,1)), 'v');
        snake.setDir(Direction.WEST);
        assertEquals(snake.charAt(new Point(1, 1)), '<');
        snake.setDir(Direction.NORD);
        assertEquals(snake.charAt(new Point(1,1)), (char) 94);
        Point[] points2 = new Point[] {new Point(1,1), new Point(2,1),
                new Point(3, 1)};
        Snake snake2 = new Snake(new ArrayList<>(List.of(points2)), Direction.EAST);
        assertEquals(snake2.charAt(new Point(1,1)), '>');
        assertEquals(snake.charAt(new Point(2,1)), ' ');
        assertEquals(snake2.charAt(new Point(3,1)), '|');
        assertEquals(snake2.toString(), "(1, 1) (2, 1) (3, 1) ");
    }

    @Test
    void equals() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.SOUTH);
        Point[] points2 = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake2 = new Snake(new ArrayList<>(List.of(points2)), Direction.SOUTH);
        assertEquals(snake, snake2);
        snake.setHead(new Point(2,2));
        assertNotEquals(snake, snake2);
        snake2.setHead(new Point(1,1));
        snake2.setBody(new ArrayList<>(List.of(new Point[] {new Point(1,2),
                new Point(1,3), new Point(1,4)})));
        assertNotEquals(snake, snake2);
        snake.setBody(new ArrayList<>(List.of(new Point[] {new Point(1,2),
                new Point(1,3), new Point(2,3)})));
        assertNotEquals(snake, snake2);
    }
}