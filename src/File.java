public class File
{
    private final String path;

    private FileBuffer buffer;

    private FileBufferView bufferView;

    public File(String path)
    {
        this.path = path;
        this.buffer = new FileBuffer(path);
        this.bufferView = new FileBufferView(buffer);
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

    public FileBufferView getBufferView() {
        return bufferView;
    }

    public void setBufferView(FileBufferView newBufferView) {
        this.bufferView = newBufferView;
    }

}
