import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class CompositeLayout extends Layout {
    private Layout[] subLayouts;

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/

    /**
     * This constructor creates a new CompositeLayout with the given height, width, leftUpperCorner and subLayouts
     * @param height the height of the layout
     * @param width the width of the layout
     * @param leftUpperCorner the leftUpperCorner of the layout
     * @param subLayouts the subLayouts of the layout
     * @post getSubLayouts() == subLayouts
     */
    public CompositeLayout(int height, int width, Point leftUpperCorner, Layout[] subLayouts) {
        super(height, width, leftUpperCorner);
        for (int i = 0; i < subLayouts.length; i++) {
            subLayouts[i].setParent(this);
        }
        this.subLayouts = subLayouts;
    }

    /**
     * This constructor creates a new CompositeLayout with the given height, width, leftUpperCorner, filepaths and newLine
     * @param height the height of the layout
     * @param width the width of the layout
     * @param leftUpperCorner the leftUpperCorner of the layout
     * @param filepaths the filepaths that are loaded in the layout
     * @post getSubLayouts().length == filepaths.length
     */
    public CompositeLayout(int height, int width, Point leftUpperCorner, String[] filepaths, String newLine) throws FileNotFoundException {
        super(height, width, leftUpperCorner);
        int length = filepaths.length;
        this.subLayouts = new Layout[length];
        Point subSize = calcSubSize();
        for (int i = 0; i < length; i++) {
            Point leftUpCorner = calcLeftUpCorner(i);
            getSubLayouts()[i] = new FileBufferView(subSize.getX(), subSize.getY(), leftUpCorner, filepaths[i], newLine);
            getSubLayouts()[i].setParent(this);
        }
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    /**
     * This method sets the subLayouts to the given parameter newSubLayouts
     * @param newSubLayouts the new subLayouts of the layout
     * @post getSubLayouts() == newSubLayouts
     * @return: void
     */
    public void setSubLayouts(Layout[] newSubLayouts) {
        this.subLayouts = newSubLayouts;
        for (int i = 0; i < countSubLayouts(); i++) {
            getSubLayouts()[i].setParent(this);
        }
    }

    /**
     * This method returns the subLayouts of the layout
     * @return: Layout[], the subLayouts of the layout
     */
    public Layout[] getSubLayouts() {
        return subLayouts;
    }

    /* **********************
     *  DERIVED ATTRIBUTES  *
     * **********************/

    /**
     * This method returns the number of subLayouts
     * @return: int, the number of subLayouts
     */
    @Override
    public int countSubLayouts() {
        return getSubLayouts().length;
    }

    /* ******************
     *   CLOSE VIEW     *
     * ******************/

    /**
     * This method closes the buffer at the given focus and returns the new focused layout and updates the subLayouts
     * @param focus the focussed view
     * @param parent the parent of the view
     * @post getSubLayouts().length == countSubLayouts() - 1
     * @return: Layout, the new layout after closing the current focused view
     */
    @Override
    public Layout closeView(int focus, CompositeLayout parent) throws IOException {
        if (this == parent) {
            if (getSubLayouts().length == 2) {
                if (getSubLayouts()[0].closeView(focus, parent) != null) {
                    if (getSubLayouts()[1].closeView(focus, parent) == null) {
                        return getSubLayouts()[0].closeView(focus, parent);
                    }
                    else {
                        return this;
                    }
                } else return getSubLayouts()[1].closeView(focus, parent);
            } else {
                Layout[] newSubLayouts = new Layout[countSubLayouts() - 1];
                int i = 0;
                for (int j = 0; j < getSubLayouts().length; j++) {
                    if (getSubLayouts()[j].closeView(focus, parent) != null) {
                        newSubLayouts[i] = getSubLayouts()[j].closeView(focus, parent);
                        i++;
                    }
                }
                setSubLayouts(newSubLayouts);
                return this;
            }
        } else {
            Layout[] newSubLayouts = getSubLayouts();
            for (int i = 0; i < newSubLayouts.length; i++) {
                newSubLayouts[i] = getSubLayouts()[i].closeView(focus, parent);
            }
            setSubLayouts(newSubLayouts);
            return this;
        }
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
    @Override
    protected Layout rotateView(int dir, int focus) {
        int heigth = getHeigth();
        int width = getWidth();
        Layout result;
        View focusView = getFocusedView(focus);
        View nextView = getFocusedView(getNextFocus(focus));
        if (focusView.getParent() == nextView.getParent()) {
            if (focusView.getParent().countSubLayouts() == 2) {
                result = rotateSiblingsFlip(dir, focus, getNextFocus(focus), focusView.getParent());
            } else {
                result = rotateSiblings(dir, focus, getNextFocus(focus), focusView.getParent());
            }
        } else {
            if (nextView.getParent().countSubLayouts() == 2) {
                result = rotateNonSiblingsPromote(dir, focus, nextView, focusView.getParent(), nextView.getParent());
            } else {
                result = rotateNonSiblings(dir, focus, nextView, focusView.getParent(), nextView.getParent());
            }
        }
        result.updateSize(heigth, width, new Point(1, 1));
        return result;
    }

    /** 
     * This method rotates the view and updates the subLayouts
     * @param dir direction of the rotation
     * @param focus the index of the focused view
     * @param nextFocus the index of the next focused view
     * @param parent the parent of the view
     * @return: SideBySideLayout
     */
    protected abstract CompositeLayout rotateSiblings(int dir, int focus, int nextFocus, CompositeLayout parent);

    /** 
     * This method rotates the view and updates the subLayouts
     * @param dir direction of the rotation
     * @param focus the index of the focused view
     * @param nextFocus the index of the next focused view
     * @param parent the parent of the view
     * @return: CompositeLayout
     */
    protected abstract CompositeLayout rotateSiblingsFlip(int dir, int focus, int nextFocus, CompositeLayout parent);

    /** 
     * This method rotates the view and updates the subLayouts
     * @param dir direction of the rotation
     * @param focus the index of the focused view
     * @param nextView the next view
     * @param parent1 the parent of the view
     * @param parent2 the parent of the view
     * @return: CompositeLayout
     */
    protected abstract CompositeLayout rotateNonSiblings(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2);

     /** 
     * This method rotates the view and updates the subLayouts
     * @param dir direction of the rotation
     * @param focus the index of the focused view
     * @param nextView the next view
     * @param parent1 the parent of the view
     * @param parent2 the parent of the view
     * @return: Layout
     */
    protected abstract Layout rotateNonSiblingsPromote(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2);

    
    /*  public Layout prune() {
        if (getSubLayouts().length == 1) {
            if (this.getParent() == null) {
                getSubLayouts()[0].setParent(null);
                return getSubLayouts()[0];
            } else {
                setSubLayouts(getSubLayouts()[0].getParent().getSubLayouts());
            }
        } else {
            for (int i = 0; i < getSubLayouts().length; i++) {
                if (getSubLayouts()[i] instanceof CompositeLayout) {
                    CompositeLayout subLay = (CompositeLayout) getSubLayouts()[i];
                    setSubLayout(subLay.prune(), i);
                }
            }
        }
        return this;
    } */

    /* ******************
     *  OPEN GAME VIEW  *
     * ******************/

    /* ************************
     *  OPEN FILEBUFFER VIEW  *
     * ************************/

    /**
     * This method updates the views of the given buffer
     * @param focus this is the index of the focussed view
     * @param insert this is the insertion point of the focused view
     * @param c this is the character that should be added
     * @param isDeleted this is a boolean that indicates if the character should be deleted
     * @param buffer this is the buffer of the focused view
     * @return void
     */
    @Override
    public void updateViews(int focus, Point insert, char c, boolean isDeleted, FileBuffer buffer) {
        for (int i = 0; i < countSubLayouts(); i++) {
            getSubLayouts()[i].updateViews(focus, insert, c, isDeleted, buffer);
        }
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    /**
     * This method shows the layout of the subLayouts
     * @return: void
     */
    public void show() {
        Layout[] subLays = getSubLayouts();
        for (int i = 0; i < countSubLayouts(); i++) {
            subLays[i].show();
        }
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    /** 
     * This method returns the size of the subLayouts
     * @return: Point, gives the size of the subLayouts with first value the height and second value the width of the subLayouts
     */
    public abstract Point calcSubSize();

    /**
     * This method returns the leftUpperCorner of the subLayouts
     * @return: Point, the leftUpperCorner of the subLayouts
     */
    public abstract Point calcLeftUpCorner(int i);

    /**
     * This method initializes the position at the given index i
     * @return: void
     */
    @Override
    public void initViewPosition(int i) {
        Layout[] subLayouts = getSubLayouts();
        int i1 = i;
        for (int j = 0; j < countSubLayouts(); j++) {
            subLayouts[j].initViewPosition(i1);
            i1 += subLayouts[j].countViews();
        }
    }

    /**
     * This method returns the focused view at the given index i
     * @param i the index of the focused view
     * @return: View, the focused view at the given index i
     */
    @Override
    public View getFocusedView(int i) {
        Layout[] subLayout = getSubLayouts();
        View res = null;
        for (Layout layout : subLayout) {
            if (res == null) {
                res = layout.getFocusedView(i);
            }
        }
        return res;
    }

    /**
     * This method returns a boolean that indicates if the sublayouts contain the given layout
     * @return: boolean, true if the sublayouts contain the given layout, false otherwise
     */
    public boolean contains(Layout layout) {
        for (int i = 0; i < countSubLayouts(); i++) {
            if (getSubLayouts()[i] == layout) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method returns the number of views
     * @return: int, the number of views
     */
    @Override
    public int countViews() {
        int result = 0;
        Layout[] subLays = getSubLayouts();
        for (int i = 0; i < subLays.length; i++) {
            result += subLays[i].countViews();
        }
        return result;
    }

    /**
     * This method updates the size of the layout to the given parameters heigth, width and leftUpperCorner
     * @param heigth the new heigth of the layout
     * @param width the new width of the layout
     * @param leftUpperCorner the new leftUpperCorner of the layout
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
        Point subSize = calcSubSize();
        for (int i = 0; i < countSubLayouts(); i++) {
            Point subLeftUp = calcLeftUpCorner(i);
            getSubLayouts()[i].updateSize(subSize.getX(), subSize.getY(), subLeftUp);
        }
    }

}
