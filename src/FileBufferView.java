public class FileBufferView extends Layout
{
    private int verticalScrollState;
    private int horizontalScrollState;

    private int position;

    private FileBuffer buffer;

    public FileBufferView(int heigth, int witdh, String filepath) {
        super(heigth, witdh);
    }

    public FileBufferView(int heigth, int witdh, Layout parent, String filepath) {
        super(heigth, witdh,parent);
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

    public void setPosition(int newPosition) {
        this.position = newPosition;
    }

    public int getPosition() {
        return this.position;
    }
}
