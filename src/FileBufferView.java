import io.github.btj.termios.Terminal;

import java.awt.*;
import java.io.FileNotFoundException;

public class FileBufferView extends Layout
{
    private int verticalScrollState;

    private int horizontalScrollState;

    private int position;

    private File file;

    public FileBufferView(int heigth, int witdh, Point leftUpperCorner, String filepath, String newLine) {
        super(heigth, witdh, leftUpperCorner);
        try {
            this.file = new File(filepath, newLine);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.verticalScrollState = 1;
        this.horizontalScrollState = 1;
    }

    public FileBufferView(int heigth, int witdh, CompositeLayout parent, Point leftUpperCorner, String filepath, String newLine) {
        super(heigth, witdh, parent, leftUpperCorner);
        try {
            this.file = new File(filepath, newLine);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.verticalScrollState = 1;
        this.horizontalScrollState = 1;
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

    public void setFile(File newFile) {
        this.file = newFile;
    }

    public File getFile() {
        return file;
    }

    public void setPosition(int newPosition) {
        this.position = newPosition;
    }

    public int getPosition() {
        return this.position;
    }

    public String[] getContent() {
        return getFile().getContent();
}

    public FileBuffer getBuffer() {
        return getFile().getBuffer();
    }

    public Point getInsertionPoint() {
        return getBuffer().getInsertionPoint();
    }

    public int getRowCount() {
        return getBuffer().getRowCount();
    }

    public int getColumnCount() {
        return getBuffer().getColumnCount();
    }

    public String getPath() {
        return getFile().getPath();
    }

    public Point getCursor() {
        Point insert = getInsertionPoint();
        Point leftUp = getLeftUpperCorner();
        return new Point((int) (leftUp.getX() + insert.getX() - getVerticalScrollState()), (int) (leftUp.getY() + insert.getY() - getHorizontalScrollState()));
    }

    public void show() {
        String[] cont = getContent();
        for (int i = 0; i < getHeigth() - 1; i++) {
            int row = i + getVerticalScrollState() - 1;
            if (row >= getRowCount()) {
                break;
            }
            if (cont[row] != null) {
                if (cont[row].length() >= getWidth() + getHorizontalScrollState() - 2) {
                    Terminal.printText((int) getLeftUpperCorner().getX() + i,
                            (int) getLeftUpperCorner().getY(), cont[row].substring(getHorizontalScrollState() - 1, getHorizontalScrollState() + getWidth() - 3));
                } else {
                    Terminal.printText((int) getLeftUpperCorner().getX() + i,
                            (int) getLeftUpperCorner().getY(), cont[row].substring(getHorizontalScrollState() - 1));
                }
            }
        }
        showScrollbars();
    }

    public void addNewLineBreak() {
        getBuffer().insertLineBreak();
        updateScrollStates();
    }

    private void showScrollbars() {
        char[] verticalScrollBar = makeVerticalScrollBar();
        String horizontalScrollBar = makeHorizontalScrollBar();
        Terminal.printText((int) (getLeftUpperCorner().getX() + getHeigth()) - 1, (int) getLeftUpperCorner().getY(), horizontalScrollBar);
        for (int i = 0; i < verticalScrollBar.length; i++) {
            Terminal.printText((int) (getLeftUpperCorner().getX() + i),
                    (int) (getLeftUpperCorner().getY() + getWidth() - 1), String.valueOf(verticalScrollBar[i]));
        }
    }

    private char[] makeVerticalScrollBar() {
        char[] result = new char[getHeigth() - 1];
        if (getHeigth() > getRowCount()) {
            for (int i = 0; i < getHeigth() - 1; i++) {
                result[i] = '#';
            }
        }
        else {
            int start = (int) Math.floor((float) getVerticalScrollState() / ((float) getVerticalScrollState() + getHeigth()) * getHeigth());
            int end = (int) Math.floor((float) (getVerticalScrollState() + getHeigth() - 1) / (float) getRowCount() * getHeigth());
            for (int i = 0; i < getHeigth() - 1; i++) {
                if (i < start || i > end) {
                    result[i] = '|';
                }
                else{
                    result[i] = '#';
                }
            }
        }
        return result;
    }

    private String makeHorizontalScrollBar() {
        StringBuilder result = new StringBuilder();
        result.append(getPath() + ", rows: " + String.valueOf(getRowCount()) + ", columns: " + String.valueOf(getColumnCount()) + " ");
        if (getWidth() - 1 > getColumnCount()) {
            while (result.length() < getWidth()) {
                result.append('#');
            }
        }
        else {
            int start = (int) Math.floor((float) getHorizontalScrollState() / (float) (getHorizontalScrollState() + getWidth()) * (getWidth() - result.length()) + 1);
            int end = (int) Math.floor((float) (getHorizontalScrollState() + getWidth() - 1)/ (float) getColumnCount() * (getWidth() - result.length()));
            int i = 1;
            while (result.length() < getWidth()) {
                if (i < start || i > end) {
                    result.append('-');
                }
                else {
                    result.append('#');
                }
                i++;
            }
        }
        return result.toString();
    }

    public void addNewChar(char c) {
        File file = getFile();
        file.addNewChar(c);
        setFile(file);
        updateScrollStates();
    }

    public void deleteChar() {
        getBuffer().deleteChar();
        updateScrollStates();
    }

    @Override
    protected FileBufferView rotateView(int dir, CompositeLayout parent, int focus) {
        return this;
    }

    public Layout rotateview(int dir, CompositeLayout parent, int focus) {
        return this;
    }

    public void updateScrollStates() {
        if (getInsertionPoint().getY() > getHorizontalScrollState() + getWidth() - 2) {
            setHorizontalScrollState((int) getInsertionPoint().getY());
        }
        else if (getInsertionPoint().getY() < getHorizontalScrollState()) {
            setHorizontalScrollState(getHorizontalScrollState() -  getWidth() + 1);
        }
        if (getInsertionPoint().getX() > getVerticalScrollState() + getHeigth() - 2) {
            setVerticalScrollState((int) getInsertionPoint().getX());
        }
        else if (getInsertionPoint().getX() < getVerticalScrollState()) {
            setVerticalScrollState(getVerticalScrollState() - getHeigth() + 1);
        }
    }

    @Override
    public int initViewPosition(int i) {
        setPosition(i);
        return 1;
    }

    @Override
    public FileBufferView getFocusedView(int i) {
        if (getPosition() == i) {
            return this;
        }
        return null;
    }

    @Override
    public int countViews() {
        return 1;
    }

    @Override
    public void updateSize(int heigth, int width, Point leftUpperCorner) {
        setHeigth(heigth);
        setWidth(width);
        setLeftUpperCorner(leftUpperCorner);
        updateScrollStates();
    }

    @Override
    public FileBufferView closeBuffer(int focus, CompositeLayout parent) {
        if (getPosition() != focus) {
            return this;
        }
        return null;
    }
}
