import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/* **********
 *  LAYOUT  *
 * **********/
public abstract class Layout {
    private int height;

    private int width;

    private Point leftUpperCorner;

    private CompositeLayout parent;

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/

    /** 
     * This constructor creates a new Layout with the given height, width and leftUpperCorner
     * @post getHeigth() == height
     * @post getWidth() == widthµ
     * @post getParent() == null
     * @post getLeftUpperCorner() == leftUpperCorner
     */
    public Layout(int height, int width, Point leftUpperCorner) {
        this.height = height;
        this.width = width;
        this.parent = null;
        this.leftUpperCorner = leftUpperCorner;
    }

    /** 
     * This constructor creates a new Layout with the given height, width, parent and leftUpperCorner
     * @post getHeigth() == height
     * @post getWidth() == width
     * @post getParent() == parent
     * @post getLeftUpperCorner() == leftUpperCorner
     */ /*
    public Layout(int height, int width, CompositeLayout parent, Point leftUpperCorner) {
        this.height = height;
        this.width = width;
        this.parent = parent;
        this.leftUpperCorner = leftUpperCorner;
    }
    */
    
    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    /** 
     * This method sets the height of the layout to the given parameter newHeight
     * @param newHeight this is the new height of the layout
     * @post getHeigth() == newHeight
     * @return: void
     */
    public void setHeigth(int newHeight) {
        this.height = newHeight;
    }

    /** 
     * This method returns the height of the layout
     * @return: int, the height of the layout
     */
    public int getHeigth() {
        return height;
    }

    /** 
     * This method sets the width of the layout to the given parameter newWidth
     * @post getWidth() == newWidth
     * @return: void
     */
    public void setWidth(int newWidth) {
        this.width = newWidth;
    }

    /** 
     * This method returns the width of the layout
     * @return: int, the width of the layout
     */
    public int getWidth() {
        return width;
    }

    /** 
     * This method sets the parent of the layout to the given parameter newParent
     * @post getParent() == newParent
     * @return: void
     */
    public void setParent(CompositeLayout newParent) {
        this.parent = newParent;
    }

    /** 
     * This method returns the parent of the layout
     * @return: CompositeLayout, the parent of the layout
     */
    public CompositeLayout getParent() {
        return parent;
    }

    /** 
     * This method sets the leftUpperCorner of the layout to the given parameter newLeftUpperCorner
     * @post getLeftUpperCorner() == newLeftUpperCorner
     * @return: void
     */
    public void setLeftUpperCorner(Point newLeftUpperCorner) {
        this.leftUpperCorner = newLeftUpperCorner;
    }

    /** 
     * This method returns the leftUpperCorner of the layout
     * @return: Point, the leftUpperCorner of the layout
     */
    public Point getLeftUpperCorner() {
        return leftUpperCorner;
    }

    public Layout[] getSubLayouts() {
        return new Layout[] {this};
    }

    public int countSubLayouts() {
        return 1;
    }

    /* ******************
     *  INSPECT CONTENT *
     * ******************/

    /** 
     * This method moves the insertion point of the focused view in the given direction if the focussed view is a fileBufferView
     * or moves the snake in the given direction if the focussed view is a gameView
     * @param dir this is the direction in which the insertion point or the snake should move
     * @param focus this is the index of the focussed view
     * @return: void
     */	
    public void arrowPressed(Direction dir, int focus) {
        View focussed = getFocusedView(focus);
        focussed.move(dir);
    }

    /**
     * This method returns the next focussed view
     * @param focus this is the index of the focussed view
     * @return: int, the index of the next focussed view
     */
    public int getNextFocus(int focus) {
        if (focus == countViews()) {
            return 1;
        }
        return focus + 1;
    }

    /**
     * This method returns the previous focussed view
     * @param focus this is the index of the focussed view
     * @return: int, the index of the previous focussed view
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
     * This method adds a new line break at the insertion point of the focused view
     * @param focus this is the index of the focussed view
     * @return: void
     */
    public void addNewLineBreak(int focus) {
        View focussed = getFocusedView(focus);
        if (focussed.addNewLineBreak()) {
            FileBufferView f = (FileBufferView) focussed;
            FileBufferView.NonEmptyEdit edit = (FileBufferView.NonEmptyEdit) f.getLastEdit();
            updateViews(focus, edit.getInsertionPoint(), (char) 13, false, f.getBuffer());
        }
    }

    /** 
     * This method adds a new character at the insertion point of the focused view
     * @param c this is the character that should be added
     * @param focus this is the index of the focussed view
     * @return: void
     */
    public void addNewChar(char c, int focus) {
        View focussed = getFocusedView(focus);
        if (focussed.addNewChar(c)) {
            FileBufferView f = (FileBufferView) focussed;
            FileBufferView.NonEmptyEdit edit = (FileBufferView.NonEmptyEdit) f.getLastEdit();
            updateViews(focus, edit.getInsertionPoint(), c, false, f.getBuffer());
        }
    }

    /** 
     * This method deletes the character at the insertion point of the focused view
     * @param focus this is the index of the focussed view
     * @return: void
     */
    public void deleteChar(int focus) {
        View focussed = getFocusedView(focus);
        if (focussed.deleteChar()) {
            FileBufferView f = (FileBufferView) focussed;
            FileBufferView.NonEmptyEdit edit = (FileBufferView.NonEmptyEdit) f.getLastEdit();
            updateViews(focus, edit.getInsertionPoint(), edit.getChange(), true, f.getBuffer());
        }
    }

    /* ******************
     *   CLOSE VIEW     *
     * ******************/

     /** 
     * This method closes the focused view and updates the subLayouts
     * @param focus this is the index of the focussed view
     * @return: Layout, the new layout after closing the focused view
     */
    public Layout closeView(int focus) {
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
     * @param focus this is the index of the focussed view
     * @return: int, the new index of the focussed view
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
    public abstract Layout closeView(int focus, CompositeLayout parent);

    /* ******************
     *    SAVE BUFFER   *
     * ******************/

    /** 
     * This method saves the buffer of the focused view
     * @param focus this is the index of the focussed view
     * @param newLine this is the new line that should be added to the buffer
     * @return: void
     */
    public void saveBuffer(int focus, String newLine) throws IOException {
        View focussed = getFocusedView(focus);
        focussed.saveBuffer(newLine);
    }

    /* *****************
     *    ROTATE VIEW  *
     * *****************/

    /**
     * This method directs how it should be rotated based on the focus and the direction
     * @param dir the direction in which the view should be rotated
     * @param focus the index of the focused view
     * @return: Layout, the new layout after rotating the view
     */
    protected abstract Layout rotateView(int dir, int focus);

    /**
     * 
     * @param dir the direction in which the view should be rotated
     * @param focus the index of the focused view
     * @param nextFocus the index of the next focused view
     * @param parent the parent of the focused view
     * @return: Layout, the new layout after rotating the siblings
     */
    protected abstract Layout rotateSiblings(int dir, int focus, int nextFocus, CompositeLayout parent);

    /**
     * 
     * @param dir the direction in which the view should be rotated
     * @param focus the index of the focused view
     * @param nextFocus the index of the next focused view
     * @param parent the parent of the focused view
     * @return Layout, the new layout after rotating the siblings
     */
    protected abstract Layout rotateSiblingsFlip(int dir, int focus, int nextFocus, CompositeLayout parent);

    /**
     * 
     * @param dir the direction in which the view should be rotated
     * @param focus the index of the focused view
     * @param nextView the next focused view
     * @param parent1 the parent of the focused view
     * @param parent2 the parent of the next focused view
     * @return Layout, the new layout after rotating the non siblings
     */
    protected abstract Layout rotateNonSiblingsPromote(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2);

    /**
     * 
     * @param dir the direction in which the view should be rotated
     * @param focus the index of the focused view 
     * @param nextView the next focused view
     * @param parent1 the parent of the focused view
     * @param parent2 the parent of the next focused view
     * @return Layout, the new layout after rotating the non siblings
     */
    protected abstract Layout rotateNonSiblings(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2);

    protected abstract Layout flip();

    /* ******************
     *   UNDO / REDO    *
     * ******************/

    /**
     * This method checks if the last edit of the focused view can be undone and if so, undoes it and updates the views
     * @param focus this is the index of the focussed view
     * @return: void
     */
    public void undo(int focus) {
        View focussed = getFocusedView(focus);
        if (focussed.undo()) {
            FileBufferView f = (FileBufferView) focussed;
            FileBufferView.NonEmptyEdit edit = (FileBufferView.NonEmptyEdit) f.getLastEdit().getNext();
            updateViews(focus, edit.getInsertionPointAfter(), edit.getChange(), edit.getClass() != FileBufferView.Deletion.class, f.getBuffer());
        }
    }

  /**
     * This method checks if the last edit of the focused view can be redone and if so, redoes it and updates the views
     * @param focus this is the index of the focussed view
     * @return: void
     */
    public void redo(int focus) {
        View focussed =getFocusedView(focus);
        if (focussed.redo()) {
            FileBufferView f = (FileBufferView) focussed;
            FileBufferView.NonEmptyEdit edit = (FileBufferView.NonEmptyEdit) f.getLastEdit();
            updateViews(focus, edit.getInsertionPoint(), edit.getChange(), edit.getClass() == FileBufferView.Insertion.class, f.getBuffer());
        }
    }

    /* *************
     *  OPEN VIEW  *
     * *************/

    /** 
     * This method opens the given views in the layout, updates the size of the layout and initializes the view positions
     * @param focus this is the index of the focussed view
     * @param parent this is the parent of the focussed view
     * @param views this is the array of views that should be opened
     * @return: Layout, the new layout after opening the views
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
     * @param focus this is the index of the focussed view
     * @return: Layout, the new layout after opening the gameView
     */
      public Layout newGame(int focus) {
        View focussed = getFocusedView(focus);
        return openViews(focus, focussed.getParent(),
                new View[] {new GameView(focussed.getHeigth(), focussed.getWidth() / 2, focussed.getLeftUpperCorner().add(new Point(0, focussed.getWidth() / 2)))});
    }


    /* *******************
     *  DUPLICATED VIEW  *
     * *******************/
  
    /** 
     * This method inserts the given views in the layout
     * @param focus this is the index of the focussed view
     * @param parent this is the parent of the focussed view
     * @param views this is the array of views that should be inserted
     * @return: Layout, the new layout after inserting the views
     */
    public abstract Layout insertViews(int focus, CompositeLayout parent, View[] views);

    /** 
     * This method opens a new game view in the layout
     * @param focus this is the index of the focussed view
     * @return: Layout, the new layout after opening the gameView
     */
    public Layout newGame(int focus) {
        View focussed = getFocusedView(focus);
        return openViews(focus, focussed.getParent(),
                new View[] {new GameView(focussed.getHeigth(), focussed.getWidth() / 2, focussed.getLeftUpperCorner().add(new Point(0, focussed.getWidth() / 2)))});
    }

    /* *******************
     *  DUPLICATED VIEW  *
     * *******************/
  
    /**
     * This method duplicates the focused view
     * @param focus this is the index of the focussed view
     * @return Layout, the new layout after duplicating the view
     */
    public Layout newBufferView(int focus) {
        View focussed = getFocusedView(focus);
        View[] duplicates = focussed.duplicate();
        return openViews(focus, focussed.getParent(), duplicates);
    }

    /**
     * This method updates the views of the given buffer
     * @param focus this is the index of the focussed view
     * @param insert this is the insertion point of the focused view
     * @param c this is the character that should be added
     * @param isDeleted this is a boolean that indicates if the character should be deleted
     * @param buffer this is the buffer of the focused view
     * @return void
     */
    public abstract void updateViews(int focus, Point insert, char c, boolean isDeleted, FileBuffer buffer);

    /* ****************
     *    RUN SNAKE   *
     * ****************/

    /**
     * This method runs the snake in the focused view
     * @param focus this is the index of the focussed view
     * @return void
     */
    public void tick(int focus) throws IOException {
        getFocusedView(focus).tick();
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    /** 
     * This method shows the layout of the subLayouts
     * @return: void
     */
    public abstract void show();

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/
    
     /** 
     * This method returns the viewposition at the given index i and updates the viewpositions of the subLayouts
     * @return: int, the viewposition at the given index i
     */
    public abstract void initViewPosition(int i);

    /** 
     * This method returns the focused view at the given index i
     * @param i this is the index of the view that should be returned
     * @return: FileBufferView
     */	
    public abstract View getFocusedView(int i);

    /** 
     * This method returns the number of views
     * @return: int, the number of views
     */
    public abstract int countViews();

    /**
     *  This method returns the number of subLayouts
     * @param focus this is the index of the focussed view
     * @return: long, the NextDeadline of the focused view
     */
    public long getNextDeadline(int focus) {
         return getFocusedView(focus).getNextDeadline();
    }

    /** 
     * This method updates the size of the layout to the given parameters heigth, width and leftUpperCorner
     * @param heigth this is the new height of the layout
     * @return: void
     */
    public abstract void updateSize(int heigth, int width, Point leftUpperCorner);

    /**
     * Checks if the structure of the Layouts match
     */
    @Override
    public boolean equals(Object o) {
        if (o.getClass() != this.getClass()) {
            return false;
        }
        if (o instanceof CompositeLayout) {
            Layout[] subLayouts = ((CompositeLayout) this).getSubLayouts();
            Layout[] otherSubLayouts = ((CompositeLayout) o).getSubLayouts();
            return Arrays.equals(subLayouts, otherSubLayouts);
        }
        return true;
    }
}

