public abstract class Layout
{
    private int heigth;

    private int width;

    private Layout parent;

    public Layout(int heigth, int width) {
        this.heigth = heigth;
        this.width = width;
        this.parent = null;
    }

    public Layout(int heigth, int width, Layout parent) {
        this.heigth = heigth;
        this.width = width;
        this.parent = parent;
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


    public void setParent(Layout newParent) {
        this.parent = newParent;
    }

    public Layout getParent() {
        return parent;
    }
}
