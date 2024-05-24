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
     * Visible for testing
     */
    Buffer getBuffer() {
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
     *      This method is only used to add parent entry to the entries
     *      therefore this method does not set the parent of the new entry to this object
     *      Visible for testing
     * @param entry The new entry
     */
    void addToEntries(FileSystemEntry entry) {
        ArrayList<FileSystemEntry> entries = new ArrayList<>();
        entries.add(entry);
        Collections.addAll(entries, getEntries());
        setEntries(entries.toArray(new FileSystemEntry[0]));
    }

    /**
     * This method return all entries of the current object (used for reading Directories)
     * @return  FileSystemEntr[]
     */
    @Override
    FileSystemEntry[] readNode() {
        return getEntries();
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
        generator.generateJsonObject(this);
    }

    /**
     * This method generates Json in String[] format for this JsonObject
     * @return  String[]
     *
     * Visible for testing
     */
    String[] generateJson() {
        return SimpleJsonGenerator.generateJson(this);
    }

    /* ********************
     *   SAVE TO BUFFER   *
     * ********************/

    /**
     * This method saves the current JsonObject to the original buffer and adds a saveEdit to this buffer
     * @param edits The edits done on the changed JsonValue
     */
    @Override
    protected void saveToBuffer(Buffer.Edit[] edits) {
        String[] content = generateJson();
        getBuffer().setContent(content);
        getBuffer().setDirty(true);
        getBuffer().addSaveEdit(edits);
    }

    /* ******************
     *    CLOSE ENTRY   *
     * ******************/

    /**
     * This method releases a lock on the original buffer containing the json
     */
    @Override
    public void close() {
        getBuffer().releaseLock();
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    @Override
    public String toString() {
        return getName() + "/";
    }
}
