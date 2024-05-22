import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

/* *************************
 *    JSON OBJECT CLASS    *
 * *************************/
public class JsonObject extends FileSystemNode {

    private Buffer buffer;

    /* ***************
     *  CONSTRUCTORS *
     *****************/
    public JsonObject(String name, FileSystemEntry[] subNodes) {
        super(new JsonPath(name), subNodes);
        for (int i = 0; i < subNodes.length; i++) {
            subNodes[i].addParent(this);
        }
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    private Buffer getBuffer() {
        return buffer;
    }

    protected void setBuffer(Buffer buffer) {
        this.buffer = buffer;
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

    /* ******************
     *  JSON GENERATOR  *
     * ******************/

    @Override
    public void generate(SimpleJsonGenerator generator) {
        generator.generateJsonObject(this);
    }

    /* **************
     *  OPEN ENTRY  *
     * **************/

    @Override
    protected View openEntry(LayoutManager manager, int line, Buffer buffer, String newLine) throws FileNotFoundException {
        FileSystemEntry entry = getEntry(line);
        return entry.open(manager, buffer, newLine);
    }

    /* ********************
     *   SAVE TO BUFFER   *
     * ********************/

    @Override
    protected void saveToBuffer() {
        String[] content = generateJson();
        getBuffer().setContent(content);
        getBuffer().setDirty(true);
    }

    @Override
    protected void saveToBuffer(String text, Buffer.Edit[] edits) {
        String[] content = generateJson();
        getBuffer().setContent(content);
        getBuffer().setDirty(true);
        getBuffer().addSaveEdit(edits);
    }

    /* ******************
     *    CLOSE ENTRY   *
     * ******************/
    @Override
    public void close() {
        getBuffer().releaseLock();
    }

    /* ******************
     *  JSON GENERATOR  *
     * ******************/

    private String[] generateJson() {
        return SimpleJsonGenerator.generateJson(this);
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    @Override
    public String toString() {
        return getName() + ".";
    }
}
