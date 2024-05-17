public abstract class FileSystemLeaf extends FileSystemEntry{

    public FileSystemLeaf(String path, char delimiter) {
        super(path, delimiter);
    }

    public FileSystemLeaf(String path, char delimiter, FileSystemNode parent) {
        super(path, delimiter, parent);
    }
}
