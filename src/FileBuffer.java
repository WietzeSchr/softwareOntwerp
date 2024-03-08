import java.awt.*;
import java.rmi.UnexpectedException;
import java.util.Scanner;

public class FileBuffer {
    private String content;
    private Point insertionPoint;
    private FileBufferView bufferView;
    private boolean dirty;

    public FileBuffer(String path)
    {
        StringBuilder cont = new StringBuilder();
        try (Scanner fileReader = new Scanner(path)) {
            while (fileReader.hasNextByte()) {
                int c = fileReader.nextByte();
                if (c != 10 && c != 13 && (c < 32 || c > 126)) {
                    throw new Exception("File contains illegal byte");
                }
                else {
                    cont.append(String.valueOf((char) c));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.content = cont.toString();
        this.insertionPoint = null;
        this.bufferView = null;
        this.dirty = false;
    }

    public FileBufferView getBufferView() {
        return bufferView;
    }

    public void setBufferView(FileBufferView newBufferView) {
        this.bufferView = newBufferView;
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
