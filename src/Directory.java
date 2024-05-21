import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/* ***********************
 *    DIRECTORY CLASS    *
 * ***********************/
public class Directory extends FileSystemNode {

    /* ***************
     *  CONSTRUCTORS *
     *****************/
    public Directory(String absPath) {
        super(new FilePath(absPath));
    }

    public Directory(String absPath, Directory parent) {
        super(new FilePath(absPath), parent);
    }

    @Override
    FileSystemEntry[] readNode() {
        ArrayList<FileSystemEntry> subNodes = new ArrayList<>();
        Collections.addAll(subNodes, readSubNodes());
        Collections.addAll(subNodes, readLeaves());
        return subNodes.toArray(new FileSystemEntry[0]);
    }

    FileSystemNode[] readSubNodes() {
        String absDirPath = getPathString();
        List<Directory> resultList = new ArrayList<>();
        java.io.File[] files = new java.io.File(absDirPath).listFiles();
        if (getParentPath() != null) {
            resultList.add(null);
        }
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            if (files[i].isDirectory()) {
                resultList.add(new Directory(files[i].getAbsolutePath() + "/", this));
            }
        }
        return resultList.toArray(new Directory[0]);
    }

    FileSystemLeaf[] readLeaves() {
        String absDirPath = getPathString();
        List<File> resultList = new ArrayList<>();
        java.io.File[] flist = new java.io.File(absDirPath).listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isFile()) {
                resultList.add(new File(flist[i].getAbsolutePath(), this));
            }
        }
        return resultList.toArray(new File[0]);
    }

    /* **************
     *  OPEN ENTRY  *
     * **************/

    @Override
    public View openEntry(LayoutManager manager, int line, Buffer buffer, String newLine) throws FileNotFoundException {
        FileSystemEntry entry = getEntry(line);
        if (entry == null) {
            return new DirectoryView(5,5, new Point(1,1), getParentPath(), manager);
        }
        return entry.open(manager, buffer, newLine);
    }

    @Override
    public void generate(SimpleJsonGenerator generator) {
        generator.generateDir(this);
    }

    @Override
    protected void saveToBuffer() {}

    /* ******************
     *    CLOSE ENTRY   *
     * ******************/

    @Override
    protected void close() {}

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    @Override
    public String toString() {
        return getName() + "/";
    }
}
