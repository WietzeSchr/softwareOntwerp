import java.io.FileNotFoundException;
import java.io.IOException;

public class DirectoryView extends View{

    private final FileSystemNode fileSystemNode;

    private int line;

    private final LayoutManager manager;

    /**
     * This constructor creates a new View
     *
     * @param height          the height of the view
     * @param width           the width of the view
     * @param leftUpperCorner the left upper corner of the view
     */
    public DirectoryView(int height, int width, Point leftUpperCorner, String path, LayoutManager manager) {
        super(height, width, leftUpperCorner);
        this.fileSystemNode = new Directory(path);
        this.line = 1;
        this.manager = manager;
    }

    /**
     * This constructor creates a new View
     *
     * @param height          the height of the view
     * @param width           the width of the view
     * @param leftUpperCorner the left upper corner of the view
     */
    public DirectoryView(int height, int width, Point leftUpperCorner, FileSystemNode fileSystemNode, LayoutManager manager) {
        super(height, width, leftUpperCorner);
        this.fileSystemNode = fileSystemNode;
        this.line = 1;
        this.manager = manager;
    }

    public FileSystemNode getFileSystemNode() {
        return fileSystemNode;
    }

    public void setLine(int newLine) {
        if (newLine > 0 && newLine <= getFileSystemNode().getEntries().length) {
            this.line = newLine;
        }
    }

    public int getLine() {
        return line;
    }

    private LayoutManager getManager() {
        return manager;
    }

    @Override
    public Layout closeView(int focus, CompositeLayout parent, TerminalInterface printer) throws IOException {
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
    public void addNewLineBreak(String newLine) throws FileNotFoundException {
        FileSystemEntry entry = getFileSystemNode().getEntry(getLine());
        Buffer buffer = null;
        if (entry != null) {
            String path = entry.getPathString();
            buffer = openFile(path, newLine);
        }
        View newView = getFileSystemNode().openEntry(getManager(), getLine(), buffer, newLine);
        getManager().replace(this, newView);
    }

    @Override
    public View closeView(int focus, CompositeLayout parent) {
        if (getPosition() == focus) {
            getFileSystemNode().getRoot().close();
            return null;
        }
        return this;
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
        String[] content = getFileSystemNode().makeContent();
        String[] result = new String[getHeigth() - 1];
        for (int i = 0; i < content.length && i < result.length; i++) {
            result[i] = content[i];
        }
        return result;
    }

    @Override
    String makeHorizontalScrollBar() {
        StringBuilder result = new StringBuilder();
        result.append(getFileSystemNode().getPathString());
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

    public Buffer openFile(String path, String newLine) {
        Buffer result = findBuffer(path);
        if (result != null) {
            return result;
        }
        try {
            return new FileBuffer(path, newLine);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public Buffer findBuffer(String name) {
        Layout root = getRoot();
        return root.getBufferByName(name);
    }
}
