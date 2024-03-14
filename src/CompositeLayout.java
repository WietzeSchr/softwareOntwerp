import java.awt.*;

public abstract class CompositeLayout extends Layout
{
    private Layout[] subLayouts;

    public abstract Point calcSubSize();

    public abstract Point calcLeftUpCorner(int i);

    public abstract void addNewChar(char c, int focus);

    public abstract CompositeLayout rotateView(int dir, CompositeLayout parent, int focus);

    public CompositeLayout(int height, int width, Point leftUpperCorner, int subLayCount) {
        super(height, width, leftUpperCorner);
        this.subLayouts = new Layout[subLayCount];
    }
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

    public void replaceView(CompositeLayout parent, Layout subView1, Layout subView2) {
        Layout[] newSubLays = new Layout[countSubLayouts() + 1];
        for (int i = 0; i < newSubLays.length - 1; i++) {
            if (getSubLayouts()[i] == parent) {
                newSubLays[i] = subView1;
            }
            else if (i > 0 && getSubLayouts()[i - 1] == parent) {
                newSubLays[i] = subView2;
            }
            else {
                newSubLays[i] = getSubLayouts()[i];
            }
        }
        setSubLayouts(newSubLays);
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
        setSubLayouts(subLayouts);
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

    @Override
    public void updateSize(int heigth, int width, Point leftUpperCorner) {
        setHeigth(heigth);
        setWidth(width);
        setLeftUpperCorner(leftUpperCorner);
        Point subSize = calcSubSize();
        for (int i = 0; i < countSubLayouts(); i++) {
            Point subLeftUp = calcLeftUpCorner(i);
            getSubLayouts()[i].updateSize((int) subSize.getX(), (int) subSize.getY(), subLeftUp);
        }
    }
}
