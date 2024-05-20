import java.io.FileNotFoundException;

public abstract class FileSystemEntry {

    private final Path absPath;

    private FileSystemNode parent = null;

    public FileSystemEntry(Path path) {
        this.absPath = path;
    }

    public FileSystemEntry(Path path, FileSystemNode parent) {
        this.absPath = path;
        this.parent = parent;
    }

    public Path getPath() {
        return absPath;
    }

    public FileSystemNode getParent() {
        return parent;
    }

    public void addParent(FileSystemNode parent) {
        setParent(parent);
    }

    public void setParent(FileSystemNode newParent) {
        this.parent = newParent;
    }

    public String getPathString() {
        return getPath().getPath();
    }

    public String getParentPath() {
        return getPath().getParentPath();
    }

    public String getName() {
        return getPath().getName();
    }

    public abstract View open(LayoutManager manager, Buffer buffer, String newLine) throws FileNotFoundException;

    public abstract String toString();
}
