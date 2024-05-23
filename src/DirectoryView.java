import java.io.FileNotFoundException;
import java.io.IOException;

/* *************************
 *   DIRECTORY VIEW CLASS  *
 * *************************/
public class DirectoryView extends View{

    private final FileSystemNode fileSystemNode;

    private int line;

    private final LayoutManager manager;

    /* ***************
     *  CONSTRUCTORS *
     *****************/

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

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

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

    /* ******************
     *  INSPECT CONTENT *
     * ******************/
    @Override
    public void move(Direction dir) {
        if (dir == Direction.SOUTH) {
            setLine(getLine() + 1);
        }
        if (dir == Direction.NORD) {
            setLine(getLine() - 1);
        }
    }

    /* **************
     *  OPEN ENTRY  *
     * **************/

    /**
     * This method opens the selected entry by replacing this view with a new view
     * @param newLine   Line seperator used to read files, if file should be read from disc
     */
    @Override
    public void enterPressed(String newLine) throws FileNotFoundException {
        FileSystemEntry entry = getFileSystemNode().getEntry(getLine());
        Buffer buffer = null;
        if (entry != null) {
            String path = entry.getPathString();
            buffer = openFile(path, newLine);
        }
        View newView = getFileSystemNode().openEntry(getManager(), getLine(), buffer, newLine);
        getManager().replace(this, newView);
    }

    /**
     * This method opens a buffer for when a file should be opened. It first checks if there already exists a
     * buffer with the same path, if not a new filebuffer is opened
     * @param path      The absolute path of the file
     * @param newLine   The line separator to read files
     * @return          Buffer
     */
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


    /* ******************
     *   CLOSE VIEW     *
     * ******************/

    @Override
    public View closeView(int focus, CompositeLayout parent) {
        if (getPosition() == focus) {
            getFileSystemNode().getRoot().close();
            return null;
        }
        return this;
    }

    @Override
    public View closeView(int focus, CompositeLayout parent, TerminalInterface printer) {
        if (getPosition() == focus) {
            getFileSystemNode().getRoot().close();
            return null;
        }
        return this;
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

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

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    public Buffer findBuffer(String path) {
        return getManager().getBufferByName(path);
    }

    @Override
    public Point getCursor() {
        return getLeftUpperCorner().add(new Point(getLine() - 1, 0));
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
}
