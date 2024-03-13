import java.io.FileNotFoundException;

public class File
{
    private final String path;

    private FileBuffer buffer;

    public File(String path, String newLine) throws FileNotFoundException {
        this.path = path;
        this.buffer = new FileBuffer(path, newLine);
    }

    public File(String path, FileBuffer buffer) {
        this.path = path;
        this.buffer = buffer;
    }

    public String getPath()
    {
        return this.path;
    }

    public FileBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(FileBuffer newBuffer) {
        this.buffer = newBuffer;
    }

    public String[] getContent() {
        return getBuffer().getContent();
    }

    public void addNewChar(char c) {
        FileBuffer buffer = getBuffer();
        buffer.addNewChar(c);
        setBuffer(buffer);
    }
}
