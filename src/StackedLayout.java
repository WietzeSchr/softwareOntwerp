import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.floor;

public class StackedLayout extends CompositeLayout {
    public StackedLayout(int height, int width, Point leftUpperCorner, int subLaysCount) {
        super(height, width, leftUpperCorner, subLaysCount);
    }

    public StackedLayout(int height, int width, Point leftUpperCorner, String[] filepaths, String newLine) {
        super(height, width, leftUpperCorner, filepaths, newLine);
    }

    public StackedLayout(int heigth, int width, Point leftUpperCorner, Layout[] subLayouts) {
        super(heigth, width, leftUpperCorner, subLayouts);
    }

    public Point calcSubSize() {
        return new Point((int) Math.floor((float) getHeigth() / (float) countSubLayouts()), getWidth());
    }


    public Point calcLeftUpCorner(int i) {
        int subHeight = (int) floor((float) getHeigth() / (float) countSubLayouts());
        return new Point((int) (getLeftUpperCorner().getX() + i * subHeight), (int) getLeftUpperCorner().getY());
    }

    @Override
    public CompositeLayout rotateView(int dir, CompositeLayout parent, int focus) {
        if (this == parent) {
            FileBufferView focused = getFocusedView(focus);
            FileBufferView next = getFocusedView(focus + 1);
            if (countSubLayouts() == 2) {
                if (getParent() != null) {
                    if (dir == -1) {
                        getParent().replaceView(parent, focused, next);
                    } else {
                        getParent().replaceView(parent, next, focused);
                    }
                    return null;
                } else {
                    Layout[] newSubLays = new Layout[2];
                    if (dir == -1) {
                        newSubLays[0] = focused;
                        newSubLays[1] = next;
                    } else {
                        newSubLays[0] = next;
                        newSubLays[1] = focused;
                    }
                    return new SideBySideLayout(getHeigth(),getWidth(),getLeftUpperCorner(),newSubLays);
                }
            } else {    // this == parent && countViews > 2
                Layout[] newSubLays = new Layout[countSubLayouts() - 1];
                int i = 0;
                for (Layout lay : newSubLays) {
                    if (getSubLayouts()[i] == focused) {
                        Layout[] subSubLays = new Layout[2];
                        subSubLays[0] = focused;
                        subSubLays[1] = next;
                        newSubLays[i] = new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), subSubLays);
                        i += 2;
                    }
                    else {
                        newSubLays[i] = getSubLayouts()[i];
                    }
                }
                setSubLayouts(newSubLays);
            }
        }
        else {
            ArrayList<Layout> newSubLays = new ArrayList<>();
            for (int i = 0; i < getSubLayouts().length; i++) {
                if (getSubLayouts()[i].rotateView(dir, parent, focus) != null) {
                    newSubLays.add(getSubLayouts()[i].rotateView(dir, parent, focus));
                }
            }
            setSubLayouts((Layout[]) newSubLays.toArray());
            return this;
        }
        return null;
    }
}
