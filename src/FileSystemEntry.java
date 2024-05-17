import java.io.FileNotFoundException;

public abstract class FileSystemEntry {

    private final Path absPath;

    private FileSystemNode parent = null;

    public FileSystemEntry(String path, char delimiter) {
        this.absPath = new Path(path, delimiter);
    }

    public FileSystemEntry(String path, char delimiter, FileSystemNode parent) {
        this.absPath = new Path(path, delimiter);
        this.parent = parent;
    }

    public Path getPath() {
        return absPath;
    }

    public FileSystemNode getParent() {
        return parent;
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

    public abstract View open(LayoutManager manager, FileBuffer buffer, String newLine) throws FileNotFoundException;

    public abstract String toString();
}
