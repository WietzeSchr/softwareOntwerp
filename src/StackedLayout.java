import java.io.FileNotFoundException;

import static java.lang.Math.floor;

public class StackedLayout extends CompositeLayout {

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/

    /** This constructor creates a new StackedLayout with the given height, width, leftUpperCorner, filepaths and newLine
     */
    public StackedLayout(int height, int width, Point leftUpperCorner, String[] filepaths, String newLine) throws FileNotFoundException {
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
            View focusView = getFocusedView(focus);
            View nextView = getFocusedView(nextFocus);
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
    protected CompositeLayout rotateNonSiblings(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2) {
        if (this == parent1)
        {
            View focussed = getFocusedView(focus);
            Layout[] newSubLayouts = new Layout[countSubLayouts() + 1];
            int j = 0;
            for (int i = 0; i < countSubLayouts(); i++) {
                if (getSubLayouts()[i] == focussed) {
                    if (dir == 1) {
                        newSubLayouts[j] = focussed;
                        newSubLayouts[j + 1] = nextView;
                    }
                    else {
                        newSubLayouts[j] = nextView;
                        newSubLayouts[j + 1] = focussed;
                    }
                    j += 2;
                }
                else {
                    newSubLayouts[j] = getSubLayouts()[i].rotateNonSiblings(dir, focus, nextView, parent1, parent2);
                    j += 1;
                }
            }
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
                        if (dir == 1) {
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
            return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
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
            return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
        else {
            Layout[] newSubLayouts = new Layout[countSubLayouts()];
            for (int i = 0; i < countSubLayouts(); i++) {
                newSubLayouts[i] = getSubLayouts()[i].rotateNonSiblingsPromote(dir, focus, nextView, parent1, parent2);
            }
            return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
        }
    }

    @Override
    protected SideBySideLayout flip() {
        Layout[] newSubLayouts = new Layout[countSubLayouts()];
        for (int i = 0; i < countSubLayouts(); i++) {
            newSubLayouts[i] = getSubLayouts()[i].flip();
        }
        return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
    }

    /* ******************
     *  OPEN GAME VIEW  *
     * ******************/

    @Override
    public Layout insertViews(int focus, CompositeLayout parent, View[] views) {
        View focussed = getFocusedView(focus);
        if (focussed == null) return this;
        View[] subSubLayouts = new View[views.length + 1];
        subSubLayouts[0] = focussed;
        for (int k = 0; k < views.length; k++) {
            subSubLayouts[k + 1] = views[k];
        }
        Layout[] newSubLayouts = new Layout[countSubLayouts()];
        if (this == parent) {
            for (int i = 0; i < countSubLayouts(); i++) {
                if (getSubLayouts()[i] == focussed) {
                    newSubLayouts[i] = new SideBySideLayout(1, 1, new Point(1, 1), subSubLayouts);
                } else {
                    newSubLayouts[i] = getSubLayouts()[i];
                }
            }
        } else {
            for (int i = 0; i < countSubLayouts(); i++) {
                newSubLayouts[i] = getSubLayouts()[i].insertViews(focus, parent, views);
            }
        }
        return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLayouts);
    }

    @Override
    public int calcGameWidth(int focus){
        View focused = getFocusedView(focus);
        int result = -1;
        if(focused.getParent() == this) result = getWidth()/2;
        else {
            int i=0;
            while(result<0) {
                result = getSubLayouts()[i++].calcGameWidth(focus);
            }
        }
        return result;
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
        return new Point((int) Math.floor((float) getHeigth() / (float) countSubLayouts()), getWidth());
    }

    /** 
     * This method returns the leftUpperCorner of the subLayouts
     * @param i int, the index of the subLayout
     * @return: Point, gives the leftUpperCorner of the sublayout i
     */
    @Override
    public Point calcLeftUpCorner(int i) {
        int subHeight = (int) floor((float) getHeigth() / (float) countSubLayouts());
        return new Point(getLeftUpperCorner().getX() + i * subHeight, getLeftUpperCorner().getY());
    }
}
