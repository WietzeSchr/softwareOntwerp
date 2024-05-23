import java.io.FileNotFoundException;

/* *************************
 * FILESYSTEM ENTRY CLASS  *
 * *************************/
public abstract class FileSystemEntry {     // Can be either a Directory, File, JsonObject or JsonValue

    private final Path absPath;

    private FileSystemNode parent = null;

    /* ***************
     *  CONSTRUCTORS *
     *****************/

    /**
     * This constructor creates a new FileSystemEntry
     * @param path  The absolute path of the entry
     */
    public FileSystemEntry(Path path) {
        this.absPath = path;
    }

    /**
     * This constructor creates a new FileSystemEntry
     * @param path      The absolute path of the entry
     * @param parent    The FileSystemNode containing this entry
     */
    public FileSystemEntry(Path path, FileSystemNode parent) {
        this.absPath = path;
        this.parent = parent;
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    /**
     * This method returns the path
     * @return  Path    The path object
     */
    private Path getPath() {
        return absPath;
    }

    /**
     * This method returns the parent
     * @return  FileSystemNode  parent
     */
    public FileSystemNode getParent() {
        return parent;
    }

    /**
     * This method adds sets the parent to the new parent
     * @param parent    FileSystemNode
     */
    public void addParent(FileSystemNode parent) {
        setParent(parent);
    }

    /**
     * This method adds sets the parent to the new parent
     * @param newParent    FileSystemNode
     */
    public void setParent(FileSystemNode newParent) {
        this.parent = newParent;
    }

    /* **********************
     *  DERIVED ATTRIBUTES  *
     * **********************/

    /**
     * This method returns the absolute path as a string
     * @return  String      The absolute path
     */
    public String getPathString() {
        return getPath().getPath();
    }

    /**
     * This method returns the abolute path of the parent as a string
     * @return  String      The parent path | null if this entry has no parent
     */
    public String getParentPath() {
        return getPath().getParentPath();
    }

    /**
     * This method return the entry name
     * @return  String      The name of the entry
     */
    public String getName() {
        return getPath().getName();
    }

    /* **************
     *  OPEN ENTRY  *
     * **************/

    /**
     * This method opens a new view for this entry
     * @param manager   The Layout Manager, needed for a possible new DirectoryView
     * @param buffer    Not null buffer if there already exists a buffer on this entry
     * @param newLine   The line seperator used
     * @return          View    The opened view
     */
    public abstract View open(LayoutManager manager, Buffer buffer, String newLine) throws FileNotFoundException;

    /* ******************
     *    CLOSE ENTRY   *
     * ******************/

    /**
     * This method closes this entry
     */
    protected void close() {}

    /* ******************
     *  JSON GENERATOR  *
     * ******************/

    /**
     * Method needed for compiling without SDK 19 preview 3 (handling switch in JsonGenerator)
     * @param generator     The generator
     */
    public abstract void generate(SimpleJsonGenerator generator);

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    public abstract String toString();
}
