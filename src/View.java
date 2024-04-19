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
     * @param newPosition the new position of the view
     * @return void
     * @post getPosition() == newPosition
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
     * @param dir the direction to move
     * @return void
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
     * This method adds a new line break to the buffer 
     * It also makes a new Edit object and set this new Edit as the lastEdit
     * @return: boolean
     */
    public abstract boolean addNewLineBreak();

    /** This method adds a new character to the file
     *  It also makes a new Edit object and set this new Edit as the lastEdit
     * @param c the character to add
     * @return: boolean
     */
    public abstract boolean addNewChar(char c);

    /** This method deletes the character before the insertionPoint.
     *  It also makes a new Edit object and set this new Edit as the lastEdit
     * @return: boolean
     */
    public abstract boolean deleteChar();

    /* ******************
     *   CLOSE VIEW     *
     * ******************/

    /* ******************
     *    SAVE BUFFER   *
     * ******************/
    
    /** 
     * This method saves the buffer of the file and updates the scroll states
     * @param newLine the new line to add to the buffer
     * @return: void
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
     * @return: boolean, true if the undo was successful, false otherwise
     */
    public abstract boolean undo();

    /**
     * This method redoes the last edit and uses therefor the redo method of the lastEdit
     * It also sets the lastEdit to the next edit 
     * @return: boolean, true if the redo was successful, false otherwise
     */
    public abstract boolean redo();

    /* ******************
     *  OPEN GAME VIEW  *
     * ******************/

     /** 
     * This method inserts the given views in the layout
     * @param focus this is the index of the focussed view
     * @param parent this is the parent of the focussed view
     * @param views this is the array of views that should be inserted
     * @return: Layout, the new layout after inserting the views
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
     * This method duplicates the FileBufferView
     * @return: View[], an array with the FileBufferView duplicated
     */
    public abstract View[] duplicate();

    /* ****************
     *    RUN SNAKE   *
     * ****************/

    /**
     * This method returns the next deadline of the system
     * @return: long, the next deadline
     */
    public abstract long getNextDeadline();

    /**
     * This method returns the current tick of the system
     * @return: long, the current tick
     */
    public abstract long getTick();

    /**
     * This method ticks the game
     * @return: void
     */
    public abstract void tick() throws IOException;

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    /** 
     * This method shows the content of the FileBufferView and the updated scrollbars
     * @return: String, the content of the FileBufferView
     * Visibile for testing
     */
    abstract String[] makeShow();

    /** 
     * This method returns the created horizontal scrollbar
     * @return: String, the horizontal scrollbar
     * Visibile for testing
     */
    abstract String makeHorizontalScrollBar();

    /** 
     * This method returns the created vertical scrollbar
     * @return: char[], the vertical scrollbar
     * Visibile for testing
     */
    abstract char[] makeVerticalScrollBar();

    /**
     * This method shows the content of the FileBufferView and the updated scrollbars
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

    /**
     * This method returns the cursor of the view
     * @return: Point, the point where the cursor is in the view
     */
    public abstract Point getCursor();

    /**
     * This method initializes the position of the view at index i
     * @param i the index of the view
     * @return void
     */
    @Override
    public void initViewPosition(int i) {
        setPosition(i);
    }

    /**
     * This method returns the focused view at the given index i
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
     * @return: int
     */
    @Override
    public int countViews() {
        return 1;
    }

    @Override
    public int calcGameWidth(int focus) {
        if (getPosition() == focus) return getWidth() / 2;
        return -1;
    }
}
