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
     * Checks the layout to call the appropriate method for rotating the focused view with the next focused view
     * If the layout is only one view, nothing happens
     * @pre | dir == 1 || dir == -1
     * @param dir   | 1: counterclockwise, -1: clockwise
     * @param focus | The index of the focused view
     * @return      |Layout, the new layout after rotating the view
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

    /* ************************
     *  OPEN FILEBUFFER VIEW  *
     * ************************/

    /**
     * This method updates the views of the given buffer
     * @param focus     | The index of the focussed view
     * @param insert    | The insertion point of the focused view
     * @param c         | The character that was deleted or added
     * @param isDeleted | true: character was deleted, false: character was inserted
     * @param buffer    | The buffer of the focused view
     * @return  | void
     */
    @Override
    public void updateViews(int focus, Point insert, char c, boolean isDeleted, FileBuffer buffer) {
        for (int i = 0; i < countSubLayouts(); i++) {
            getSubLayouts()[i].updateViews(focus, insert, c, isDeleted, buffer);
        }
    }

    @Override
    public void updateViewsSaved(int focus, FileBuffer buffer) {
        for (int i = 0; i < countSubLayouts(); i++) {
            getSubLayouts()[i].updateViewsSaved(focus, buffer);
        }
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    /**
     * This method shows the layout of the subLayouts
     * @return  | void
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
     * @return  | Point, Point.getX() = heigth subLayouts Point.getY() = width subLayouts
     */
    public abstract Point calcSubSize();

    /**
     * This method returns the leftUpperCorner of the subLayout i
     * @return  | Point, the leftUpperCorner of the subLayout i
     */
    public abstract Point calcLeftUpCorner(int i);

    /**
     * This method initializes the view positions, assigning all views a position from i to i + countViews() - 1
     * layout. The views are ordered depth first
     * @return  | void
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
     * This method returns the view with position i
     * @param i  | The index of the view that should be returned
     * @return   | View, returns null when this doesn't contain the view
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
     * @return  | boolean, true if the sublayouts contain the given layout, false otherwise
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
     * @return  | int, the number of views
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
