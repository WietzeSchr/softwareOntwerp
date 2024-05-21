import java.io.FileNotFoundException;

/* *************************
 * FILESYSTEM ENTRY CLASS  *
 * *************************/
public abstract class FileSystemEntry {

    private final Path absPath;

    private FileSystemNode parent = null;

    /* ***************
     *  CONSTRUCTORS *
     *****************/
    public FileSystemEntry(Path path) {
        this.absPath = path;
    }

    public FileSystemEntry(Path path, FileSystemNode parent) {
        this.absPath = path;
        this.parent = parent;
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    public Path getPath() {
        return absPath;
    }

    public FileSystemNode getParent() {
        return parent;
    }

    public void addParent(FileSystemNode parent) {
        setParent(parent);
    }

    public void setParent(FileSystemNode newParent) {
        this.parent = newParent;
    }

    /* **********************
     *  DERIVED ATTRIBUTES  *
     * **********************/

    public String getPathString() {
        return getPath().getPath();
    }

    public String getParentPath() {
        return getPath().getParentPath();
    }

    public String getName() {
        return getPath().getName();
    }

    /* **************
     *  OPEN ENTRY  *
     * **************/

    public abstract View open(LayoutManager manager, Buffer buffer, String newLine) throws FileNotFoundException;

    /* ******************
     *  JSON GENERATOR  *
     * ******************/

    public abstract void generate(SimpleJsonGenerator generator);

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    public abstract String toString();
}
