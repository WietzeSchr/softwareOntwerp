import java.io.FileNotFoundException;
import java.util.ArrayList;

/* *************************
 *  FILESYSTEM NODE CLASS  *
 * *************************/
public abstract class FileSystemNode extends FileSystemEntry {

    private FileSystemEntry[] entries;

    /* ***************
     *  CONSTRUCTORS *
     *****************/

    /**
     * This constructor creates a new FileSystemNode
     * Is only used for directories
     * @param absPath   The absolute path of the directory
     */
    public FileSystemNode(Path absPath) {
        super(absPath);
        this.entries = readNode();
    }

    /**
     * This constructor creates a new FileSystemNode
     * Is only used for directories
     * @param absPath   The absolute path of the directory
     * @param parent    The parent of the directory
     */
    public FileSystemNode(Path absPath, FileSystemNode parent) {
        super(absPath);
        setParent(parent);
        this.entries = readNode();
    }

    /**
     * This constructor creates a new FileSystemNode
     * Is only used for JsonObjects
     * @param absPath   The absolute path of the JsonObject
     * @param entries   The properties of the JsonObject    
     *                  These should all be distinct JsonObjects or JsonValues 
     */
    public FileSystemNode(Path absPath, FileSystemEntry[] entries) {
        super(absPath);
        for (int i = 0; i < entries.length; i++) {
            if (entries[i] != null) {
                entries[i].setParent(this);
            }
        }
        this.entries = entries;
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    /**
     * This method returns the entries
     * @return  FileSystemEntry[]   entries
     */
    public FileSystemEntry[] getEntries() {
        return entries;
    }

    /**
     * This method sets the entries to the new entries
     * @param newEntries    The new entries
     */
    void setEntries(FileSystemEntry[] newEntries) {
        this.entries = newEntries;
    }

    /**
     * This method returns the entry at given index
     * @param line  Entry index     One-based integer
     * @return  FileSystemEntry
     */
    protected FileSystemEntry getEntry(int line) {
        return getEntries()[line - 1];
    }

    /* **************
     *   LOAD DIR   *
     ****************/

    /**
     * Only used for directory
     * This method reads the directory, used for initializing directory entries
     * @return  FilSystemEntry[]    The entries of the directory (first entry is parent if this directory had a parent)
     */
    abstract FileSystemEntry[] readNode();

    /* **************
     *  OPEN ENTRY  *
     * **************/

    /**
     * This method opens the entry at the given line
     * @param manager   The LayoutManager, used for when a DirectoryView should be opened
     * @param line      The entry line
     * @param buffer    A buffer on the given entry, null if there is no buffer yet
     * @param newLine   The line seperator used to read files
     * @return          View    The opened view
     */
    protected View openEntry(LayoutManager manager, int line, Buffer buffer, String newLine) throws FileNotFoundException {
        FileSystemEntry entry = getEntry(line);
        if (entry == null && line == 1) {
            return new DirectoryView(5,5, new Point(1,1), getParentPath(), manager);
        }
        return entry.open(manager, buffer, newLine);
    }

    /**
     * This method opens a new DirectoryView on this node
     * @param manager   The Layout Manager
     * @param buffer    Always null
     * @param newLine   The line seperator used, not used
     * @return          View    The opened view
     */
    @Override
    public View open(LayoutManager manager, Buffer buffer, String newLine) throws FileNotFoundException {
        return new DirectoryView(5, 5, new Point(1,1), this, manager);
    }

    /* ******************
     *    SAVE BUFFER   *
     * ******************/

    /**
     * This method is only used for JsonObjects
     * @param edits     The edits made in the JsonObject
     */
    protected void saveToBuffer(Buffer.Edit[] edits) {
        return;
    }


    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    /**
     * This method returns the content of this node
     * @return  String[]    content
     */
    public String[] makeContent() {
        ArrayList<String> result = new ArrayList<>();
        FileSystemEntry[] entries = getEntries();
        for (int i = 0; i < entries.length; i++) {
            if (i == 0 && entries[i] == null || i == 0 && getParent() != null) {
                result.add("..");
            }
            else {
                result.add(entries[i].toString());
            }
        }
        return result.toArray(new String[0]);
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    /**
     * This method returns the root node of the FileSystem
     * @return  FileSystemNode  root
     */
    public FileSystemNode getRoot() {
        if (getParent() == null) {
            return this;
        }
        return getParent().getRoot();
    }
}
