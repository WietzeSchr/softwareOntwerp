import java.awt.*;

public class SideBySideLayout extends CompositeLayout{
    public SideBySideLayout(int height, int width, Point leftUpperCorner, String[] filepaths, String newLine) {
        super(height, width, leftUpperCorner, filepaths, newLine);
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
