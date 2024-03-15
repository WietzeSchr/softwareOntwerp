import java.awt.*;
import java.io.IOException;

public abstract class Layout {
    private int height;

    private int width;

    private Point leftUpperCorner;

    private CompositeLayout parent;

    public abstract void show();

    public abstract int initViewPosition(int i);

    public abstract FileBufferView getFocusedView(int i);

    public abstract int countViews();

    public abstract void updateSize(int heigth, int width, Point leftUpperCorner);

    public abstract Layout closeBuffer(int focus, CompositeLayout parent) throws IOException;

    public Layout(int height, int width, Point leftUpperCorner) {
        this.height = height;
        this.width = width;
        this.parent = null;
        this.leftUpperCorner = leftUpperCorner;
    }

    public Layout(int height, int width, CompositeLayout parent, Point leftUpperCorner) {
        this.height = height;
        this.width = width;
        this.parent = parent;
        this.leftUpperCorner = leftUpperCorner;
    }

    public void setHeigth(int newHeight) {
        this.height = newHeight;
    }

    public int getHeigth() {
        return height;
    }

    public void setWidth(int newWidth) {
        this.width = newWidth;
    }

    public int getWidth() {
        return width;
    }


    public void setParent(CompositeLayout newParent) {
        this.parent = newParent;
    }

    public CompositeLayout getParent() {
        return parent;
    }

    public void setLeftUpperCorner(Point newLeftUpperCorner) {
        this.leftUpperCorner = newLeftUpperCorner;
    }

    public Point getLeftUpperCorner() {
        return leftUpperCorner;
    }

    protected abstract Layout rotateView(int dir, CompositeLayout parent, int focus, int nextFocus);
}

