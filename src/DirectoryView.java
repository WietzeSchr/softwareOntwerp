import java.io.FileNotFoundException;
import java.io.IOException;

public class DirectoryView extends View{

    private final Directory directory;

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
        this.directory = new Directory(path);
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
    public DirectoryView(int height, int width, Point leftUpperCorner, LayoutManager manager) {
        super(height, width, leftUpperCorner);
        this.directory = null;
        this.line = 1;
        this.manager = manager;
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

    private LayoutManager getManager() {
        return manager;
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
    public void addNewLineBreak(String newLine) throws FileNotFoundException {
        DirEntry entry = getDirectory().getEntry(getLine());
        FileBuffer buffer = null;
        if (entry != null) {
            String path = entry.getPathString();
            buffer = openFile(path, newLine);
        }
        View newView = getDirectory().openEntry(getManager(), getLine(), buffer, newLine);
        getManager().replace(this, newView);
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

    public FileBuffer openFile(String path, String newLine) {
        FileBuffer result = findBuffer(path);
        if (result != null) {
            return result;
        }
        try {
            return new FileBuffer(path, newLine);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public FileBuffer findBuffer(String name) {
        Layout root = getRoot();
        return root.getBufferByName(name);
    }
}
