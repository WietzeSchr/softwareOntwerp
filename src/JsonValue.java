import java.io.FileNotFoundException;

public class JsonValue extends FileSystemLeaf {

    public JsonValue(String name) {
        super(name);
    }

    public JsonValue(String name, FileSystemNode parent) {
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
}
