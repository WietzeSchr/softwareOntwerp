public abstract class CompositeLayout extends Layout
{
    private Layout[] subLayouts;

    public void setSubLayouts(Layout[] newSubLayouts) {
        this.subLayouts = newSubLayouts;
    }

    public Layout[] getSubLayouts() {
        return subLayouts;
    }

    public int countSubLayouts() {
        return subLayouts.length;
    }
}
