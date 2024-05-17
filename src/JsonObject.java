import java.io.FileNotFoundException;

public class JsonObject extends FileSystemNode {

    public JsonObject(String name) {
        super(name);
    }

    public JsonObject(String name, FileSystemNode parent) {
        super(name, parent);
    }

    @Override
    public View open(LayoutManager manager, FileBuffer buffer, String newLine) throws FileNotFoundException {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    FileSystemNode[] readSubNodes() {
        return new FileSystemNode[0];
    }

    @Override
    FileSystemNode[] readSubNodes(FileSystemNode parent) {
        return new FileSystemNode[0];
    }

    @Override
    FileSystemLeaf[] readLeaves() {
        return new FileSystemLeaf[0];
    }

    @Override
    protected View openEntry(LayoutManager manager, int line, FileBuffer buffer, String newLine) throws FileNotFoundException {
        return null;
    }

    @Override
    protected String[] makeContent() {
        return new String[0];
    }
}
