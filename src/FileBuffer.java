import java.awt.*;

public class FileBuffer {
    private String content;
    private String path;
    private Point insertionPoint;
    private FileBufferView bufferView;
    private boolean dirty;

    public FileBufferView getBufferView() {
        return bufferView;
    }

    public void setBufferView(FileBufferView newBufferview) {
        this.bufferView = newBufferview;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String newPath)
    {
        path = newPath;
    }

    public boolean getDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public Point getInsertionPoint() {
        return insertionPoint;
    }

    public void setInsertionPoint(Point insertionPoint) {
        this.insertionPoint = insertionPoint;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void moveInsertionPoint(Point dir)
    {
        Point newInsertionPoint = new Point((int) (getInsertionPoint().getX() + dir.getX()),
                (int) (getInsertionPoint().getY() + dir.getY()));
        setInsertionPoint(newInsertionPoint);
    }
}
