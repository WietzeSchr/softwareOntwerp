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

    @Override
    public View open(LayoutManager manager, FileBuffer buffer, String newLine) throws FileNotFoundException {
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
