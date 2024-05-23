import java.util.ArrayList;
import java.util.Objects;

/* ***********************
 *    DIRECTORY CLASS    *
 * ***********************/
public class Directory extends FileSystemNode {

    /* ***************
     *  CONSTRUCTORS *
     *****************/

    /**
     * This constructor creates a new directory
     * @param absPath   The absolute path of the directory
     */
    public Directory(String absPath) {
        super(new FilePath(absPath));
    }

    /**
     * This constructor creates a new subdirectory
     * @param absPath   The absolute path of the directory
     * @param parent    The parent of the directory
     */
    public Directory(String absPath, Directory parent) {
        super(new FilePath(absPath), parent);
    }

    /**
     * This method reads the directory, used for initializing directory entries
     * @return  FilSystemEntry[]    The entries of the directory (first entry is parent if this directory had a parent)
     */
    @Override
    FileSystemEntry[] readNode() {
        ArrayList<FileSystemEntry> subNodes = new ArrayList<>();
        String absDirPath = getPathString();
        java.io.File[] files = new java.io.File(absDirPath).listFiles();
        if (getParentPath() != null) {
            subNodes.add(getParent());
        }
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            if (files[i].isDirectory()) {
                subNodes.add(new Directory(files[i].getAbsolutePath() + "/", this));
            }
            if (files[i].isFile()) {
                subNodes.add(new File(files[i].getAbsolutePath(), this));
            }
        }
        return subNodes.toArray(new FileSystemEntry[0]);
    }

    /* ******************
     *  JSON GENERATOR  *
     * ******************/

    /**
     * Method needed for compiling without SDK 19 preview 3 (handling switch in JsonGenerator)
     * @param generator     The generator
     */
    @Override
    public void generate(SimpleJsonGenerator generator) {
        generator.generateDir(this);
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    @Override
    public String toString() {
        return getName() + "/";
    }
}
