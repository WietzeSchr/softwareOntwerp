public abstract class FileSystemLeaf extends FileSystemEntry{

    public FileSystemLeaf(String path) {
        super(path);
    }

    public FileSystemLeaf(String path, FileSystemNode parent) {
        super(path, parent);
    }
}
