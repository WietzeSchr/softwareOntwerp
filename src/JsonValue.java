import java.io.FileNotFoundException;

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
        return new String[] {getValue()};
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
            return new FileBufferView(5,5,new Point(1,1), new JsonBuffer(this, newLine));
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
