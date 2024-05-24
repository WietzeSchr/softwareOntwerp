import java.io.FileNotFoundException;
import java.util.ArrayList;

/* *************************
 *    JSON VALUE CLASS     *
 * *************************/
public class JsonValue extends FileSystemLeaf {

    private Point location;

    private String value;

    /* ***************
     *  CONSTRUCTORS *
     *****************/

    /**
     * This constructor creates a new JsonValue
     *
     * @param path      The path of the JsonValue
     * @param value     The string content of the value
     * @param location  The start location of the string in the original JsonObject
     */
    public JsonValue(String path, String value, Point location) {
        super(new JsonPath(path));
        this.location = location;
        this.value = value;
    }

    /**
     * This constructor creates a new JsonValue
     *
     * @param path      The path of the JsonValue
     * @param value     The string content of the value
     * @param location  The start location of the string in the original JsonObject
     * @param parent    The File System Node that contains this value | should be JsonObject
     */
    public JsonValue(String path, String value, Point location, FileSystemNode parent) {
        super(new JsonPath(path), parent);
        this.location = location;
        this.value = value;
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    /**
     * This method returns the location of this value in the original Json file
     * @return  Point
     */
    public Point getLocation() {
        return location;
    }

    /**
     * This method sets the location of this value to the new location
     * @param newLocation   The new loation of the value
     */
    public void setLocation(Point newLocation) {
        this.location = newLocation;
    }

    /**
     * This method return the string of this Json Value
     * @return  String
     */
    public String getValue() {
        return value;
    }

    /**
     * This method sets the String value of this JsonValue to the new Value
     * @param newValue  The new string
     */
    public void setValue(String newValue) {
        this.value = newValue;
    }

    /* **************
     *  LOAD VALUE  *
     ****************/

    /**
     * This method loads the Json Value as an array of strings, splitting the original string in to multiple lines
     * @param newLine   The line seperator to read the original string
     * @return          String[]
     */
    public String[] load(String newLine) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        char[] value = getValue().toCharArray();
        for (int i = 0; i  < value.length; i++) {
            if (value[i] == '\n') {
                if (newLine.equals("\r\n")) throw new IllegalArgumentException("Illegal linebreak");
                result.add(line.toString());
                line = new StringBuilder();
            }
            else if (value[i] == '\r') {
                if (newLine.equals("\n")) throw new IllegalArgumentException("Illegal linebreak");
                result.add(line.toString());
                i++;
                line = new StringBuilder();
            }
            else if (value[i] == '\"') {
                line.append('"');
            }
            else {
                line.append(value[i]);
            }
        }
        if (line.length() > 0) result.add(line.toString());
        return result.toArray(new String[0]);
    }

    /* **************
     *  SAVE BUFFER *
     ****************/

    /**
     * This method saves new content to this JsonValue and updates the original buffer containing the json object
     * @param newLine   The line seperator added to the content to get a single string
     * @param content   The content as an array of strings
     * @param edits     The list of edits made in the Json Buffer. Used for save edit
     */
    @Override
    public void save(String newLine, String[] content, Buffer.Edit[] edits) {
        StringBuilder text = new StringBuilder();
        for (Buffer.Edit edit : edits) {
            edit.mapToStringLocation(newLine);
            edit.mapToStart(newLine, getLocation());
        }
        for (int i = 0; i < content.length; i++) {
            text.append(content[i]);
            if (i != content.length - 1) {
                text.append(newLine);
            }
        }
        setValue(text.toString());
        getRoot().saveToBuffer(edits);
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
        generator.generateJsonValue(this);
    }

    /* **************
     *  OPEN ENTRY  *
     * **************/


    /**
     * This method opens a new view for this JsonValue
     * @param manager   The Layout Manager, not used for JsonValue
     * @param buffer    JsonBuffer if there already was a view on this JsonValue
     * @param newLine   The line seperator used to read the string of this JsonValue
     * @return          The opened view
     */
    @Override
    public View open(LayoutManager manager, Buffer buffer, String newLine) throws FileNotFoundException {
        if (buffer == null) {
            return new FileBufferView(5,5,new Point(1,1), new JsonBuffer(this, newLine));
        }
        return new FileBufferView(5,5,new Point(1,1), buffer);
    }

    @Override
    public void close() {
        getRoot().close();
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    @Override
    public String toString() {
        return getName();
    }
}
