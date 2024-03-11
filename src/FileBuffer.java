import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class FileBuffer {
    private String[] content;
    private Point insertionPoint;
    private boolean dirty;

    public FileBuffer(String path, String newLine) throws FileNotFoundException {
        ArrayList<String> content = new ArrayList<>();
        FileInputStream file = new FileInputStream(path);
        int c;
        StringBuilder line = new StringBuilder();
        int column = 1;
        try {
            while ((c = file.read()) != -1) {
                if (c != 10 && c != 13 && c < 32 || 127 <= c)
                {
                    throw new RuntimeException("File" + path + "contains an illegal byte");
                }
                else {
                    if (c != 10) {
                        line.append((char) c);
                    } else {
                        content.add(line.toString());
                        line = new StringBuilder();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.content = content.toArray(new String[0]);
        this.insertionPoint = new Point(1,1);
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

    public int getRowCount() {
        return getContent().length;
    }

    public int getColumnCount() {
        String[] cont = getContent();
        int result = 1;
        for (int i = 0; i < getRowCount(); i++) {
            if (cont[i].length() > result) {
                result = cont[i].length();
            }
        }
        return result;
    }

    public void moveInsertionPoint(Point dir) {
        Point newInsertionPoint = new Point((int) (getInsertionPoint().getX() + dir.getX()),
                (int) (getInsertionPoint().getY() + dir.getY()));
        setInsertionPoint(newInsertionPoint);
    }
}
