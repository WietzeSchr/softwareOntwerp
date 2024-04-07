import io.github.btj.termios.Terminal;

import java.io.IOException;

public class GameView extends View{

    TerminalHandler terminalHandler = new TerminalHandler();
    private Game game;

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/
    public GameView(int height, int width, Point leftUpperCorner) {
        super(height, width, leftUpperCorner);
        this.game = new Game(height, width);
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    public void setGame(Game newGame) {
        this.game = newGame;
    }

    public Game getGame() {
        return game;
    }

    /* ******************
     *  INSPECT CONTENT *
     * ******************/

    public void move(Point dir) {
        changeDir(dir);
        if (getGame().getSnake().getDir().equals(dir)) {
            tick();
        }
    }

    /* **********************
     *  EDIT BUFFER CONTENT *
     ************************/

    @Override
    public void addNewLineBreak() {
        if (getGame().getSnake() == null) {
            setGame(new Game(getHeigth(), getWidth()));
        }
    }

    @Override
    public void addNewChar(char c) {
        return;
    }

    @Override
    public void deleteChar() {
        return;
    }

    /* ******************
     *   CLOSE BUFFER   *
     * ******************/

    @Override
    public Layout closeBuffer(int focus, CompositeLayout parent) throws IOException {
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

    public void undo() {
        return;
    }

    public void redo() {
        return;
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    @Override
    String[] makeShow() {
        if (getGame().getSnake() != null) {
            char[][] abstractGrid = getGame().getAbstractGrid();
            String[] result = new String[abstractGrid.length + 1];
            for (int i = 0; i < abstractGrid.length; i++) {
                result[i] = String.copyValueOf(abstractGrid[i]) + "#";
            }
            result[abstractGrid.length] = "Score: " + getGame().getScore() + " ";
            while (result[abstractGrid.length].length() != getWidth()) {
                result[abstractGrid.length] += "#";
            }
            return result;
        }
        return showLoss();
    }

    public String[] showLoss() {
        return new String[0];
    }

    /* ****************
     *    RUN SNAKE   *
     * ****************/

    @Override
    public long getTick() {
        return getGame().getTick();
    }

    public void tick() {
        getGame().tick();
    }

    public void changeDir(Point newDir) {
        getGame().changeDir(newDir);
    }

    private void runNewGame() {
        setGame(new Game(getHeigth(), getWidth()));
    }

    public void loseGame() throws IOException {
        boolean rerun = false;
        boolean close = false;
        while (! rerun && ! close) {
            int c = terminalHandler.readByte();
            if (c == 13) {
                rerun = true;
            }
            else if (c == 17) {
                close = true;
            }
        }
        if (rerun) {
            runNewGame();
        }
        if (close) {
            terminalHandler.clearScreen();
        }
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    public Point getCursor() {
        return getLeftUpperCorner();
    }

    /** This method updates the size of the layout to the given parameters heigth, width and leftUpperCorner
     * and updates the scroll states
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
    }
}
