import java.io.File;
import java.io.IOException;

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

    /** This constructor creates a new Layout with the given height, width and leftUpperCorner
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

    /** This constructor creates a new Layout with the given height, width, parent and leftUpperCorner
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

    public void setHeigth(int newHeight) {
        this.height = newHeight;
    }

    /** This method returns the height of the layout
     * @return: int
     */
    public int getHeigth() {
        return height;
    }

    /** This method sets the width of the layout to the given parameter newWidth
     * @post getWidth() == newWidth
     * @return: void
     */
    public void setWidth(int newWidth) {
        this.width = newWidth;
    }

    /** This method returns the width of the layout
     * @return: int
     */
    public int getWidth() {
        return width;
    }

    /** This method sets the parent of the layout to the given parameter newParent
     * @post getParent() == newParent
     * @return: void
     */
    public void setParent(CompositeLayout newParent) {
        this.parent = newParent;
    }

    /** This method returns the parent of the layout
     * @return: CompositeLayout
     */
    public CompositeLayout getParent() {
        return parent;
    }

    /** This method sets the leftUpperCorner of the layout to the given parameter newLeftUpperCorner
     * @post getLeftUpperCorner() == newLeftUpperCorner
     * @return: void
     */
    public void setLeftUpperCorner(Point newLeftUpperCorner) {
        this.leftUpperCorner = newLeftUpperCorner;
    }

    /** This method returns the leftUpperCorner of the layout
     * @return: Point
     */
    public Point getLeftUpperCorner() {
        return leftUpperCorner;
    }

    /* ******************
     *  INSPECT CONTENT *
     * ******************/

    public void arrowPressed(Direction dir, int focus) {
        View focussed = getFocusedView(focus);
        focussed.move(dir);
    }

    public int getNextFocus(int focus) {
        if (focus == countViews()) {
            return 1;
        }
        return focus + 1;
    }

    public int getPreviousFocus(int focus) {
        if (focus == 1) {
            return countViews();
        }
        return focus - 1;
    }

    /* **********************
     *  EDIT BUFFER CONTENT *
     ************************/

    public void addNewLineBreak(int focus) {
        View focussed = getFocusedView(focus);
        if (focussed.addNewLineBreak()) {
            FileBufferView f = (FileBufferView) focussed;
            FileBufferView.NonEmptyEdit edit = (FileBufferView.NonEmptyEdit) f.getLastEdit();
            updateViews(focus, edit.getInsertionPoint(), (char) 13, false, f.getBuffer());
        }
    }

    public void addNewChar(char c, int focus) {
        View focussed = getFocusedView(focus);
        if (focussed.addNewChar(c)) {
            FileBufferView f = (FileBufferView) focussed;
            FileBufferView.NonEmptyEdit edit = (FileBufferView.NonEmptyEdit) f.getLastEdit();
            updateViews(focus, edit.getInsertionPoint(), c, false, f.getBuffer());
        }
    }

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

    public int getNewFocus(int focus) {
        if (focus > countViews()) {
            return focus - 1;
        }
        return focus;
    }

    /** This method closes the buffer and updates the subLayouts
     * @return: Layout
     */
    public abstract Layout closeView(int focus, CompositeLayout parent) throws IOException;

    /* ******************
     *    SAVE BUFFER   *
     * ******************/

    public void saveBuffer(int focus, String newLine) throws IOException {
        View focussed = getFocusedView(focus);
        focussed.saveBuffer(newLine);
    }

    /* *****************
     *    ROTATE VIEW  *
     * *****************/

    protected abstract Layout rotateView(int dir, int focus);

    protected abstract Layout rotateSiblings(int dir, int focus, int nextFocus, CompositeLayout parent);

    protected abstract Layout rotateSiblingsFlip(int dir, int focus, int nextFocus, CompositeLayout parent);

    protected abstract Layout rotateNonSiblingsPromote(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2);

    protected abstract Layout rotateNonSiblings(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2);

    /* ******************
     *   UNDO / REDO    *
     * ******************/

    public void undo(int focus) {
        View focussed = getFocusedView(focus);
        if (focussed.undo()) {
            FileBufferView f = (FileBufferView) focussed;
            FileBufferView.NonEmptyEdit edit = (FileBufferView.NonEmptyEdit) f.getLastEdit().getNext();
            updateViews(focus, edit.getInsertionPointAfter(), edit.getChange(), edit.getClass() != FileBufferView.Deletion.class, f.getBuffer());
        }
    }

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

    public Layout openViews(int focus, CompositeLayout parent, View[] views) {
        if (views.length == 0) return this;
        Layout result = insertViews(focus, parent, views);
        result.updateSize(getHeigth(), getWidth(), new Point(1, 1));
        result.initViewPosition(1);
        return result;
    }

    public abstract Layout insertViews(int focus, CompositeLayout parent, View[] views);

    public Layout newGame(int focus) {
        View focussed = getFocusedView(focus);
        return openViews(focus, focussed.getParent(),
                new View[] {new GameView(getHeigth(), getWidth() / 2, getLeftUpperCorner().add(new Point(0, getWidth() / 2)))});
    }

    /* *******************
     *  DUPLICATED VIEW  *
     * *******************/

    public Layout newBufferView(int focus) {
        View focussed = getFocusedView(focus);
        View[] duplicates = focussed.duplicate();
        return openViews(focus, focussed.getParent(), duplicates);
    }

    public abstract void updateViews(int focus, Point insert, char c, boolean isDeleted, FileBuffer buffer);

    /* ****************
     *    RUN SNAKE   *
     * ****************/

    public void tick(int focus) throws IOException {
        getFocusedView(focus).tick();
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    /** This method shows the layout of the subLayouts
     * @return: void
     */
    public abstract void show();

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/
    
     /** This method returns the viewposition at the given index i and updates the viewpositions of the subLayouts
     * @return: int
     */
    public abstract void initViewPosition(int i);

    /** This method returns the focused view at the given index i
     * @return: FileBufferView
     */	
    public abstract View getFocusedView(int i);

    /** This method returns the number of views
     * @return: int
     */
    public abstract int countViews();

    public long getNextDeadline(int focus) {
         return getFocusedView(focus).getNextDeadline();
    }

    /** This method updates the size of the layout to the given parameters heigth, width and leftUpperCorner
     * @return: void
     */
    public abstract void updateSize(int heigth, int width, Point leftUpperCorner);

}

