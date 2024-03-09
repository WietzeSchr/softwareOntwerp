import java.awt.*;

public class StackedLayout extends CompositeLayout {
    public StackedLayout(int heigth, int width, String[] filepaths) {
        super(heigth, width, filepaths);
    }

    public Point calcSubSize() {
        return new Point(getHeigth() / countSubLayouts(), getWidth());
    }

}
