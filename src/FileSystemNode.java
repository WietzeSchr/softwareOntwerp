import java.io.FileNotFoundException;
import java.util.ArrayList;

/* *************************
 *  FILESYSTEM NODE CLASS  *
 * *************************/
public abstract class FileSystemNode extends FileSystemEntry {

    private FileSystemEntry[] subNodes;

    /* ***************
     *  CONSTRUCTORS *
     *****************/
    public FileSystemNode(Path absPath) {
        super(absPath);
        this.subNodes = readNode();
    }

    public FileSystemNode(Path absPath, FileSystemNode parent) {
        super(absPath);
        this.subNodes = readNode();
    }

    public FileSystemNode(Path absPath, FileSystemEntry[] entries) {
        super(absPath);
        for (int i = 0; i < entries.length; i++) {
            if (entries[i] != null) {
                entries[i].setParent(this);
            }
        }
        this.subNodes = entries;
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    public FileSystemEntry[] getEntries() {
        return subNodes;
    }

    void setSubNodes(FileSystemEntry[] newEntries) {
        this.subNodes = newEntries;
    }

    protected FileSystemEntry getEntry(int line) {
        return getEntries()[line - 1];
    }

    /* **************
     *   LOAD DIR   *
     ****************/

    abstract FileSystemEntry[] readNode();

    /* **************
     *  OPEN ENTRY  *
     * **************/

    protected abstract View openEntry(LayoutManager manager, int line, Buffer buffer, String newLine) throws FileNotFoundException;

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/
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

    /* **************
     *  OPEN ENTRY  *
     * **************/

    @Override
    public View open(LayoutManager manager, Buffer buffer, String newLine) throws FileNotFoundException {
        return new DirectoryView(5, 5, new Point(1,1), this, manager);
    }

    protected abstract void saveToBuffer();

    protected void saveToBuffer(Buffer.SaveEdit edit) {
        return;
    }

    /* ******************
     *    CLOSE ENTRY   *
     * ******************/

    protected abstract void close();

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    public FileSystemNode getRoot() {
        if (getParent() == null) {
            return this;
        }
        return getParent().getRoot();
    }
}
