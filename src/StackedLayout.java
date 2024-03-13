import java.awt.*;

import static java.lang.Math.floor;

public class StackedLayout extends CompositeLayout {
    public StackedLayout(int heigth, int width, Point leftUpperCorner, String[] filepaths, String newLine) {
        super(heigth, width, leftUpperCorner, filepaths, newLine);
    }

    public StackedLayout(int heigth, int width, Point leftUpperCorner, Layout[] subLayouts) {
        super(heigth, width, leftUpperCorner, subLayouts);
    }

    public Point calcSubSize() {
        return new Point(getHeigth() / countSubLayouts(), getWidth());
    }


    public Point calcLeftUpCorner(int i) {
        int subHeight = (int) floor(getHeigth() / countSubLayouts());
        return new Point((int) (getLeftUpperCorner().getX() + i * subHeight), (int) getLeftUpperCorner().getY());
    }

    public StackedLayout addNewChar(char c, int focus) {
        Layout[] subLays = getSubLayouts();
        for (int i = 0; i < getSubLayouts().length; i++) {
            subLays[i] = subLays[i].addNewChar(c, focus);
        }
        return new StackedLayout(getHeigth(), getWidth(), getLeftUpperCorner(), subLays);
    }
}
