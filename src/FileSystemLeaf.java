import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class FileSystemLeaf extends FileSystemEntry{

    public FileSystemLeaf(Path path) {
        super(path);
    }

    public FileSystemLeaf(Path path, FileSystemNode parent) {
        super(path, parent);
    }

    public abstract String[] load(String newLine) throws FileNotFoundException;

    public abstract void save(String newLine, String[] content) throws IOException;
}
