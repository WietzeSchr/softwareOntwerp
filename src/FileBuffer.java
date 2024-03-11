import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class FileBuffer {
    private String[] content;
    private Point insertionPoint;
    private boolean dirty;

    public FileBuffer(String path) throws FileNotFoundException {
        ArrayList<String> content = new ArrayList<>();
        FileInputStream file = new FileInputStream(path);
        int c;
        StringBuilder line = new StringBuilder();
        int column = 1;
        try {
            while ((c = file.read()) != -1) {
                if (c != 10) {
                    column += 1;
                    line.append((char) c);
                } else {
                    column = 1;
                    content.add(line.toString());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.content = content.toArray(new String[0]);
        int contentLength = getContent().length;
        this.insertionPoint = new Point(contentLength, getContent()[contentLength - 1].length() + 1);
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

    public void moveInsertionPoint(Point dir) {
        Point newInsertionPoint = new Point((int) (getInsertionPoint().getX() + dir.getX()),
                (int) (getInsertionPoint().getY() + dir.getY()));
        setInsertionPoint(newInsertionPoint);
    }
}
