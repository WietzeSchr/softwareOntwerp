import java.io.FileNotFoundException;
import java.util.Arrays;

import static java.lang.Math.floor;

public class SideBySideLayout extends CompositeLayout{

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/

    /** 
     * This constructor creates a new SideBySideLayout with the given height, width, leftUpperCorner, filepaths and newLine
     * @param height the height of the SideBySideLayout
     * @param width the width of the SideBySideLayout
     * @param leftUpperCorner the leftUpperCorner of the SideBySideLayout
     * @param filepaths the filepaths of the SideBySideLayout
     * @param newLine the newLine separator of the SideBySideLayout 
     */
    public SideBySideLayout(int height, int width, Point leftUpperCorner, String[] filepaths, String newLine) throws FileNotFoundException {
        super(height, width, leftUpperCorner, filepaths, newLine);
    }
        //  Deze kunnen we bij houden voor initialisatie met SideBySide

    /** 
     * This constructor creates a new SideBySideLayout with the given height, width, leftUpperCorner and subLayouts
     * @param height the height of the SideBySideLayout
     * @param width the width of the SideBySideLayout
     * @param leftUpperCorner the leftUpperCorner of the SideBySideLayout
     * @param subLayouts the subLayouts of the SideBySideLayout
     */
    public SideBySideLayout(int height, int width, Point leftUpperCorner, Layout[] subLayouts) {
        super(height, width, leftUpperCorner, subLayouts);
    }

    /* *****************
     *    ROTATE VIEW  *
     * *****************/

    /** 
     * This method rotates the view and updates the subLayouts
     * @param dir direction of the rotation
     * @param focus the index of the focused view
     * @param nextFocus the index of the next focused view
     * @param parent the parent of the view
     * @return: SideBySideLayout
     */
    @Override
    protected SideBySideLayout rotateSiblings(int dir, int focus, int nextFocus, CompositeLayout parent) {
        if (this == parent) {
            Layout[] newSubLayouts = new Layout[countSubLayouts() - 1];
            int j = 0;
            View focusView = getFocusedView(focus);
            View nextView = getFocusedView(nextFocus);
            for (int i= 0; i < countSubLayouts(); i++) {
                if (getSubLayouts()[i] != nextView) {
                    if (getSubLayouts()[i] == focusView) {
                        if (dir == 1) {
                            Layout[] subSubLayouts = new Layout[] {focusView, nextView};
                            newSubLayouts[j] = new StackedLayout(getHeigth(),getWidth(),getLeftUpperCorner(),subSubLayouts);
                        }
                        else {
                            Layout[] subSubLayouts = new Layout[] {nextView, focusView};
                            newSubLayouts[j] = new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), subSubLayouts);
                        }
                        j += 1;
                    }
                    else {
                        newSubLayouts[j] = getSubLayouts()[i];
                        j += 1;
                    }
                }
            }
            return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
        else {
            Layout[] newSubLayouts = new Layout[countSubLayouts()];
            for (int i = 0; i < countSubLayouts(); i++) {
                newSubLayouts[i] = getSubLayouts()[i].rotateSiblings(dir, focus, nextFocus, parent);
            }
            return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
    }

     /** 
     * This method rotates the view and updates the subLayouts
     * @param dir direction of the rotation
     * @param focus the index of the focused view
     * @param nextFocus the index of the next focused view
     * @param parent the parent of the view
     * @return: CompositeLayout
     */
    @Override
    protected CompositeLayout rotateSiblingsFlip(int dir, int focus, int nextFocus, CompositeLayout parent) {
        if (this == parent) {
            if (dir == -1) {
                return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), getSubLayouts());
            }
            else {
                return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(),
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
                    } else {
                        newSubLayouts[j] = parent.getFocusedView(nextFocus);
                        newSubLayouts[j + 1] = parent.getFocusedView(focus);
                    }
                    j += 2;
                } else {
                    newSubLayouts[j] = getSubLayouts()[i];
                    j += 1;
                }
            }
            return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
        else {
            Layout[] newSubLayouts = new Layout[countSubLayouts()];
            for (int i = 0; i < countSubLayouts(); i++) {
                newSubLayouts[i] = getSubLayouts()[i].rotateSiblingsFlip(dir, focus, nextFocus, parent);
            }
            return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
    }

    /** 
     * This method rotates the view and updates the subLayouts
     * @param dir direction of the rotation
     * @param focus the index of the focused view
     * @param nextView the next view
     * @param parent1 the parent of the view
     * @param parent2 the parent of the view
     * @return: CompositeLayout
     */
    @Override
    protected CompositeLayout rotateNonSiblings(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2) {
        if (this == parent1)
        {
            View focussed = getFocusedView(focus);
            Layout[] newSubLayouts = new Layout[countSubLayouts() + 1];
            int j = 0;
            for (int i = 0; i < countSubLayouts(); i++) {
                if (getSubLayouts()[i] == focussed) {
                    if (dir == 1) {
                        newSubLayouts[j] = nextView;
                        newSubLayouts[j + 1] = focussed;
                    } else {
                        newSubLayouts[j] = focussed;
                        newSubLayouts[j + 1] = nextView;
                    }
                    j += 2;
                }
                else {
                    newSubLayouts[j] = getSubLayouts()[i].rotateNonSiblings(dir, focus, nextView, parent1, parent2);
                    j += 1;
                }
            }
            return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
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
            return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
        else {
            Layout[] newSubLayouts = new Layout[countSubLayouts()];
            for (int i = 0; i < countSubLayouts(); i++) {
                newSubLayouts[i] = getSubLayouts()[i].rotateNonSiblings(dir, focus, nextView, parent1, parent2);
            }
            return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
    }

     /** 
     * This method rotates the view and updates the subLayouts
     * @param dir direction of the rotation
     * @param focus the index of the focused view
     * @param nextView the next view
     * @param parent1 the parent of the view
     * @param parent2 the parent of the view
     * @return: Layout
     */
    @Override
    protected Layout rotateNonSiblingsPromote(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2) {
        if (this == parent1)
        {
            Layout[] newSubLayouts;
            View focussed = getFocusedView(focus);
            if (getFocusedView(nextView.getPosition()) == null) {
                newSubLayouts = new Layout[countSubLayouts() + 1];
                int j = 0;
                for (int i = 0; i < countSubLayouts(); i++) {
                    if (getSubLayouts()[i] == focussed) {
                        if (dir == -1) {
                            newSubLayouts[j] = nextView;
                            newSubLayouts[j + 1] = focussed;
                        } else {
                            newSubLayouts[j] = focussed;
                            newSubLayouts[j + 1] = nextView;
                        }
                        j += 2;
                    } else {
                        newSubLayouts[j] = getSubLayouts()[i].rotateNonSiblingsPromote(dir, focus, nextView, parent1, parent2);
                        j += 1;
                    }
                }
            }
            else {
                newSubLayouts = new Layout[countSubLayouts() + parent2.countSubLayouts() - 1];
                int j = 0;
                for (int i = 0; i < countSubLayouts(); i++) {
                    if (getSubLayouts()[i] == focussed) {
                        if (dir == -1) {
                            newSubLayouts[j] = nextView;
                            newSubLayouts[j + 1] = focussed;
                        }
                        else {
                            newSubLayouts[j] = focussed;
                            newSubLayouts[j + 1] = nextView;
                        }
                        j += 2;
                        for (int k = 1; k < parent2.countSubLayouts(); k++) {
                            newSubLayouts[j] = parent2.getSubLayouts()[k].flip();
                            j += 1;
                        }
                    }
                    else {
                        if (getSubLayouts()[i] != parent2) {
                            newSubLayouts[j] = getSubLayouts()[i];
                        }
                    }
                }
            }
            return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
        else if (this == parent2) {
            Layout[] newSubLayouts;
            getSubLayouts()[0] = getSubLayouts()[0].rotateNonSiblingsPromote(dir, focus, nextView, parent1, parent2);
            newSubLayouts = new Layout[getSubLayouts()[0].countSubLayouts()];
            for (int i  = 0; i < newSubLayouts.length; i++) {
                newSubLayouts[i] = getSubLayouts()[0].getSubLayouts()[i];
            }
            for (int i = 0; i < newSubLayouts.length; i++) {
                newSubLayouts[i] = newSubLayouts[i].flip();
            }
            return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
        else {
            Layout[] newSubLayouts = new Layout[countSubLayouts()];
            for (int i = 0; i < countSubLayouts(); i++) {
                newSubLayouts[i] = getSubLayouts()[i].rotateNonSiblingsPromote(dir, focus, nextView, parent1, parent2);
            }
            return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
    }

    /** 
     * This method flips the layout, so makes sidebysideLayout to stackedLayout
     * @return: SideBySideLayout
     */
    @Override
    protected StackedLayout flip() {
        Layout[] newSubLayouts = new Layout[countSubLayouts()];
        for (int i = 0; i < countSubLayouts(); i++) {
            newSubLayouts[i] = getSubLayouts()[i].flip();
        }
        return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
    }

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
        View focussed = getFocusedView(focus);
        if (focussed == null) return this;
        Layout[] newSubLayouts;
        if (this == parent) {
            newSubLayouts = new Layout[countSubLayouts() + views.length];
            int j = 0;
            for (int i = 0; i < countSubLayouts(); i++) {
                if (getSubLayouts()[i] == focussed) {
                    newSubLayouts[j] = focussed;
                    j += 1;
                    for (int k = 0; k < views.length; k++) {
                        newSubLayouts[j] = views[k];
                        j += 1;
                    }
                } else {
                    newSubLayouts[j] = getSubLayouts()[i];
                    j += 1;
                }
            }
        } else {
            newSubLayouts = new Layout[countSubLayouts()];
            for (int i = 0; i < countSubLayouts(); i++) {
                newSubLayouts[i] = getSubLayouts()[i].insertViews(focus, parent, views);
            }
        }
        return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    /** 
     * This method returns the size of the subLayouts
     * @return: Point, gives the size of the subLayouts
     */
    @Override
    public Point calcSubSize() {
        return new Point(getHeigth(), (int) Math.floor((float) getWidth() / (float) countSubLayouts()));
    }

    /** 
     * This method returns the leftUpperCorner of the subLayouts
     * @param i int, the index of the subLayout
     * @return: Point, gives the leftUpperCorner of the sublayout i
     */
    @Override
    public Point calcLeftUpCorner(int i) {
        int subWidth = (int) floor((float) getWidth() / (float) countSubLayouts());
        return new Point(getLeftUpperCorner().getX(), getLeftUpperCorner().getY() + i * subWidth);
    }
}
