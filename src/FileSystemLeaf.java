public abstract class FileSystemLeaf extends FileSystemEntry{

    public FileSystemLeaf(String path, String delimiter) {
        super(path, delimiter);
    }

    public FileSystemLeaf(String path, String delimiter, FileSystemNode parent) {
        super(path, delimiter, parent);
    }
}
