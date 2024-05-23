import java.io.FileNotFoundException;

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
     * This constructor creates a new DirectoryView
     * Only used for directories
     * @param height            The height of the view
     * @param width             The width of the view
     * @param leftUpperCorner   The left upper corner of the view
     * @param path              The path of the directory
     * @param manager           The layoutManager
     */
    public DirectoryView(int height, int width, Point leftUpperCorner, String path, LayoutManager manager) {
        super(height, width, leftUpperCorner);
        this.fileSystemNode = new Directory(path);
        this.line = 1;
        this.manager = manager;
    }

    /**
     * This constructor creates a new DirectoryView
     * @param height            The height of the view
     * @param width             The width of the view
     * @param leftUpperCorner   The left upper corner of the view
     * @param fileSystemNode    The FileSystemNode
     * @param manager           The layoutManager
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

    /**
     * This method returns the FileSystemNode
     * @return  FileSystemNode
     */
    public FileSystemNode getFileSystemNode() {
        return fileSystemNode;
    }

    /**
     * This method sets the line to the new line index
     * @param newLine   The new line index
     */
    public void setLine(int newLine) {
        if (newLine > 0 && newLine <= getFileSystemNode().getEntries().length) {
            this.line = newLine;
        }
    }

    /**
     * This method returns the line index
     * @return  int
     */
    public int getLine() {
        return line;
    }

    /**
     * This method returns the LayoutManager
     * @return  LayoutManager
     */
    private LayoutManager getManager() {
        return manager;
    }

    /* ******************
     *  INSPECT CONTENT *
     * ******************/

    /**
     * This method moves the selected line
     * @param dir   The direction to move
     */
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
     * buffer with the same path, if not a new fileBuffer is opened
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

    /**
     * This method closed the view
     * @param focus     The position of the view that should be closed
     * @param parent    The parent of the view that should be closed
     * @param printer   Not used in directoryView
     * @return          View
     */
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

    /**
     * This method shows the content of the FileBufferView and the updated scrollbars
     * @return  | String, the content of the FileBufferView
     * Visibile for testing
     */
    @Override
    String[] makeShow() {
        String[] content = getFileSystemNode().makeContent();
        String[] result = new String[getHeigth() - 1];
        for (int i = 0; i < content.length && i < result.length; i++) {
            result[i] = content[i];
        }
        return result;
    }

    /**
     * This method returns the created horizontal scrollbar
     * @return  | String, the horizontal scrollbar
     * Visibile for testing
     */
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

    /**
     * This method returns the created vertical scrollbar
     * @return  | char[], the vertical scrollbar
     * Visibile for testing
     */
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

    /**
     * This method checks if there already exists a buffer with the given path, if so return this buffer
     * Returns null otherwise
     * @param path  The absolute path
     * @return      Buffer || null
     */
    public Buffer findBuffer(String path) {
        return getManager().getBufferByName(path);
    }

    /**
     * This method returns the position of the cursor
     * @return: Point
     */
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
