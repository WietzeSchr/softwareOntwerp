public abstract class DirEntry {

    private final Path absPath;

    private Directory parent = null;

    public DirEntry(String path) {
        this.absPath = new Path(path);
    }

    public DirEntry(String path, Directory parent) {
        this.absPath = new Path(path);
        this.parent = parent;
    }

    public Path getPath() {
        return absPath;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory newParent) {
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

    @Override
    public abstract String toString();

}
