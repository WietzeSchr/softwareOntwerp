import java.awt.*;

public abstract class CompositeLayout extends Layout
{
    private Layout[] subLayouts;

    public abstract Point calcSubSize();

    public abstract Point calcLeftUpCorner(int i);

    public abstract CompositeLayout addNewChar(char c, int focus);

    public CompositeLayout(int height, int width, Point leftUpperCorner, Layout[] subLayouts) {
        super(height, width, leftUpperCorner);
        this.subLayouts = subLayouts;
    }

    public CompositeLayout(int height, int width, Point leftUpperCorner, String[] filepaths, String newLine) {
        super(height, width, leftUpperCorner);
        int length = filepaths.length;
        this.subLayouts = new Layout[length];
        Point subSize = calcSubSize();
        for (int i = 0; i < length; i++) {
            Point leftUpCorner = calcLeftUpCorner(i);
            setSubLayout(new FileBufferView((int) subSize.getX(), (int) subSize.getY(), this, calcLeftUpCorner(i), filepaths[i], newLine), i);
        }
    }

    public void setSubLayouts(Layout[] newSubLayouts) {
        this.subLayouts = newSubLayouts;
    }

    public Layout[] getSubLayouts() {
        return subLayouts;
    }

    public void setSubLayout(Layout newSubLayout, int i) {
        Layout[] oldSubLayouts = getSubLayouts();
        oldSubLayouts[i] = newSubLayout;
    }

    public int countSubLayouts() {
        return subLayouts.length;
    }

    public void show() {
        Layout[] subLays = getSubLayouts();
        for (int i = 0; i < countSubLayouts(); i++) {
            subLays[i].show();
        }
    }

    @Override
    public int initViewPosition(int i) {
        Layout[] subLayouts = getSubLayouts();
        int i1 = i;
        for (int j = 0; j < countSubLayouts(); j++)
        {
            i1 += subLayouts[j].initViewPosition(i1);
        }
        return i1;
    }

    @Override
    public FileBufferView getFocusedView(int i) {
        Layout[] subLayout = getSubLayouts();
        FileBufferView res = null;
        for (Layout layout : subLayout) {
            if (res == null) {
                res = layout.getFocusedView(i);
            }
        }
        return res;
    }

    @Override
    public int countViews() {
        int result  = 0;
        Layout[] subLays = getSubLayouts();
        for (int i = 0; i < subLays.length; i++) {
            result += subLays[i].countViews();
        }
        return result;
    }
}
