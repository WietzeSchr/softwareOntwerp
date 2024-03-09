public class Textr
{
    private Layout layout;

    private String newLine;

    public Textr(String[] filepaths) {
        if (filepaths.length > 1) {
            Layout lay = new StackedLayout(25, 80, filepaths);
            this.layout = lay;
        }
        else {
            Layout lay = new FileBufferView(25, 80, filepaths[0]);
            this.layout = lay;
        }
        this.newLine = System.lineSeparator();
    }

    public void setLayout(Layout newLayout) {
        this.layout = newLayout;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setNewLine(String newLine1) {
        newLine = newLine1;
    }

    public String getNewLine() {
        return newLine;
    }
}
