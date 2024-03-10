import java.awt.*;

import static java.lang.Math.floor;

public class StackedLayout extends CompositeLayout {
    public StackedLayout(int heigth, int width, Point leftUpperCorner, String[] filepaths) {
        super(heigth, width, leftUpperCorner, filepaths);
    }

    public Point calcSubSize() {
        return new Point(getHeigth() / countSubLayouts(), getWidth());
    }


    public Point calcLeftUpCorner(int i) {
        int hei = (int) floor(getHeigth() / countSubLayouts());
        return new Point((int) getLeftUpperCorner().getX(), (int) (getLeftUpperCorner().getY() + i * hei));
    }

}
