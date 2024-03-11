import java.awt.*;

import static java.lang.Math.floor;

public class StackedLayout extends CompositeLayout {
    public StackedLayout(int heigth, int width, Point leftUpperCorner, String[] filepaths, String newLine) {
        super(heigth, width, leftUpperCorner, filepaths, newLine);
    }

    public Point calcSubSize() {
        return new Point(getHeigth() / countSubLayouts(), getWidth());
    }


    public Point calcLeftUpCorner(int i) {
        int subHeight = (int) floor(getHeigth() / countSubLayouts());
        return new Point((int) (getLeftUpperCorner().getX() + i * subHeight), (int) getLeftUpperCorner().getY());
    }

}
