import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class View extends Layout {

    private int position;
    TerminalHandler terminalHandler = new TerminalHandler();

    /* ******************
     *  CONSTRUCTOR     *
     * ******************/

    /**
     * This constructor creates a new View
     * @param height the height of the view
     * @param width the width of the view
     * @param leftUpperCorner the left upper corner of the view
     */
    public View(int height, int width, Point leftUpperCorner) {
        super(height, width, leftUpperCorner);
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    /**
     * This method sets the position of the view
     * @param newPosition  | The new position of the view
     * @post    | getPosition() == newPosition
     * @return  | void
     */
    public void setPosition(int newPosition) {
        this.position = newPosition;
    }

    /**
     * This method returns the position of the FileBufferView
     * @return: int
     */
    public int getPosition() {
        return this.position;
    }

    /* ******************
     *  INSPECT CONTENT *
     * ******************/

    /**
     * This method moves the cursor or snake in the given direction
     * @param dir | The direction to move
     * @return    | void
     */
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

    /**
     * If the view is a FileBufferView, this method inserts a new line break at the FileBufferViews insertion
     * point. If the focused view is a gameView this method starts a new game, if the game was game over.
     * @return      | void
     */
    public void addNewLineBreak(String newLine) throws FileNotFoundException {
        return;
    }

    /**
     * This method adds a new character to the file
     * It also makes a new Edit object and set this new Edit as the lastEdit
     * @param c  | The character to add
     * @return:  | boolean, return true if the character was added
     */
    public void addNewChar(char c) {
        return;
    }

    /** This method deletes the character before the insertionPoint.
     *  It also makes a new Edit object and set this new Edit as the lastEdit
     * @return  | boolean
     */
    public void deleteChar() {
        return;
    }

    /* ******************
     *    SAVE BUFFER   *
     * ******************/
    
    /** 
     * This method saves the buffer of the file if the View is a FileBufferView, otherwise does nothing
     * @param newLine | the new line to add to the buffer
     * @return  | void
     */
    public void saveBuffer(String newLine) throws IOException {
        return;
    }

    /* *****************
     *    ROTATE VIEW  *
     * *****************/

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

    @Override
    protected View flip() {
        return this;
    }

    /* ******************
     *   UNDO / REDO    *
     * ******************/

    /**
     * This method undoes the last edit and uses therefor the undo method of the lastEdit
     * It also sets the lastEdit to the previous edit 
     * @return  | boolean, true if the undo was successful, false otherwise
     */
    public void undo() {
        return;
    }

    /**
     * This method redoes the last edit and uses therefor the redo method of the lastEdit
     * It also sets the lastEdit to the next edit 
     * @return  | boolean, true if the redo was successful, false otherwise
     */
    public void redo() {
        return;
    }

    /* ******************
     *  OPEN GAME VIEW  *
     * ******************/

     /** 
     * This method inserts the given views in the layout
     * @param focus  | The index of the focussed view
     * @param parent | The parent of the focussed view
     * @param views  | The array of views that should be inserted
     * @return  | Layout, the new layout after inserting the views
     */
    @Override
    public Layout insertViews(int focus, CompositeLayout parent, View[] views) {
        if (getPosition() == focus) {
            View[] newViews = new View[views.length + 1];
            newViews[0] = this;
            for (int k = 0; k < views.length; k++) {
                newViews[k + 1] = views[k];
            }
            return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newViews);
        }
        return this;
    }

    /* ************************
     *  OPEN FILEBUFFER VIEW  *
     * ************************/

    /**
     * This method duplicates the FileBufferView,
     * @return  | View[], an array with the FileBufferView duplicated
     *          | View[], an empty array if gameView
     */
    public abstract View[] duplicate();

    /* ****************
     *    RUN SNAKE   *
     * ****************/

    /**
     * This method returns the next deadline of the system
     * @return  | long, the next deadline
     */
    public abstract long getNextDeadline();

    /**
     * This method returns the current tick of the system
     * @return  | long, the current tick
     */
    public abstract long getTick();

    /**
     * This method ticks the game
     * @return  | void
     */
    public void tick() throws IOException {};

    public View[] getDirectoryView(LayoutManager manager) {
        return new View[] {};
    }

    public View[] parseJson(LayoutManager manager) {
        return new View[] {};
    }

    public View closeView(int focus, CompositeLayout parent) throws IOException {
        if (getPosition() == focus) {
            return null;
        }
        return this;
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    /** 
     * This method shows the content of the FileBufferView and the updated scrollbars
     * @return  | String, the content of the FileBufferView
     * Visibile for testing
     */
    abstract String[] makeShow();

    /** 
     * This method returns the created horizontal scrollbar
     * @return  | String, the horizontal scrollbar
     * Visibile for testing
     */
    abstract String makeHorizontalScrollBar();

    /** 
     * This method returns the created vertical scrollbar
     * @return  | char[], the vertical scrollbar
     * Visibile for testing
     */
    abstract char[] makeVerticalScrollBar();

    /**
     * This method shows the content of the FileBufferView and the updated scrollbars
     * @return  | void
     */
    public void show(TerminalInterface printer) {
        String[] toShow = makeShow();
        String horizontalBar = makeHorizontalScrollBar();
        if (horizontalBar.length() > getWidth()) horizontalBar = horizontalBar.substring(0, getWidth());
        char[] verticalBar = makeVerticalScrollBar();
        //  Print BufferContent/Game
        for (int i = 0; i < toShow.length; i++) {
            if (toShow[i] != null) {
                printer.printText(getLeftUpperCorner().getX() + i,
                        getLeftUpperCorner().getY(), toShow[i]);
            }
        }
        //  Print verticalSideBar
        for (int i = 0; i < verticalBar.length; i++) {
            printer.printText(getLeftUpperCorner().getX() + i, getLeftUpperCorner().getY() + getWidth() - 1,
                    String.valueOf(verticalBar[i]));
        }
        //  Print horizontalSideBar
        printer.printText(getLeftUpperCorner().getX() + getHeigth() - 1, getLeftUpperCorner().getY(), horizontalBar);
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    @Override
    public void updateSize(int heigth, int width, Point leftUpperCorner) {
        setHeigth(heigth);
        setWidth(width);
        setLeftUpperCorner(leftUpperCorner);
    }

    public void updateSize(View otherView) {
        setHeigth(otherView.getHeigth());
        setWidth(otherView.getWidth());
        setLeftUpperCorner(otherView.getLeftUpperCorner());
        setPosition(otherView.getPosition());
    }

    /**
     * This method returns the cursor of the view
     * @return  | Point, the point where the cursor is in the view
     */
    public abstract Point getCursor();

    /**
     * This method initializes the position of the view at index i
     * @param i | The index of the view
     * @return  | void
     */
    @Override
    public void initViewPosition(int i) {
        setPosition(i);
    }

    /**
     * This method returns the focused view at the given index i
     * @return  | View || null
     */
    @Override
    public View getFocusedView(int i) {
        if (getPosition() == i) {
            return this;
        }
        return null;
    }

    @Override
    public Buffer getBufferByName(String name) {
        return null;
    }

    /**
     * This method returns the number of views
     * @return  | int
     */
    @Override
    public int countViews() {
        return 1;
    }
}
