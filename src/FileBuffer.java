import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FileBuffer {
    private String[] content;
    private Point insertionPoint;
    private boolean dirty;

    public FileBuffer(String path, String newLine) throws FileNotFoundException {
        ArrayList<String> content = new ArrayList<>();
        FileInputStream file = new FileInputStream(path);
        byte[] newLineBytes = newLine.getBytes();
        int c;
        StringBuilder line = new StringBuilder();
        int column = 1;
        try {
            while ((c = file.read()) != -1) {
                if (c != 10 && c != 13 && c < 32 || 127 <= c) {
                    throw new RuntimeException("File" + path + "contains an illegal byte");
                } else {
                    if (c != 13 && c != 10) {
                        line.append((char) c);
                    } else if(isLineSeparator(c, newLineBytes, file)){
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
        this.insertionPoint = new Point(1, 1);
        this.dirty = false;
    }

    private boolean isLineSeparator(int c, byte[] lineSep, FileInputStream file) throws IOException {
        if(c != lineSep[0]){return false;}
        if(c == 13) {
            c = file.read();
            return c==10;
        }
        return false;
    }

    public void insertLineBreak(){
        int row = (int)getInsertionPoint().getX()-1;
        int col = (int)getInsertionPoint().getY()-1;
        ArrayList<String> cont = new ArrayList<String>(Arrays.asList(getContent()));
        String currentRow = getContent()[row];
        String firstPart = currentRow.substring(0, col);
        String secondPart = currentRow.substring(col);
        cont.set(row, firstPart);
        cont.add(row + 1, secondPart);
        setContent(cont.toArray(new String[0]));
        setInsertionPoint(new Point((int)getInsertionPoint().getX()+1, 1));
        setDirty(true);
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
        //spring naar laatste character van target lijn
        int currRowLength = content[(int)insertionPoint.getX()-1].length();
        if (insertionPoint.getY()-1 > currRowLength){
            insertionPoint = new Point((int)insertionPoint.getX(), currRowLength+1);
        }
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
            if (cont[i] != null) {
                if (cont[i].length() > result) {
                    result = cont[i].length();
                }
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
            if (row == null) {
                row = String.valueOf(c);
                content[(int) getInsertionPoint().getX() - 1] = row;
            }
            else {
                StringBuilder eRow = new StringBuilder();
                for (int i = 0; i < row.length(); i++) {
                    if (i == insert.getY() - 1) {
                        eRow.append(c);
                    }
                    eRow.append(row.toCharArray()[i]);
                }
                if (insert.getY() > row.length()) {
                    eRow.append(c);
                }
                content[(int) insert.getX() - 1] = eRow.toString();
            }
        }
        setContent(content);
        moveInsertionPoint(new Point(0, 1));
        setDirty(true);
    }

    public void deleteChar() {
        String[] content = getContent();
        String[] newContent;
        if (content[(int) (getInsertionPoint().getX() - 1)] == null)  {
            newContent = new String[content.length - 1];
            int j = 0;
            for (int i = 0; i < content.length; i++) {
                if (i != getInsertionPoint().getX() - 1) {
                    newContent[j] = content[i];
                    j++;
                }
            }
            setInsertionPoint(new Point((int) (getInsertionPoint().getX() + 1), content[(int) (getInsertionPoint().getX() + 1)].length()));
        }
        else {
            newContent = new String[content.length];
            for (int i = 0; i < content.length; i++) {
                if (i != getInsertionPoint().getX() - 1) {
                    newContent[i] = String.copyValueOf(content[i].toCharArray());
                }
                else {
                    StringBuilder newRow = new StringBuilder();
                    for (int j = 0; j < content[i].length(); j++) {
                        if (j != getInsertionPoint().getY() - 2) {
                            newRow.append(content[i].toCharArray()[j]);
                        }
                    }
                    newContent[i] = newRow.toString();
                }
            }
            moveInsertionPoint(new Point(0, -1));
        }
        setContent(newContent);
        setDirty(true);
    }
}
