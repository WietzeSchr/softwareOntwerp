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

    /**
     * This constructor creates a new JsonObject
     * @param path      The path of the JsonObject
     * @param subNodes  The array of properties of this json object (should all be other JsonObjects or JsonValues)
     */
    public JsonObject(String path, FileSystemEntry[] subNodes) {
        super(new JsonPath(path), subNodes);
        for (int i = 0; i < subNodes.length; i++) {
            subNodes[i].addParent(this);
        }
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    /**
     * This method returns the original buffer from where the json object was parsed
     * @return  Buffer, returns null if this is not the root object
     */
    private Buffer getBuffer() {
        return buffer;
    }

    /**
     * This method sets the of this JsonObject to the new buffer
     * @param buffer    The new buffer
     */
    protected void setBuffer(Buffer buffer) {
        this.buffer = buffer;
    }

    /**
     * This method sets the parent of this object to the given parent and adds it to the entries of this object
     * @param parent    The new parent of this JsonObject
     */
    @Override
    public void addParent(FileSystemNode parent) {
        setParent(parent);
        if (getEntries()[0] != parent) {
            addToEntries(parent);
        }
    }

    /**
     * This method adds a new entry to the start of the entries list
     * @param entry The new entry
     */
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
