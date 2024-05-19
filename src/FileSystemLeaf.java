public abstract class FileSystemLeaf extends FileSystemEntry{

    public FileSystemLeaf(Path path) {
        super(path);
    }

    public FileSystemLeaf(Path path, FileSystemNode parent) {
        super(path, parent);
    }
}
