import java.io.IOException;

public abstract class View extends Layout {

    private int position;
    TerminalHandler terminalHandler = new TerminalHandler();

    public View(int height, int width, Point leftUpperCorner) {
        super(height, width, leftUpperCorner);
    }
    /*
    public View(int height, int width, CompositeLayout parent, Point leftUpperCorner) {
        super(height, width, parent, leftUpperCorner);
    } */

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    public void setPosition(int newPosition) {
        this.position = newPosition;
    }

    /**
     * This method returns the position of the FileBufferView
     *
     * @return: int
     */
    public int getPosition() {
        return this.position;
    }

    /* ******************
     *  INSPECT CONTENT *
     * ******************/

    public abstract void move(Direction dir);

    @Override
    public int getNextFocus(int focus) {
        return 1;
    }

    @Override
    public int getPreviousFocus(int focus) {
        return 1;
    }

    /* **********************
     *  EDIT BUFFER CONTENT *
     ************************/

    public abstract void addNewLineBreak();

    public abstract void addNewChar(char c);

    public abstract void deleteChar();

    /* ******************
     *   CLOSE VIEW     *
     * ******************/

    /* ******************
     *    SAVE BUFFER   *
     * ******************/

    public abstract void saveBuffer(String newLine) throws IOException;

    /* *****************
     *    ROTATE VIEW  *
     * *****************/

    /**
     * This method returns the focused Layout
     *
     * @return: FileBufferView
     */
    @Override
    protected View rotateView(int dir, int focus) {
        return this;
    }

    protected View rotateSiblings(int dir, int focus, int nextFocus, CompositeLayout parent) {
        return this;
    }

    protected View rotateSiblingsFlip(int dir, int focus, int nextFocus, CompositeLayout parent) {
        return this;
    }


    protected View rotateNonSiblings(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2) {
        return this;
    }

    protected View rotateNonSiblingsPromote(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2) {
        return this;
    }

    /* ******************
     *   UNDO / REDO    *
     * ******************/

    public abstract void undo();

    public abstract void redo();

    /* ******************
     *  OPEN GAME VIEW  *
     * ******************/

    public Layout openNewGame(int focus, Layout parent) {
        if (getPosition() == focus) {
            return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(),
                    new Layout[]{this, new GameView(getHeigth(), getWidth() / 2, new Point(1, getWidth() / 2))});
        } else {
            return this;
        }
    }

    /* ****************
     *    RUN SNAKE   *
     * ****************/

    public abstract long getNextDeadline();

    public abstract long getTick();

    public abstract void tick() throws IOException;

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    abstract String[] makeShow();

    abstract String makeHorizontalScrollBar();

    abstract char[] makeVerticalScrollBar();

    /**
     * This method shows the content of the FileBufferView and the updated scrollbars
     *
     * @return: void
     */
    public void show() {
        String[] toShow = makeShow();
        String horizontalBar = makeHorizontalScrollBar();
        char[] verticalBar = makeVerticalScrollBar();
        //  Print BufferContent/Game
        for (int i = 0; i < toShow.length; i++) {
            if (toShow[i] != null) {
                terminalHandler.printText(getLeftUpperCorner().getX() + i,
                        getLeftUpperCorner().getY(), toShow[i]);
            }
        }
        //  Print verticalSideBar
        for (int i = 0; i < verticalBar.length; i++) {
            terminalHandler.printText(getLeftUpperCorner().getX() + i, getLeftUpperCorner().getY() + getWidth() - 1,
                    String.valueOf(verticalBar[i]));
        }
        //  Print horizontalSideBar
        terminalHandler.printText(getLeftUpperCorner().getX() + getHeigth() - 1, getLeftUpperCorner().getY(), horizontalBar);
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    public abstract Point getCursor();

    @Override
    public void initViewPosition(int i) {
        setPosition(i);
    }

    /**
     * This method returns the focused view at the given index i
     *
     * @return: FileBufferView || null
     */
    @Override
    public View getFocusedView(int i) {
        if (getPosition() == i) {
            return this;
        }
        return null;
    }

    /**
     * This method returns the number of views
     *
     * @return: int
     */
    @Override
    public int countViews() {
        return 1;
    }
}
