import java.awt.*;

public class SideBySideLayout extends CompositeLayout{
    public SideBySideLayout(int height, int width, String[] filepaths) {
        super(height, width, filepaths);
    }

    @Override
    public Point calcSubSize() {
        return null;
    }
}
