import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void testConstructor() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Game game = new Game(9, 19);
        assertEquals(game.getDelay(), 1000);
        assertEquals(game.getSnake(), new Snake(new ArrayList<>(List.of(new Point[] {new Point(4,9), new Point(4,10), new Point(4,11),
                new Point(4,12),new Point(4,13),new Point(4,14)})), Direction.WEST));
        assertEquals(game.getScore(), 0);
    }

    @Test
    void testSetters() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.SOUTH);
        Game game = new Game(9, 19);
        game.setSnake(snake);
        assertEquals(game.getSnake(), snake);
        game.setScore(50);
        assertEquals(game.getScore(), 50);
        game.setDelay(900);
        assertEquals(game.getDelay(), 900);
        assertEquals(game.getGrid().length, 9);
        assertEquals(game.getGrid()[0].length, 19);
        for (int i = 1; i < game.getGrid().length + 1; i++) {
            for (int j = 1; j < game.getGrid()[0].length + 1; j++) {
                assertEquals(game.getGridAt(new Point(i, j)), 0);
            }
        }
    }

    @Test
    void tick() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.SOUTH);
        Game game = new Game(3, 7);
        game.setSnake(snake);
        game.tick();
        assertArrayEquals(snake.abstractList(), new Point[] {new Point(2,1), new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4)});
        assertEquals(game.getSnake().getDir(), Direction.SOUTH.point);
    }

    @Test
    void changeDir() {
        Game game = new Game(9, 19);
        game.changeDir(Direction.WEST);
        assertEquals(game.getSnake().getDir(), new Point(0, -1));
        game.changeDir(Direction.NORD);
        assertEquals(game.getSnake().getDir(), new Point(-1, 0));
        game.changeDir(Direction.EAST);
        assertEquals(game.getSnake().getDir(), new Point(-1, 0));
    }

    @Test
    void moveSnake() {
        Game game = new Game(9, 19);
        game.changeDir(Direction.WEST);
        game.moveSnake();
        Snake snake1 = new Snake(new ArrayList<>(List.of(new Point[] {new Point(4,8), new Point(4,9), new Point(4,10),
                new Point(4,11),new Point(4,12),new Point(4,13)})), Direction.WEST);
        assertEquals(game.getSnake(), snake1);
        game.setGridAt(1, new Point(4, 7));
        game.moveSnake();
        Snake snake2 = new Snake(new ArrayList<>(List.of(new Point[] {new Point(4,7), new Point(4,8), new Point(4,9), new Point(4,10),
                new Point(4,11),new Point(4,12),new Point(4,13)})), Direction.WEST);
        assertEquals(game.getSnake(), snake2);
        game.changeDir(Direction.NORD);
        game.moveSnake();
        game.moveSnake();
        game.moveSnake();
        game.moveSnake();
        assertNull(game.getSnake());
    }

    @Test
    void spawnApple() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.SOUTH);
        Game game = new Game(2, 6);
        game.setSnake(snake);
        assertArrayEquals(game.getFreeSpace(), new Point[] {new Point(1, 6), new Point(2,1), new Point(2,2),
                new Point(2,3), new Point(2,4), new Point(2,5), new Point(2,6)});
        assertEquals(game.countApples(), 0);
        game.spawnApple();
        assertEquals(game.countApples(), 1);
    }

    @Test
    void isValid() {
        Game game = new Game(9,19);
        assertFalse(game.isValid(new Point(0,5)));
        assertFalse(game.isValid(new Point(11,5)));
        assertFalse(game.isValid(new Point(3,0)));
        assertFalse(game.isValid(new Point(4, 21)));
        assertTrue(game.isValid(new Point(5,3)));
    }

    @Test
    void eatApple() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.SOUTH);
        Game game = new Game(3, 7);
        game.setSnake(snake);
        game.setGridAt(1, new Point(1, 1));
        assertTrue(game.eatApple());
        assertEquals(game.countApples(), 1);
        assertEquals(game.getDelay(), 990);
        assertEquals(game.getScore(), 10);
    }

    @Test
    void loseGame() {
        Game game = new Game(10, 20);
        assertNotNull(game.getSnake());
        game.loseGame();
        assertNull(game.getSnake());
    }

    @Test
    void getAbstractGrid() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.SOUTH);
        Game game = new Game(2, 6);
        game.setSnake(snake);
        game.setGridAt(1, new Point(2,1));
        assertArrayEquals(game.getAbstractGrid(), new char[][] {{'v', 'o', 'o', 'o', '-', ' '}, {'*', ' ', ' ', ' ', ' ', ' '}});
    }

    @Test
    void findBestFit(){
        Point[] points = new Point[] {new Point(1,4), new Point(1,5),
                new Point(1,6)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.WEST);
        Game game = new Game(2, 7);
        game.setSnake(snake);
        assertArrayEquals(game.getAbstractGrid(), new char[][] {{' ',' ',' ','<','o','-',' '},
                                                                {' ',' ',' ',' ',' ',' ',' '}});
        game.updateSize(2, 4);
        assertArrayEquals(game.getAbstractGrid(), new char[][] {{' ',' ',' ',' '},
                                                                {' ',' ','<','o'}});
    }
}