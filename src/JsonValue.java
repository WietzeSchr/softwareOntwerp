import java.io.FileNotFoundException;
import java.util.ArrayList;

public class JsonValue extends FileSystemLeaf {

    private Point location;

    private String value;

    public JsonValue(String name, String value, Point location) {
        super(new JsonPath(name));
        this.location = location;
        this.value = value;
    }

    public JsonValue(String name, String value, Point location, FileSystemNode parent) {
        super(new JsonPath(name), parent);
        this.location = location;
        this.value = value;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point newLocation) {
        this.location = newLocation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String newValue) {
        this.value = newValue;
    }

    public String[] load(String newLine) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        char[] value = getValue().toCharArray();
        for (int i = 0; i  < value.length; i++) {
            if (value[i] == '\n') {
                result.add(line.toString());
                line = new StringBuilder();
            }
            else if (value[i] == '\r') {
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

    @Override
    public void save(String newLine, String[] content) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < content.length; i++) {
            text.append(content[i]);
            if (i != content.length - 1) {
                text.append(newLine);
            }
        }
        setValue(text.toString());
        getRoot().saveToBuffer();
    }

    @Override
    public void generate(SimpleJsonGenerator generator) {
        generator.generateJsonValue(this);
    }

    @Override
    public View open(LayoutManager manager, Buffer buffer, String newLine) throws FileNotFoundException {
        if (buffer == null) {
            View result = new FileBufferView(5,5,new Point(1,1), new JsonBuffer(this, newLine));
            return result;
        }
        return new FileBufferView(5,5,new Point(1,1), buffer);
    }

    @Override
    protected void close() {
        getRoot().close();
    }

    @Override
    public String toString() {
        return getName();
    }
}
