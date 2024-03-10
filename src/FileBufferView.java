import io.github.btj.termios.Terminal;

import java.awt.*;

public class FileBufferView extends Layout
{
    private int verticalScrollState;

    private int horizontalScrollState;

    private int position;

    private File file;

    public FileBufferView(int heigth, int witdh, Point leftUpperCorner, String filepath) {
        super(heigth, witdh, leftUpperCorner);
        this.file = new File(filepath);
    }

    public FileBufferView(int heigth, int witdh, Layout parent, Point leftUpperCorner, String filepath) {
        super(heigth, witdh, parent, leftUpperCorner);
        this.file = new File(filepath);

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

    public void show() {

    }
}
