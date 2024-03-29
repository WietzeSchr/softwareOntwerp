import static java.lang.Math.floor;

public class StackedLayout extends CompositeLayout {

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/

    /** This constructor creates a new StackedLayout with the given height, width, leftUpperCorner, filepaths and newLine
     */
    public StackedLayout(int height, int width, Point leftUpperCorner, String[] filepaths, String newLine) {
        super(height, width, leftUpperCorner, filepaths, newLine);
    }

    /** This constructor creates a new StackedLayout with the given height, width, leftUpperCorner and subLayouts
     */
    public StackedLayout(int heigth, int width, Point leftUpperCorner, Layout[] subLayouts) {
        super(heigth, width, leftUpperCorner, subLayouts);
    }

    /* *****************
     *    ROTATE VIEW  *
     * *****************/

    /** This method rotates the view and updates the subLayouts
     * @return: CompositeLayout || null
     */
    @Override
    protected StackedLayout rotateSiblings(int dir, int focus, int nextFocus, CompositeLayout parent) {
        if (this == parent) {
            Layout[] newSubLayouts = new Layout[countSubLayouts() - 1];
            int j = 0;
            FileBufferView focusView = getFocusedView(focus);
            FileBufferView nextView = getFocusedView(nextFocus);
            for (int i= 0; i < countSubLayouts(); i++) {
                if (getSubLayouts()[i] != nextView) {
                    if (getSubLayouts()[i] == focusView) {
                        if (dir == 1) {
                            Layout[] subSubLayouts = new Layout[] {focusView, nextView};
                            newSubLayouts[j] = new SideBySideLayout(getHeigth(),getWidth(),getLeftUpperCorner(),subSubLayouts);
                        }
                        else {
                            Layout[] subSubLayouts = new Layout[] {nextView, focusView};
                            newSubLayouts[j] = new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), subSubLayouts);
                        }
                        j += 1;
                    }
                    else {
                        newSubLayouts[j] = getSubLayouts()[i];
                        j += 1;
                    }
                }
            }
            return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
        else {
            Layout[] newSubLayouts = new Layout[countSubLayouts()];
            for (int i = 0; i < countSubLayouts(); i++) {
                newSubLayouts[i] = getSubLayouts()[i].rotateSiblings(dir, focus, nextFocus, parent);
            }
            return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
    }

    /** This method rotates the view and updates the subLayouts
     * @return: CompositeLayout || null
     */
    @Override
    protected CompositeLayout rotateSiblingsFlip(int dir, int focus, int nextFocus, CompositeLayout parent) {
        if (this == parent) {
            if (dir == 1) {
                return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), getSubLayouts());
            }
            else {
                return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(),
                        new Layout[] {getSubLayouts()[1], getSubLayouts()[0]});
            }
        }
        else if (contains(parent)) {
            Layout[] newSubLayouts = new Layout[countSubLayouts() + 1];
            int j = 0;
            for (int i = 0; i < countSubLayouts(); i++) {
                if (getSubLayouts()[i] == parent) {
                    if (dir == -1) {
                        newSubLayouts[j] = parent.getFocusedView(focus);
                        newSubLayouts[j + 1] = parent.getFocusedView(nextFocus);
                    }
                    else {
                        newSubLayouts[j] = parent.getFocusedView(nextFocus);
                        newSubLayouts[j + 1] = parent.getFocusedView(focus);
                    }
                    j += 2;
                }
                else {
                    newSubLayouts[j] = getSubLayouts()[i];
                    j += 1;
                }
            }
            return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
        else {
            Layout[] newSubLayouts = new Layout[countSubLayouts()];
            for (int i = 0; i < countSubLayouts(); i++) {
                newSubLayouts[i] = getSubLayouts()[i].rotateSiblingsFlip(dir, focus, nextFocus, parent);
            }
            return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
    }

    @Override
    protected CompositeLayout rotateNonSiblings(int dir, int focus, FileBufferView nextView, CompositeLayout parent1, CompositeLayout parent2) {
        if (this == parent1)
        {
            Layout[] newSubLayouts = new Layout[countSubLayouts() + 1];
            for (int i = 0; i < countSubLayouts(); i++) {
                newSubLayouts[i] = getSubLayouts()[i].rotateNonSiblingsPromote(dir, focus, nextView, parent1, parent2);
            }
            newSubLayouts[countSubLayouts()] = nextView;
            return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
        else if (this == parent2) {
            Layout[] newSubLayouts = new Layout[countSubLayouts() - 1];
            int j = 0;
            for (int i = 0; i < countSubLayouts(); i++) {
                if (getSubLayouts()[i] != nextView) {
                    newSubLayouts[j] = getSubLayouts()[i];
                    j++;
                }
            }
            return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
        else {
            Layout[] newSubLayouts = new Layout[countSubLayouts()];
            for (int i = 0; i < countSubLayouts(); i++) {
                newSubLayouts[i] = getSubLayouts()[i].rotateNonSiblings(dir, focus, nextView, parent1, parent2);
            }
            return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
    }

    @Override
    protected Layout rotateNonSiblingsPromote(int dir, int focus, FileBufferView nextView, CompositeLayout parent1, CompositeLayout parent2) {
        if (this == parent1)
        {
            Layout[] newSubLayouts = new Layout[countSubLayouts() + 1];
            for (int i = 0; i < countSubLayouts(); i++) {
                newSubLayouts[i] = getSubLayouts()[i].rotateNonSiblingsPromote(dir, focus, nextView, parent1, parent2);
            }
            newSubLayouts[countSubLayouts()] = nextView;
            return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
        else if (this == parent2) {
            Layout result;
            if (getSubLayouts()[0] == nextView) {
                result = getSubLayouts()[1].rotateNonSiblingsPromote(dir, focus, nextView, parent1, parent2);
            }
            else {
                result = getSubLayouts()[0].rotateNonSiblingsPromote(dir, focus, nextView, parent1, parent2);
            }
            result.setParent(getParent());
            return result;
        }
        else {
            Layout[] newSubLayouts = new Layout[countSubLayouts()];
            for (int i = 0; i < countSubLayouts(); i++) {
                newSubLayouts[i] = getSubLayouts()[i].rotateNonSiblingsPromote(dir, focus, nextView, parent1, parent2);
            }
            return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
    }


    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    /** This method returns the size of the subLayouts
     * @return: Point
     */
    @Override
    public Point calcSubSize() {
        return new Point((int) Math.floor((float) getHeigth() / (float) countSubLayouts()), getWidth());
    }

    /** This method returns the leftUpperCorner of the subLayouts
     * @return: Point
     */
    @Override
    public Point calcLeftUpCorner(int i) {
        int subHeight = (int) floor((float) getHeigth() / (float) countSubLayouts());
        return new Point(getLeftUpperCorner().getX() + i * subHeight, getLeftUpperCorner().getY());
    }
}
