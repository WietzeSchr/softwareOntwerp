
import java.io.IOException;
import java.util.Arrays;

/* **********
 *  LAYOUT  *
 * **********/
public abstract class Layout {

    private final Box box;

    private CompositeLayout parent;

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/

    /** 
     * This constructor creates a new Layout with the given height, width and leftUpperCorner
     * @pre  | heigth > 0
     * @pre  | width > 0
     * @post | getHeigth() == height
     * @post | getWidth() == width
     * @post | getParent() == null
     * @post | getLeftUpperCorner() == leftUpperCorner
     */
    public Layout(int height, int width, Point leftUpperCorner) {
        this.box = new Box(height, width, leftUpperCorner);
        this.parent = null;
    }
    
    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/
    private Box getBox() {
        return box;
    }

    /**
     * This method sets the heigth of the layout to the given parameter
     * @pre  | newHeigth > 0
     * @post | getHeigth() == newHeigth
     * @param newHeight | The newHeigth of the layout
     */
    public void setHeigth(int newHeight) {
        getBox().setHeight(newHeight);
    }

    /** 
     * This method returns the height of the layout
     * @return  | int, the height of the layout
     */
    public int getHeigth() {
        return getBox().getHeight();
    }

    /** 
     * This method sets the width of the layout to the given parameter newWidth
     * @pre  | newWidth > 0
     * @post | getWidth() == newWidth
     * @return  | void
     */
    public void setWidth(int newWidth) {
        getBox().setWidth(newWidth);
    }

    /** 
     * This method returns the width of the layout
     * @return  | int, the width of the layout
     */
    public int getWidth() {
        return getBox().getWidth();
    }

    /** 
     * This method sets the parent of the layout to the given parameter newParent
     * @post    | getParent() == newParent
     * @return  | void
     */
    public void setParent(CompositeLayout newParent) {
        this.parent = newParent;
    }

    /** 
     * This method returns the parent of the layout
     * @return  | CompositeLayout, the parent of the layout
     */
    public CompositeLayout getParent() {
        return parent;
    }

    /** 
     * This method sets the leftUpperCorner of the layout to the given parameter newLeftUpperCorner
     * @post    | getLeftUpperCorner() == newLeftUpperCorner
     * @return  |void
     */
    public void setLeftUpperCorner(Point newLeftUpperCorner) {
        getBox().setLeftUpperPoint(newLeftUpperCorner);
    }

    /** 
     * This method returns the leftUpperCorner of the layout
     * @return  | Point, the leftUpperCorner of the layout
     */
    public Point getLeftUpperCorner() {
        return getBox().getLeftUpperPoint();
    }

    /**
     * This method returns the subLayouts of the layout. Returns itself when it has no subLayouts
     * @return  | Layout[], the subLayouts of the layout
     */
    public Layout[] getSubLayouts() {
        return new Layout[] {this};
    }

    /**
     * this method return the amount of subLayouts of the layout. Returns 1 if it has no subLayouts
     * @return  | int, The amount of subLayouts
     */
    public int countSubLayouts() {
        return 1;
    }

    /* ******************
     *  INSPECT CONTENT *
     * ******************/

    /** 
     * This method moves the insertion point of the focused view in the given direction if the focussed view is
     * a fileBufferView or (tries to) move the snake in the given direction if the focussed view is a gameView
     * @param dir   | The direction in which the insertion point or the snake should move
     * @param focus | The index of the focussed view
     * @return  | void
     */	
    public void arrowPressed(Direction dir, int focus) {
        View focussed = getFocusedView(focus);
        focussed.move(dir);
    }

    /**
     * This method returns the position of the next focused view and when the current focused view is the last view then it returns -1
     * because there is no next view
     * @param focus | The index of the current focussed view
     * @return      | int, the index of the next focussed view
     */
    public int getNextFocus(int focus) {
        if (focus == countViews()) {
            return -1;
        }
        return focus + 1;
    }

    /**
     * This method returns position of the previous view
     * @param focus | The index of the current focussed view
     * @return      | int, the index of the previous focussed view
     */
    public int getPreviousFocus(int focus) {
        if (focus == 1) {
            return countViews();
        }
        return focus - 1;
    }

    /* **********************
     *  EDIT BUFFER CONTENT *
     ************************/

    /** 
     * This method adds a new character at the insertion point of the focused view if it is a FileBufferView. If the
     * focused view is a GameView, nothing happens
     * @param c     | The character that should be added
     * @param focus | The index of the focussed view
     * @return      | void
     */
    public void addNewChar(char c, int focus) {
        View focussed = getFocusedView(focus);
        focussed.addNewChar(c);
    }

    /** 
     * This method deletes the character at the insertion point of the focused view if it is a FileBufferView. If the
     * focused view is a GameView, nothing happens
     * @param focus | The index of the focussed view
     * @return      | void
     */
    public void deleteChar(int focus) {
        View focussed = getFocusedView(focus);
        focussed.deleteChar();
    }

    /* ******************
     *   CLOSE VIEW     *
     * ******************/

    /**
     * This method closes the focused view. If the focused view is a FileBufferView with a dirty buffer, the user
     * should press y to discard changes and close the buffer or n to cancel the request. If the user doesn't press
     * a key in 3 seconds, the request is cancelled automatically. This is done for testing purposes
     * @param focus | The index of the focused view
     * @return      | Layout, the new layout
     */
    public Layout closeView(int focus) throws IOException {
        int heigth = getHeigth();
        int width = getWidth();
        CompositeLayout parent = getFocusedView(focus).getParent();
        Layout result = closeView(focus, parent);
        if (result != null) {
            result.initViewPosition(1);
            result.updateSize(heigth, width, new Point(1, 1));
        }
        return result;
    }

    /** 
     * This method returns the new focus after closing the focussed view
     * @param focus | The index of the focussed view
     * @return      | int, The new index of the focussed view
     */
    public int getNewFocus(int focus) {
        if (focus > countViews()) {
            return focus - 1;
        }
        return focus;
    }

    /** 
     * This method closes the buffer and updates the subLayouts
     * @return: Layout, the new layout after closing the buffer
     */
    public abstract Layout closeView(int focus, CompositeLayout parent) throws IOException;

    /* ******************
     *    SAVE BUFFER   *
     * ******************/

    /** 
     * This method saves the buffer of the focused view if the focused view is a FileBufferView.
     * If the focused view is a GameView, nothing happens
     * @param focus   | The index of the focussed view
     * @param newLine | The new line that should be added to the buffer
     * @return        | void
     */
    public void saveBuffer(int focus, String newLine) throws IOException {
        View focussed = getFocusedView(focus);
        focussed.saveBuffer(newLine);

    }

    /* *****************
     *    ROTATE VIEW  *
     * *****************/

    /**
     * Checks the layout to call the appropriate method for rotating the focused view with the next focused view
     * If the layout is only one view, nothing happens
     * @pre | dir == 1 || dir == -1
     * @param dir   | 1: counterclockwise, -1: clockwise
     * @param focus | The index of the focused view
     * @return      |Layout, the new layout after rotating the view
     */
    protected abstract Layout rotateView(int dir, int focus);

    /**
     * This method rotates the layout in the case that the view and the next view are siblings and their parent
     * contains more than 2 subLayouts
     * @pre | getFocusedView(focus).getParent() == parent
     * @pre | getFocusedView(nextFocus).getParent() == parent
     * @pre | parent.countSubLayouts() > 2
     * @pre | dir == 1 || dir == -1
     * @param dir       | 1: counterclockwise, -1: clockwise
     * @param focus     | The index of the focused view
     * @param nextFocus | The index of the next focused view
     * @param parent    | The parent of the focused view
     * @return  | Layout, the new layout after rotating the siblings
     * Visible for testing
     */
    protected abstract Layout rotateSiblings(int dir, int focus, int nextFocus, CompositeLayout parent);

    /**
     * This method rotates the layout in the case that the view and the next view are siblings and their parent
     * contains 2 subLayouts
     * @pre | getFocusedView(focus).getParent() == parent
     * @pre | getFocusedView(nextFocus).getParent() == parent
     * @pre | parent.countSubLayouts() == 2
     * @pre | dir == 1 || dir == -1
     * @param dir       | 1: counterclockwise, -1: clockwise
     * @param focus     | The index of the focused view
     * @param nextFocus | The index of the next focused view
     * @param parent    | The parent of the focused view
     * @return   | Layout, the new layout after rotating the siblings
     * Visible for testing
     */
    protected abstract Layout rotateSiblingsFlip(int dir, int focus, int nextFocus, CompositeLayout parent);
  
    /**
     * This parent rotates the layout in the case that the view and the next view aren't siblings and the parent
     * of the next view has two subLayouts
     * @pre | getFocusedView(focus).getParent() == parent1
     * @pre | getFocusedView(nextFocus).getParent() == parent2
     * @pre | parent2.countSubLayouts() == 2
     * @pre | dir == 1 || dir == -1
     * @param dir      | 1: counterclockwise, -1: clockwise
     * @param focus    | The index of the focused view
     * @param nextView | The next view
     * @param parent1  | The parent of the focused view
     * @param parent2  | The parent of the next view
     * @return  | Layout, The new layout after rotating the view
     */
    protected abstract Layout rotateNonSiblingsPromote(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2);

    /**
     * This parent rotates the layout in the case that the view and the next view aren't siblings and the parent
     * of the next view has more than two subLayouts
     * @pre | getFocusedView(focus).getParent() == parent1
     * @pre | getFocusedView(nextFocus).getParent() == parent2
     * @pre | parent2.countSubLayouts() > 2
     * @pre | dir == 1 || dir == -1
     * @param dir      | 1: counterclockwise, -1: clockwise
     * @param focus    | The index of the focused view
     * @param nextView | The next view
     * @param parent1  | The parent of the focused view
     * @param parent2  | The parent of the next view
     * @return  | Layout, The new layout after rotating the view
     */
    protected abstract Layout rotateNonSiblings(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2);

    /**
     * This method flips the layout. If the layout is a view, it does nothing. If the layout is a CompositeLayout
     * it return a new CompositeLayout from the other subClass with the same subLayouts
     * @return  | Layout, the flipped layout
     */
    protected abstract Layout flip();

    /* ******************
     *   UNDO / REDO    *
     * ******************/

    /**
     * This method checks if the last edit of the focused view can be undone and if so, undoes it and updates the views
     * If The focused view is a gameView nothing happens
     * @param focus | The index of the focussed view
     * @return      | void
     */
    public void undo(int focus) {
        View focussed = getFocusedView(focus);
        focussed.undo();
    }

    /**
     * This method checks if the last edit of the focused view can be redone and if so, redoes it and updates the views
     * If the focused view is a GameView, nothing happens
     * @param focus this is the index of the focussed view
     * @return: void
     */
    public void redo(int focus) {
        View focussed =getFocusedView(focus);
        focussed.redo();
    }

    /* *************
     *  OPEN VIEW  *
     * *************/

    /** 
     * This method opens the given views in the layout, updates the size of the layout and initializes the view positions
     * If parent is a SideBySideLayout the new views are inserted in the same parent, next to the focused view. If the
     * parent is a StackedLayout the focused view is replaced by a SydeBySideLayout with the focused view and the
     * given views as new SubLayouts
     * @param focus  | This is the index of the focussed view
     * @param parent | This is the parent of the focussed view
     * @param views  | The array of views that should be opened
     * @return  | Layout, the new layout after opening the views
     */
    public Layout openViews(int focus, CompositeLayout parent, View[] views) {
        if (views.length == 0) return this;
        Layout result = insertViews(focus, parent, views);
        result.updateSize(getHeigth(), getWidth(), new Point(1, 1));
        result.initViewPosition(1);
        return result;
    }


    /** 
     * This method opens a new gameview in the layout
     * @param focus | this is the index of the focussed view
     * @return  | Layout, the new layout after opening the gameView
     */
      public Layout newGame(int focus) {
        View focussed = getFocusedView(focus);
        return openViews(focus, focussed.getParent(),
                new View[] {new GameView(focussed.getHeigth(), focussed.getWidth(), focussed.getLeftUpperCorner().add(new Point(0, focussed.getWidth() / 2)))});
    }


    /* *******************
     *  DUPLICATED VIEW  *
     * *******************/
  
    /** 
     * This method inserts the given views in the layout
     * @pre | views.length > 0
     * @param focus  | The index of the focussed view
     * @param parent | The parent of the focussed view
     * @param views  | The array of views that should be inserted
     * @return: Layout, the new layout after inserting the views
     */
    public abstract Layout insertViews(int focus, CompositeLayout parent, View[] views);

    /* *******************
     *  DUPLICATED VIEW  *
     * *******************/
  
    /**
     * This method duplicates the focused view if the focused view is a FileBufferView
     * @param focus | this is the index of the focussed view
     * @return      | Layout, the new layout after duplicating the view
     */
    public Layout newBufferView(int focus) {
        View focussed = getFocusedView(focus);
        View[] duplicates = focussed.duplicate();
        return openViews(focus, focussed.getParent(), duplicates);
    }

    /* ****************
     *    RUN SNAKE   *
     * ****************/

    /**
     * This method runs the snake in the focused view
     * @param focus | The index of the focussed view
     * @return      | void
     */
    public void tick(int focus) throws IOException {
        getFocusedView(focus).tick();
    }

    public View[] openDirectoryView(int focus, LayoutManager manager) {
        return getFocusedView(focus).getDirectoryView(manager);
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    /** 
     * This method shows the layout of the subLayouts
     * @return  | void
     */
    public abstract void show();

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    /**
     * This method initializes the view positions, assigning all views a position from i to i + countViews() - 1
     * layout. The views are ordered depth first
     * @return  | void
     */
    public abstract void initViewPosition(int i);

    /** 
     * This method returns the view with position i
     * @param i  | The index of the view that should be returned
     * @return   | View
     */	
    public abstract View getFocusedView(int i);

    /** 
     * This method returns the number of views
     * @return  | int, the number of views
     */
    public abstract int countViews();

    /**
     * This method returns the next deadline of the focused view
     * @param focus | The index of the focussed view
     * @return      | long, The NextDeadline of the focused view
     */
    public long getNextDeadline(int focus) {
         return getFocusedView(focus).getNextDeadline();
    }

    public void updateSize(int heigth, int width) {
        updateSize(heigth, width, new Point(1,1));
    }

    /**
     * This method updates the size of the layout, possibly it's subLayouts
     * @pre  | heigth > 0
     * @pre  | width > 0
     * @post | getHeigth() = heigth
     * @post | getWidth() = width
     * @post | getLeftUpperCorner() = leftUpperCorner
     * @param heigth    | The new heigth of the layout
     * @param width     | The new width of the layout
     * @param leftUpperCorner | The new left upper corner of the layout
     */
    public abstract void updateSize(int heigth, int width, Point leftUpperCorner);

    public abstract Buffer getBufferByName(String name);

    /**
     * Checks if the structure of the Layouts match
     */
    @Override
    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) {
            return false;
        }
        if (o instanceof CompositeLayout) {
            Layout[] subLayouts = this.getSubLayouts();
            Layout[] otherSubLayouts = ((CompositeLayout) o).getSubLayouts();
            return Arrays.equals(subLayouts, otherSubLayouts);
        }
        return true;
    }
}

