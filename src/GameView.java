import java.io.IOException;
import java.util.Arrays;

public class GameView extends View{
    private Game game;

    private long lastMove;

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/
    /**
     * This constructor creates a new GameView with the given parameters height, width and leftUpperCorner
     * @param height the height of the GameView
     * @param width the width of the GameView
     * @param leftUpperCorner the left upper corner of the GameView
     * @post getHeigth() == height
     */
    public GameView(int height, int width, Point leftUpperCorner) {
        super(height, width, leftUpperCorner);
        this.game = new Game(height - 1, width - 1);
        this.lastMove = System.currentTimeMillis();
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    /**
     * This method sets the game of the GameView to the given parameter newGame
     * @param newGame the new game of the GameView
     * @post getGame() == newGame
     * @return: void
     */
    public void setGame(Game newGame) {
        this.game = newGame;
    }

    /**
     * This method returns the game of the GameView
     * @return: Game, the game of the GameView
     */
    public Game getGame() {
        return game;
    }

    /**
     * This method sets the last move of the GameView to the given parameter newLastMove
     * @param newLastMove the new last move of the GameView
     * @post getLastMove() == newLastMove
     * @return: void
     */
    public void setLastMove(long newLastMove) {
        this.lastMove = newLastMove;
    }

    /**
     * This method returns the last move of the GameView
     * @return: long, the last move of the GameView
     */
    public long getLastMove() {
        return lastMove;
    }

    /* ******************
     *  INSPECT CONTENT *
     * ******************/

    /**
     * This method moves the snake in the given direction and ticks the game if the snake is moving in the given direction 
     * @param dir the direction to move the snake
     * @return void
     */
    public void move(Direction dir) {
        if (getGame().getSnake() != null) {
            changeDir(dir);
            if (getGame().getSnake().getDir().equals(dir.point)) {
                tick();
            }
        }
    }

    /* **********************
     *  EDIT BUFFER CONTENT *
     ************************/

    @Override
    public boolean addNewLineBreak() {
        if (getGame().getSnake() == null) {
            runNewGame();
        }
        return false;
    }

    @Override
    public boolean addNewChar(char c) {
        return false;
    }

    @Override
    public boolean deleteChar() {
        return false;
    }

    /* ******************
     *   CLOSE BUFFER   *
     * ******************/

    @Override
    public Layout closeView(int focus, CompositeLayout parent) {
        return null;
    }

    /* ******************
     *    SAVE BUFFER   *
     * ******************/

    @Override
    public void saveBuffer(String newLine) {
        return;
    }

    /* ******************
     *   UNDO / REDO    *
     * ******************/

    public boolean undo() {
        return false;
    }

    public boolean redo() {
        return false;
    }

    /* ************************
     *  OPEN FILEBUFFER VIEW  *
     * ************************/

    @Override
    public View[] duplicate() {
        return new View[] {};
    }

    @Override
    public void updateViews(int focus, Point insert, char c, boolean isDeleted, FileBuffer buffer) {
        return;
    }


    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

   
    /** 
     * This method shows the content of the game and checks if the snake is still alive if not then it shows the loss screen
     * @return: String, the content of the game
     * Visibile for testing
     */
    @Override
    String[] makeShow() {
        if (getGame().getSnake() != null) {
            char[][] abstractGrid = getGame().getAbstractGrid();
            String[] result = new String[abstractGrid.length];
            for (int i = 0; i < abstractGrid.length; i++) {
                result[i] = String.copyValueOf(abstractGrid[i]);
            }
            return result;
        }
        return showLoss();
    }

    /**
     * This method shows the loss screen
     * @return: String[], the loss screen
     * Visibile for testing
     */
    String[] showLoss() {
        String lossStr = "GAME OVER - Press enter to restart";
        String[] result = new String[getHeigth() - 1];
        if (getWidth() - 1 > lossStr.length()) {
            StringBuilder lossString = new StringBuilder();
            for (int i = 0; i < Math.floor((float) (getWidth() - lossStr.length() - 1) / 2); i++) {
                lossString.append(" ");
            }
            lossString.append(lossStr);
            while (lossString.length() < getWidth() - 1) {
                lossString.append(" ");
            }
            result[(int) Math.floor((float) (getHeigth() - 1) / 2)] = lossString.toString();
        }
        return result;
    }

    /** 
     * This method returns the created vertical scrollbar of the game, what is just a string of '#' characters
     * @return: char[], the vertical scrollbar
     * Visibile for testing
     */
    @Override
    char[] makeVerticalScrollBar() {
        char[] result = new char[getHeigth()];
        Arrays.fill(result, '#');
        return result;
    }

    /** 
     * This method returns the created horizontal scrollbar of the game, 
     * what is just a string of '#' characters with the score of the game
     * @return: String, the horizontal scrollbar of the game
     * Visibile for testing
     */
    @Override
    String makeHorizontalScrollBar() {
        String result = "Score: " + getGame().getScore() + " ";
        while (result.length() < getWidth()) {
            result += "#";
        }
        return result;
    }

    /* ****************
     *    RUN SNAKE   *
     * ****************/

    /**
     * This method returns the next deadline of the game which is the last move + the tick
     * @return: long, the next deadline
     */
    @Override
    public long getNextDeadline() {
        return getLastMove() + getTick();
    }

    /**
     * This method returns the current tick of the system
     * @return: long, the current tick
     */
    @Override
    public long getTick() {
        return getGame().getTick();
    }

    /**
     * This method ticks the game and sets the last move to the current time
     * @return: void
     */
    public void tick() {
        getGame().tick();
        setLastMove(System.currentTimeMillis());
    }

    /**
     * This method changes the direction of the snake to the given new direction
     * @param newDir the new direction of the snake
     * @return: void
     */
    public void changeDir(Direction newDir) {
        getGame().changeDir(newDir);
    }

    /**
     * This method start a new game 
     * @return: void
     */
    private void runNewGame() {
        setGame(new Game(getHeigth(), getWidth()));
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/
    /**
     * This method returns the cursor of the game which is the left upper corner
     * @return: Point, LeftUpperCorner
     */
    public Point getCursor() {
        return getLeftUpperCorner();
    }

    /** 
     * This method updates the size of the layout to the given parameters heigth, width and leftUpperCorner
     * @post getHeigth() == heigth
     * @post getWidth() == width
     * @post getLeftUpperCorner() == leftUpperCorner
     * @return: void
     */
    @Override
    public void updateSize(int heigth, int width, Point leftUpperCorner) {
        setHeigth(heigth);
        setWidth(width);
        setLeftUpperCorner(leftUpperCorner);
        getGame().updateSize(heigth - 1, width - 1);
    }
}
