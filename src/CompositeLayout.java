import java.awt.*;

public abstract class CompositeLayout extends Layout
{
    private Layout[] subLayouts;

    public abstract Point calcSubSize();

    public CompositeLayout(int height, int width, String[] filepaths) {
        super(height, width);
        Point subSize = calcSubSize();
        int length = filepaths.length;
        Layout[] subLay = new Layout[length];
        for (int i = 0; i < length; i++) {
            subLay[i] = new FileBufferView(subSize.getX(), subSize.getY(), filepaths[i]);
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
}
