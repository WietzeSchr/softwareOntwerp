public class Textr
{
    private Layout layout;

    private int newLine;

    public Textr(String[] filepaths) {
        if (filepaths.length > 1) {
            Layout lay = new StackedLayout(25, 80, filepaths);

        }
    }

    public void setLayout(Layout newLayout) {
        this.layout = newLayout;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setNewLine(int newLine1) {
        newLine = newLine1;
    }

    public int getNewLine() {
        return newLine;
    }
}
