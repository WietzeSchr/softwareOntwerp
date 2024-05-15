import javax.swing.text.FieldView;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DirectoryView extends View{

    private Directory directory;

    private int line;

    /**
     * This constructor creates a new View
     *
     * @param height          the height of the view
     * @param width           the width of the view
     * @param leftUpperCorner the left upper corner of the view
     */
    public DirectoryView(int height, int width, Point leftUpperCorner, String path) {
        super(height, width, leftUpperCorner);
        this.directory = new Directory(path);
        this.line = 1;
    }

    /**
     * This constructor creates a new View
     *
     * @param height          the height of the view
     * @param width           the width of the view
     * @param leftUpperCorner the left upper corner of the view
     */
    public DirectoryView(int height, int width, Point leftUpperCorner) {
        super(height, width, leftUpperCorner);
        this.directory = null;
        this.line = 1;
    }

    public void setDirectory(Directory newDirectory) {
        this.directory = newDirectory;
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setLine(int newLine) {
        if (newLine > 0 && newLine <= getDirectory().getEntries().length) {
            this.line = newLine;
        }
    }

    public int getLine() {
        return line;
    }

    @Override
    public Layout closeView(int focus, CompositeLayout parent) throws IOException {
        return null;
    }

    @Override
    public void move(Direction dir) {
        if (dir == Direction.SOUTH) {
            setLine(getLine() + 1);
        }
        if (dir == Direction.NORD) {
            setLine(getLine() - 1);
        }
    }

    @Override
    public View addNewLineBreak(String newLine) throws FileNotFoundException {
        String str = makeShow()[getLine() - 1];
        if (str.equals("..") || str.charAt(str.length() - 1) == '/') {
            Directory newDir = getDirectory().openDir(getLine() - 1);
            setLine(1);
            setDirectory(newDir);
            return this;
        }
        else {
            FileBuffer buffer = openFile(str, newLine);
            return new FileBufferView(getHeigth(), getWidth(), getLeftUpperCorner(), buffer);
        }
    }

    @Override
    public View[] duplicate() {
        return new View[0];
    }

    @Override
    public long getNextDeadline() {
        return 0;
    }

    @Override
    public long getTick() {
        return 0;
    }

    @Override
    String[] makeShow() {
        String[] content = getDirectory().makeContent();
        String[] result = new String[getHeigth() - 1];
        for (int i = 0; i < content.length && i < result.length; i++) {
            result[i] = content[i];
        }
        return result;
    }

    @Override
    String makeHorizontalScrollBar() {
        StringBuilder result = new StringBuilder();
        result.append(getDirectory().getPathString());
        result.append(" ");
        while (result.length() < getWidth()) {
            result.append("#");
        }
        return result.toString();
    }

    @Override
    char[] makeVerticalScrollBar() {
        char[] result = new char[getHeigth()];
        for (int i = 0; i < result.length; i++) {
            result[i] = '#';
        }
        return result;
    }

    @Override
    public Point getCursor() {
        return getLeftUpperCorner().add(new Point(getLine() - 1, 0));
    }

    public FileBuffer openFile(String name, String newLine) throws FileNotFoundException {
        File file = getDirectory().getFile(name);
        FileBuffer result = findBuffer(name);
        if (result != null) {
            return result;
        }
        return new FileBuffer(file.getPathString(), newLine);
    }

    public FileBuffer findBuffer(String name) {
        return null;
    }
}
