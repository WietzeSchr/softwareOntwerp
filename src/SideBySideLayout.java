import java.awt.*;

public class SideBySideLayout extends CompositeLayout{
    public SideBySideLayout(int height, int width, Point leftUpperCorner, String[] filepaths, String newLine) {
        super(height, width, leftUpperCorner, filepaths, newLine);
    }

    public SideBySideLayout(int height, int width, Point leftUpperCorner, Layout[] subLayouts) {
        super(height, width, leftUpperCorner, subLayouts);
    }

    @Override
    public Point calcSubSize() {
        return null;
    }

    @Override
    public Point calcLeftUpCorner(int i) {
        return null;
    }

    @Override
    public SideBySideLayout addNewChar(char c, int focus) {
        Layout[] subLays = getSubLayouts();
        for (int i = 0; i < subLays.length; i++) {
            subLays[i] = subLays[i].addNewChar(c, focus);
        }
        return new SideBySideLayout(getHeigth(), getWidth(), getLeftUpperCorner(), subLays);
    }
}
