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

    public void show() {
        String[] cont = getContent();
        for (int i = 0; i < cont.length; i++) {
            Terminal.printText((int) (getLeftUpperCorner().getX() + i), 1, cont[i]);
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
}
