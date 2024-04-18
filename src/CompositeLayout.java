import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public abstract class CompositeLayout extends Layout {
    private Layout[] subLayouts;

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/

    /**
     * This constructor creates a new CompositeLayout with the given height, width, leftUpperCorner and subLayouts
     *
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
     *
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
     *
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
     *
     * @return: Layout[]
     */
    public Layout[] getSubLayouts() {
        return subLayouts;
    }

    /* **********************
     *  DERIVED ATTRIBUTES  *
     * **********************/

    /**
     * This method returns the number of subLayouts
     *
     * @return: int
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
     *
     * @post getSubLayouts().length == countSubLayouts() - 1
     * @return: Layout
     */
    @Override
    public Layout closeView(int focus, CompositeLayout parent){
        if (this == parent) {
            if (getSubLayouts().length == 2) {
                if (getSubLayouts()[0].closeView(focus, parent) != null) {
                    return getSubLayouts()[0].closeView(focus, parent);
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

    protected abstract CompositeLayout rotateSiblings(int dir, int focus, int nextFocus, CompositeLayout parent);

    protected abstract CompositeLayout rotateSiblingsFlip(int dir, int focus, int nextFocus, CompositeLayout parent);

    protected abstract Layout rotateNonSiblingsPromote(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2);

    protected abstract CompositeLayout rotateNonSiblings(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2);

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
     *
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
     *
     * @return: Point
     */
    public abstract Point calcSubSize();

    /**
     * This method returns the leftUpperCorner of the subLayouts
     *
     * @return: Point
     */
    public abstract Point calcLeftUpCorner(int i);

    /**
     * This method returns the viewposition at the given index i and updates the viewpositions of the subLayouts
     *
     * @return: int
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
     *
     * @return: FileBufferView
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
     *
     * @return: int
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
     *
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
