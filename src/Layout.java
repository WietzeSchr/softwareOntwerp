import java.awt.*;
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
     */
    public Layout(int height, int width, CompositeLayout parent, Point leftUpperCorner) {
        this.height = height;
        this.width = width;
        this.parent = parent;
        this.leftUpperCorner = leftUpperCorner;
    }

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
     *   CLOSE BUFFER   *
     * ******************/

    /** This method closes the buffer and updates the subLayouts
     * @return: Layout
     */
    public abstract Layout closeBuffer(int focus, CompositeLayout parent) throws IOException;

    /* *****************
     *    ROTATE VIEW  *
     * *****************/

    protected abstract Layout rotateView(int dir, int focus);

    protected abstract Layout rotateSiblings(int dir, int focus, int nextFocus, CompositeLayout parent);

    protected abstract Layout rotateSiblingsFlip(int dir, int focus, int nextFocus, CompositeLayout parent);

    protected abstract Layout rotateNonSiblingsPromote(int dir, int focus, FileBufferView nextView, CompositeLayout parent1, CompositeLayout parent2);

    protected abstract Layout rotateNonSiblings(int dir, int focus, FileBufferView nextView, CompositeLayout parent1, CompositeLayout parent2);

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

    public abstract int getNextFocus(int focus);

    public abstract int getPreviousFocus(int focus);
    
     /** This method returns the viewposition at the given index i and updates the viewpositions of the subLayouts
     * @return: int
     */
    public abstract void initViewPosition(int i);

    /** This method returns the focused view at the given index i
     * @return: FileBufferView
     */	
    public abstract FileBufferView getFocusedView(int i);

    /** This method returns the number of views
     * @return: int
     */
    public abstract int countViews();

    /** This method updates the size of the layout to the given parameters heigth, width and leftUpperCorner
     * @return: void
     */
    public abstract void updateSize(int heigth, int width, Point leftUpperCorner);

    /** This method sets the height of the layout to the given parameter newHeight
     * @post getHeigth() == newHeight
     * @return: void
     */

    public abstract int getNewFocus(int focus);
}

