import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

public class JsonObject extends FileSystemNode {

    public JsonObject(String name, FileSystemEntry[] subNodes) {
        super(new JsonPath(name), subNodes);
        for (int i = 0; i < subNodes.length; i++) {
            subNodes[i].addParent(this);
        }
    }

    @Override
    public void addParent(FileSystemNode parent) {
        if (getParent() == null || getParent() != null && getEntries()[0] != parent) {
            addToEntries(parent);
        }
        setParent(parent);
    }

    public void addToEntries(FileSystemEntry entry) {
        ArrayList<FileSystemEntry> entries = new ArrayList<>();
        entries.add(entry);
        Collections.addAll(entries, getEntries());
        setSubNodes(entries.toArray(new FileSystemEntry[0]));
    }

    @Override
    FileSystemEntry[] readNode() {
        return null;
    }

    @Override
    protected View openEntry(LayoutManager manager, int line, Buffer buffer, String newLine) throws FileNotFoundException {
        FileSystemEntry entry = getEntry(line);
        return entry.open(manager, buffer, newLine);
    }

    @Override
    public String toString() {
        return getName() + ".";
    }
}
