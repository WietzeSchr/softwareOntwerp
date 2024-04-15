import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void testConstructor() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Game game = new Game(10, 20);
        assertEquals(game.getTick(), 1000);
        assertEquals(game.getSnake(), new Snake(new ArrayList<>(List.of(new Point[] {new Point(4,9), new Point(4,10), new Point(4,11),
                new Point(4,12),new Point(4,13),new Point(4,14)})), Direction.WEST));
        assertEquals(game.getScore(), 0);
    }

    @Test
    void testSetters() {
        Point[] points = new Point[] {new Point(1,1), new Point(1,2),
                new Point(1,3), new Point(1,4), new Point(1,5)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.SOUTH);
        Game game = new Game(10, 20);
        game.setSnake(snake);
        assertEquals(game.getSnake(), snake);
        game.setScore(50);
        assertEquals(game.getScore(), 50);
        game.setTick(900);
        assertEquals(game.getTick(), 900);
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
    }

    @Test
    void changeDir() {
        Game game = new Game(10, 20);
        game.changeDir(Direction.WEST);
        assertEquals(game.getSnake().getDir(), new Point(0, -1));
        game.changeDir(Direction.NORD);
        assertEquals(game.getSnake().getDir(), new Point(-1, 0));
        game.changeDir(Direction.EAST);
        assertEquals(game.getSnake().getDir(), new Point(-1, 0));
    }

    @Test
    void moveSnake() {
        Game game = new Game(10, 20);
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

    }

    @Test
    void isValid() {
        Game game = new Game(10,20);
        assertFalse(game.isValid(new Point(0,5)));
        assertFalse(game.isValid(new Point(11,5)));
        assertFalse(game.isValid(new Point(3,0)));
        assertFalse(game.isValid(new Point(4, 21)));
        assertTrue(game.isValid(new Point(5,3)));
    }

    @Test
    void eatApple() {

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

    }
}