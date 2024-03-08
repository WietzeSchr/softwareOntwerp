public class FileBufferView extends Layout
{
    private int verticalScrollState;
    private int horizontalScrollState;

    private FileBuffer buffer;

    public FileBufferView(FileBuffer buffer) {
        super();
    }

    public void setVerticalScrollState(int newVerticalScrollState) {
        this.verticalScrollState = newVerticalScrollState;
    }

    public int getVerticalScrollState() {
        return verticalScrollState;
    }

    public void setHorizontalScrollState(int newHorizontalScrollState) {
        this.horizontalScrollState = newHorizontalScrollState;
    }

    public int getHorizontalScrollState() {
        return horizontalScrollState;
    }

    public void setBuffer(FileBuffer newBuffer) {
        this.buffer = newBuffer;
    }

    public FileBuffer getBuffer() {
        return buffer;
    }
}
