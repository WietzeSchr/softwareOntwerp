import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

public abstract class FileSystemNode extends FileSystemEntry {

    private final FileSystemNode[] subNodes;

    private final FileSystemLeaf[] leaves;

    public FileSystemNode(String absPath, String delimiter) {
        super(absPath, delimiter);
        this.subNodes = readSubNodes();
        this.leaves = readLeaves();
    }

    public FileSystemNode(String absPath, String delimiter, FileSystemNode parent) {
        super(absPath, delimiter, parent);
        this.subNodes = readSubNodes(parent);
        this.leaves = readLeaves();
    }

    private FileSystemNode[] getSubNodes() {
        return subNodes;
    }

    private FileSystemLeaf[] getLeaves() {
        return leaves;
    }

    public FileSystemEntry[] getEntries() {
        ArrayList<FileSystemEntry> result = new ArrayList<>();
        Collections.addAll(result, getSubNodes());
        Collections.addAll(result, getLeaves());
        return result.toArray(new FileSystemEntry[0]);
    }

    abstract FileSystemNode[] readSubNodes();

    abstract FileSystemNode[] readSubNodes(FileSystemNode parent);

    abstract FileSystemLeaf[] readLeaves();

    protected abstract View openEntry(LayoutManager manager, int line, FileBuffer buffer, String newLine) throws FileNotFoundException;

    protected FileSystemEntry getEntry(int line) {
        return getEntries()[line - 1];
    }

    protected abstract String[] makeContent();
}
