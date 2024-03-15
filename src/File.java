import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class File
{
   
    private final String path;

    private FileBuffer buffer;

    /** This constructor creates a new file with the given path and newLine
     *  and sets the buffer to a new FileBuffer with the given path and newLine.
     * @post getpath() == path
     * @post getBuffer().getPath() == path
     * @post getBuffer().getNewLine() == newLine
     * @post getBuffer() == new FileBuffer(path, newLine)
    */
    public File(String path, String newLine) throws FileNotFoundException {
        this.path = path;
        this.buffer = new FileBuffer(path, newLine);
    }

    /** This constructor creates a new file with the given path and buffer
     * @post getpath() == path
     * @post getBuffer() == buffer
     */
    public File(String path, FileBuffer buffer) {
        this.path = path;
        this.buffer = buffer;
    }

    /** This method returns the path of the file.
     * @return: String
     */
    public String getPath(){
        return this.path;
    }

    /** This method returns the buffer of the file.
     * @return: FileBuffer 
     */
    public FileBuffer getBuffer() {
        return buffer;
    }

    /** This method sets the buffer of the file.
     * @post: getBuffer() == newBuffer
     * @return: void
     */
    public void setBuffer(FileBuffer newBuffer) {
        this.buffer = newBuffer;
    }

    /** This method returns the content of the file.
     * @return: String[]
     */
    public String[] getContent() {
        return getBuffer().getContent();
    }

    /** This method adds a new character to the buffer of the file and sets the buffer to the new updated buffer
     * @return: void
     */
    public void addNewChar(char c) {
        FileBuffer buffer = getBuffer();
        buffer.addNewChar(c);
        setBuffer(buffer);
    }

    /** This method saves the buffer of the file to the file and sets the buffer to not dirty
     * @return: void
     */
    public void saveBuffer(String newLine) throws IOException {
        FileOutputStream file = new FileOutputStream(getPath());
        for (int i = 0; i < getContent().length; i++){
            file.write(getContent()[i].getBytes());
            if (i != getContent().length - 1) {
                file.write(newLine.getBytes());
            }
        }
        file.close();
        getBuffer().setDirty(false);
    }
}
