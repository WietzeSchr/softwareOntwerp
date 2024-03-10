import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FileBuffer {
    private String[] content;
    private Point insertionPoint;
    private boolean dirty;

    public FileBuffer(String path)
    {
        ArrayList<String> conts = new ArrayList<>();
        StringBuilder cont = new StringBuilder();
        try (Scanner fileReader = new Scanner(path)) {
            while (fileReader.hasNextByte()) {
                int c = fileReader.nextByte();
                if (c != 10 && c != 13 && (c < 32 || c > 126)) {
                    throw new Exception("File contains illegal byte");
                }
                else if (c == 10) {
                    conts.add(cont.toString());
                }
                else {
                    cont.append(String.valueOf((char) c));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.content = (String[]) conts.toArray();
        this.insertionPoint = new Point(1, 1);
        this.dirty = false;
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

    public String[] getContent() {
        return content;
    }

    public void setContent(String[] newContent) {
        this.content = newContent;
    }

    public void moveInsertionPoint(Point dir)
    {
        Point newInsertionPoint = new Point((int) (getInsertionPoint().getX() + dir.getX()),
                (int) (getInsertionPoint().getY() + dir.getY()));
        setInsertionPoint(newInsertionPoint);
    }
}
