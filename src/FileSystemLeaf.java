import java.io.FileNotFoundException;
import java.io.IOException;

/* *************************
 *  FILESYSTEM LEAF CLASS  *
 * *************************/
public abstract class FileSystemLeaf extends FileSystemEntry{

    /* ***************
     *  CONSTRUCTORS *
     *****************/
    public FileSystemLeaf(Path path) {
        super(path);
    }

    public FileSystemLeaf(Path path, FileSystemNode parent) {
        super(path, parent);
    }

    public abstract String[] load(String newLine) throws FileNotFoundException;

    public abstract void save(String newLine, String[] content) throws IOException;

    public void save(String newLine, String[] content, Buffer.Edit[] edits) throws IOException {}

    /* ******************
     *    CLOSE ENTRY   *
     * ******************/
    protected void close() {};

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    public FileSystemNode getRoot() {
        return getParent().getRoot();
    }
}
