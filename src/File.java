public class File
{
    private final String path;

    private FileBuffer buffer;

    public File(String path)
    {
        this.path = path;
        this.buffer = new FileBuffer(path);
    }

    public String getPath()
    {
        return this.path;
    }

    public FileBuffer getBuffer() {
        return this.buffer;
    }

    public void setBuffer(FileBuffer newBuffer) {
        this.buffer = newBuffer;
    }
}
