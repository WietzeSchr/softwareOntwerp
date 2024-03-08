public abstract class Layout
{
    private int heigth;

    private int width;

    private int position;

    private Layout parent;

    public Layout(){
        this.heigth = 0;
        this.width = 0;
        this.position = 0;
        this.parent = null;
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

    public void setPosition(int newPosition) {
        this.position = newPosition;
    }

    public int getPosition() {
        return this.position;
    }

    public void setParent(Layout newParent) {
        this.parent = newParent;
    }

    public Layout getParent() {
        return parent;
    }
}
