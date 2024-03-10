import java.awt.*;

public abstract class CompositeLayout extends Layout
{
    private Layout[] subLayouts;

    public abstract Point calcSubSize();

    public abstract Point calcLeftUpCorner(int i);

    public CompositeLayout(int height, int width, Point leftUpperCorner, String[] filepaths) {
        super(height, width, leftUpperCorner);
        Point subSize = calcSubSize();
        int length = filepaths.length;
        Layout[] subLay = new Layout[length];
        for (int i = 0; i < length; i++) {
            Point leftUpCorner = calcLeftUpCorner(i);
            subLay[i] = new FileBufferView((int) subSize.getX(), (int) subSize.getY(), this, calcLeftUpCorner(i), filepaths[i]);
        }
    }

    public void setSubLayouts(Layout[] newSubLayouts) {
        this.subLayouts = newSubLayouts;
    }

    public Layout[] getSubLayouts() {
        return subLayouts;
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
}
