import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.floor;

public class SideBySideLayout extends CompositeLayout{

    public SideBySideLayout(int height, int width, Point leftUpperCorner, int subLaysCount) {
        super(height, width, leftUpperCorner, subLaysCount);
    }
    public SideBySideLayout(int height, int width, Point leftUpperCorner, String[] filepaths, String newLine) {
        super(height, width, leftUpperCorner, filepaths, newLine);
    }

    public SideBySideLayout(int height, int width, Point leftUpperCorner, Layout[] subLayouts) {
        super(height, width, leftUpperCorner, subLayouts);
    }

    @Override
    public Point calcSubSize() {
        return new Point(getHeigth(), (int) Math.floor((float) getWidth() / (float) countSubLayouts()));
    }

    @Override
    public Point calcLeftUpCorner(int i) {
        int subWidth = (int) floor((float) getWidth() / (float) countSubLayouts());
        return new Point((int) getLeftUpperCorner().getX(), (int) getLeftUpperCorner().getY() + i * subWidth);
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
                    return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), newSubLays);
                }
            } else {    // this == parent && countViews > 2
                Layout[] newSubLays = new Layout[countSubLayouts() - 1];
                int i = 0;
                for (int j = 0; j < newSubLays.length; j++) {
                    if (getSubLayouts()[i] == focused) {
                        Layout[] subSubLays = new Layout[2];
                        subSubLays[0] = focused;
                        subSubLays[1] = next;
                        newSubLays[j] = new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), subSubLays);
                        i += 2;
                    }
                    else {
                        newSubLays[j] = getSubLayouts()[i];
                        i += 1;
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
            setSubLayouts(newSubLays.toArray(new Layout[newSubLays.size()]));
            return this;
        }
        return null;
    }
}
