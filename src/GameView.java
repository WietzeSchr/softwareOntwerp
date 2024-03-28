import java.io.IOException;

public class GameView extends View{

    public GameView(int height, int width, Point leftUpperCorner) {
        super(height, width, leftUpperCorner);
    }

    @Override
    public Layout closeBuffer(int focus, CompositeLayout parent) throws IOException {
        return null;
    }

    @Override
    protected Layout rotateView(int dir, int focus) {
        return null;
    }

    @Override
    protected Layout rotateSiblings(int dir, int focus, int nextFocus, CompositeLayout parent) {
        return null;
    }

    @Override
    protected Layout rotateSiblingsFlip(int dir, int focus, int nextFocus, CompositeLayout parent) {
        return null;
    }

    @Override
    protected Layout rotateNonSiblingsPromote(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2) {
        return null;
    }

    @Override
    protected Layout rotateNonSiblings(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2) {
        return null;
    }

    @Override
    String[] makeShow() {
        return new String[0];
    }
}
