import java.awt.*;

public class SideBySideLayout extends CompositeLayout{
    public SideBySideLayout(int height, int width, Point leftUpperCorner, String[] filepaths) {
        super(height, width, leftUpperCorner, filepaths);
    }

    @Override
    public Point calcSubSize() {
        return null;
    }

    @Override
    public Point calcLeftUpCorner(int i) {
        return null;
    }
}
