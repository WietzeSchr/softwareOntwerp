import io.github.btj.termios.Terminal;

import java.awt.*;
import java.io.FileNotFoundException;

public class FileBufferView extends Layout
{
    private int verticalScrollState;

    private int horizontalScrollState;

    private int position;

    private File file;

    public FileBufferView(int heigth, int witdh, Point leftUpperCorner, String filepath) {
        super(heigth, witdh, leftUpperCorner);
        try {
            this.file = new File(filepath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public FileBufferView(int heigth, int witdh, Layout parent, Point leftUpperCorner, String filepath) {
        super(heigth, witdh, parent, leftUpperCorner);
        try {
            this.file = new File(filepath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

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

    public void show() {
        String[] cont = getContent();
        for (int i = 0; i < getHeigth() - 2; i++) {
            int row = i + getVerticalScrollState();
            if (row >= getRowCount()) {
                break;
            }
            if (cont[row].length() >= getWidth() - getHorizontalScrollState() - 2) {
                Terminal.printText((int) (getLeftUpperCorner().getX() + i), 1, cont[row].substring(getHorizontalScrollState() - 1, getHorizontalScrollState() + getWidth() - 3));
            }
            else {
                Terminal.printText((int) getLeftUpperCorner().getX() + i, 1, cont[row]);
            }
        }
        showScrollbars();
    }

    private void showScrollbars() {
        char[] verticalScrollBar = makeVerticalScrollBar();
        String horizontalScrollBar = makeHorizontalScrollBar();
        Terminal.printText((int) (getLeftUpperCorner().getX() + getHeigth()) - 1, 1, horizontalScrollBar);
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
            int start = (int) Math.floor((float) getVerticalScrollState() / (float) getRowCount() * getHeigth() + 1);
            int end = (int) Math.floor((float) (getVerticalScrollState() + getHeigth() - 1) / getRowCount() * getHeigth());
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
        if (getWidth() > getColumnCount()) {
            for (int i = 0; i < getWidth(); i++) {
                result.append('#');
            }
        }
        else {
            int start = (int) Math.floor((float) getHorizontalScrollState() / (float) getColumnCount() * getWidth() + 1);
            int end = (int) Math.floor((float) (getHorizontalScrollState() + getWidth() - 1)/ (float) getColumnCount() * getWidth());
            for (int i = 0; i < getWidth(); i++) {
                if (i < start || i > end) {
                    result.append('-');
                }
                else {
                    result.append('#');
                }
            }
        }
        return result.toString();
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
}
