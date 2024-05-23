import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameViewTest {

    @Test
    void testSetters() throws IOException {
        GameView gv = new GameView(10, 20, new Point(5, 15));
        Game newGame = new Game(10, 20);
        gv.setGame(newGame);
        //gv.setLastMove(1500);
        gv.saveBuffer("\n");
        gv.addNewChar('x');
        gv.enterPressed("\n");
        gv.deleteChar();
        gv.undo();
        gv.redo();
        //gv.updateViews(1, new Point(1, 1), 'x', true, new FileBuffer(new String[] {}, "test"));
        assertEquals(gv.getGame(), newGame);
        //assertEquals(gv.getLastMove(), 1500);
        assertArrayEquals(gv.duplicate(), new View[] {});
    }

    @Test
    void testDerivedAttributes() {
        GameView gv = new GameView(10, 20, new Point(5, 15));
        Game newGame = new Game(10, 20);
        gv.setGame(newGame);
        //gv.setLastMove(1500);
        assertEquals(gv.getCursor(), new Point(5, 15));
        //assertEquals(gv.getTick(), 1000);
        //assertEquals(gv.getNextDeadline(), 2500);
    }

    @Test
    void testMove() {
        GameView gv = new GameView(10, 20, new Point(5, 15));
        Game newGame = new Game(10, 20);
        Point[] points = new Point[] {new Point(1,4), new Point(1,3), new Point(1,2), new Point(1,1)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.EAST);
        newGame.setSnake(snake);
        gv.setGame(newGame);
        gv.move(Direction.WEST);
        assertEquals(gv.getGame().getSnake().getHead(), new Point(1,4));
        assertArrayEquals(gv.getGame().getSnake().getBody().toArray(), new Point[] {new Point(1,3), new Point(1,2), new Point(1,1)});
        gv.move(Direction.EAST);
        assertEquals(gv.getGame().getSnake().getHead(), new Point(1,5));
        assertArrayEquals(gv.getGame().getSnake().getBody().toArray(), new Point[] {new Point(1,4), new Point(1,3), new Point(1,2)});
    }

    @Test
    void testRestart() {    // == addNewLineBreak()
        GameView gv = new GameView(10, 20, new Point(5, 15));
        gv.getGame().loseGame();
        assertNull(gv.getGame().getSnake());
        gv.enterPressed("\n");
        assertNotNull(gv.getGame().getSnake());
    }

    @Test
    void testMakeShow() {
        GameView gv = new GameView(3, 6, new Point(5, 15));
        Game newGame = new Game(2, 5);
        Point[] points = new Point[] {new Point(1,4), new Point(1,3), new Point(1,2), new Point(1,1)};
        Snake snake = new Snake(new ArrayList<>(List.of(points)), Direction.EAST);
        newGame.setSnake(snake);
        gv.setGame(newGame);
        assertArrayEquals(gv.makeShow(), new String[] {"-oo> ", "     "});
        assertArrayEquals(gv.makeVerticalScrollBar(), new char[] {'#', '#', '#'});
        assertEquals(gv.makeHorizontalScrollBar(), "Score: 0 ");
        newGame.loseGame();
        gv.setWidth(37);
        assertArrayEquals(gv.makeShow(), new String[] {null, " GAME OVER - Press enter to restart "});
    }
}