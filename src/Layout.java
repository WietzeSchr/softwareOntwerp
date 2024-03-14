import java.awt.*;

public abstract class Layout {
    private int heigth;

    private int width;

    private Point leftUpperCorner;

    private CompositeLayout parent;

    public abstract void show();

    public abstract int initViewPosition(int i);

    public abstract FileBufferView getFocusedView(int i);

    public abstract int countViews();

    public abstract void updateSize(int heigth, int width, Point leftUpperCorner);

    public Layout(int heigth, int width, Point leftUpperCorner) {
        this.heigth = heigth;
        this.width = width;
        this.parent = null;
        this.leftUpperCorner = leftUpperCorner;
    }

    public Layout(int heigth, int width, CompositeLayout parent, Point leftUpperCorner) {
        this.heigth = heigth;
        this.width = width;
        this.parent = parent;
        this.leftUpperCorner = leftUpperCorner;
    }

    public void setHeigth(int newHeigth) {
        this.heigth = newHeigth;
    }

    public int getHeigth() {
        return heigth;
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

    protected abstract Layout rotateView(int dir, CompositeLayout parent, int focus);
}

