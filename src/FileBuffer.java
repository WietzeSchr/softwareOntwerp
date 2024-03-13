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
                    if (c != 13 && c != 10) {
                        line.append((char) c);
                    } else {
                        content.add(line.toString());
                        line = new StringBuilder();
                    }
                }
            }
            content.add(line.toString());
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
        return new Point((int) insertionPoint.getX(), (int) insertionPoint.getY());
    }

    public void setInsertionPoint(Point insertionPoint) {
        if (insertionPoint.getX()<1 || insertionPoint.getY()<1 || insertionPoint.getX() > content.length){
            return;
        }
        //spring naar laatste character van deze lijn
        int currRowLength = content[(int)getInsertionPoint().getX()].length();
        if (insertionPoint.getY() > currRowLength){
            insertionPoint = new Point((int)insertionPoint.getX(), currRowLength);
        }
        this.insertionPoint = insertionPoint;
    }

    public String[] getContent() {
        String[] result = new String[content.length];
        for (int i = 0; i < content.length; i++) {
            result[i] = String.copyValueOf(content[i].toCharArray());
        }
        return result;
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

    public void addNewChar(char c) {
        Point insert = getInsertionPoint();
        String[] content = getContent();
        if (content.length == 0)
        {
            content = new String[1];
            content[0] = String.valueOf(c);
        }
        else {
            String row = content[(int) insert.getX() - 1];
            StringBuilder eRow = new StringBuilder();
            for (int i = 0; i < row.length(); i++) {
                if (i == insert.getY() - 1) {
                    eRow.append(c);
                }
                eRow.append(row.toCharArray()[i]);
            }
            if (insert.getY() > row.length())
            {
                eRow.append(c);
            }
            content[(int) insert.getX() - 1] = eRow.toString();
        }
        setContent(content);
        moveInsertionPoint(new Point(0, 1));
        setDirty(true);
    }
}
