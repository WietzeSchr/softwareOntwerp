import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameViewTest {

    @Test
    void testSetters() {
        GameView gv = new GameView(10, 20, new Point(5, 15));
        Game newGame = new Game(10, 20);
        gv.setGame(newGame);
        gv.setLastMove(1500);
        assertEquals(gv.getGame(), newGame);
        assertEquals(gv.getLastMove(), 1500);
    }

    @Test
    void testMove() {
        GameView gv = new GameView(10, 20, new Point(5, 15));
        Game newGame = new Game(10, 20);
        Point[] points = new Point[] {new Point(1,4), new Point(1,3), new Point(1,2), new Point(1,1)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), new Point(0,1));
        newGame.setSnake(snake);
        gv.setGame(newGame);
        gv.move(new Point(0, -1));
        assertEquals(gv.getGame().getSnake().getHead(), new Point(1,4));
        assertArrayEquals(gv.getGame().getSnake().getBody().toArray(), new Point[] {new Point(1,3), new Point(1,2), new Point(1,1)});
        gv.move(new Point(0, 1));
        assertEquals(gv.getGame().getSnake().getHead(), new Point(1,5));
        assertArrayEquals(gv.getGame().getSnake().getBody().toArray(), new Point[] {new Point(1,4), new Point(1,3), new Point(1,2)});
    }

    @Test
    void testRestart() {    // == addNewLineBreak()
        GameView gv = new GameView(10, 20, new Point(5, 15));
        gv.getGame().loseGame();
        assertNull(gv.getGame().getSnake());
        gv.addNewLineBreak();
        assertNotNull(gv.getGame().getSnake());
    }

    @Test
    void testMakeShow() {

    }
}