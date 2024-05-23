import java.io.FileNotFoundException;
import java.io.IOException;

/* *************************
 *  FILESYSTEM LEAF CLASS  *
 * *************************/
public abstract class FileSystemLeaf extends FileSystemEntry{

    /* ***************
     *  CONSTRUCTORS *
     *****************/

    /**
     * This constructor creates a new FileSystemLeaf
     * @param path  The absolute path of the FileSystemLeaf
     */
    public FileSystemLeaf(Path path) {
        super(path);
    }

    /**
     * This constructor creates a new FileSystemLeaf
     * @param path      The absolute path of the FileSystemLead
     * @param parent    The parent of this leaf
     */
    public FileSystemLeaf(Path path, FileSystemNode parent) {
        super(path, parent);
    }

    /**
     * This method loads the content of this leaf as a string array
     * @param newLine   The line seperator to use
     * @return          String[]    content
     */
    public abstract String[] load(String newLine) throws FileNotFoundException;

    /**
     * This method saves new content to this leaf
     * @param newLine       The line seperator to use
     * @param content       The content to be saved
     * @param edits         The edits that were made
     */
    public void save(String newLine, String[] content, Buffer.Edit[] edits) throws IOException {}

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    /**
     * This method returns the root of the FileSystem
     * @return  FileSystemNode root
     */
    public FileSystemNode getRoot() {
        return getParent().getRoot();
    }
}
