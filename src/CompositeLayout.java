import java.awt.*;
import java.io.IOException;

public abstract class CompositeLayout extends Layout
{
    private Layout[] subLayouts;

    public abstract Point calcSubSize();

    public abstract Point calcLeftUpCorner(int i);

    public CompositeLayout(int height, int width, Point leftUpperCorner, int subLayCount) {
        super(height, width, leftUpperCorner);
        this.subLayouts = new Layout[subLayCount];
    }
    public CompositeLayout(int height, int width, Point leftUpperCorner, Layout[] subLayouts) {
        super(height, width, leftUpperCorner);
        for (int i = 0; i < subLayouts.length; i++) {
            subLayouts[i].setParent(this);
        }
        this.subLayouts = subLayouts;
    }

    public CompositeLayout(int height, int width, Point leftUpperCorner, String[] filepaths, String newLine) {
        super(height, width, leftUpperCorner);
        int length = filepaths.length;
        this.subLayouts = new Layout[length];
        Point subSize = calcSubSize();
        for (int i = 0; i < length; i++) {
            Point leftUpCorner = calcLeftUpCorner(i);
            setSubLayout(new FileBufferView((int) subSize.getX(), (int) subSize.getY(), this, leftUpCorner, filepaths[i], newLine), i);
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
            subLayouts[j].initViewPosition(i1);
            i1 += subLayouts[j].countViews();
        }
        setSubLayouts(subLayouts);
        return i + i1;
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

    @Override
    public Layout closeBuffer(int focus, CompositeLayout parent) throws IOException {
        if (this == parent) {
            if (getSubLayouts().length == 2) {
                if (getSubLayouts()[0].closeBuffer(focus, parent) != null) {
                    return getSubLayouts()[0];
                } else return getSubLayouts()[1];
            } else {
                Layout[] newSubLayouts = new Layout[countSubLayouts() - 1];
                int i = 0;
                for (int j = 0; j < getSubLayouts().length; j++) {
                    if (getSubLayouts()[j].closeBuffer(focus, parent) != null) {
                        newSubLayouts[i] = getSubLayouts()[j];
                        i++;
                    }
                }
                setSubLayouts(newSubLayouts);
                return this;
            }
        }
        else {
            Layout[] newSubLayouts = getSubLayouts();
            for (int i = 0; i < newSubLayouts.length; i++) {
                newSubLayouts[i] = getSubLayouts()[i].closeBuffer(focus, parent);
            }
            setSubLayouts(newSubLayouts);
            return this;
        }
    }

    public Layout prune(){
        if(getSubLayouts().length == 1) {
            if(this.getParent()==null) {
                getSubLayouts()[0].setParent(this.getParent());
                return getSubLayouts()[0];
            }
            else {
                setSubLayouts(getSubLayouts()[0].getParent().getSubLayouts());
            }
        }
        else {
            for (int i=0; i<getSubLayouts().length; i++) {
                if(getSubLayouts()[i] instanceof CompositeLayout){
                    CompositeLayout subLay = (CompositeLayout) getSubLayouts()[i];
                    setSubLayout(subLay.prune(), i);
                }
            }
        }
        return this;
    }

    protected abstract Layout rotateView(int dir, CompositeLayout parent, int focus, int nextFocus);
}
