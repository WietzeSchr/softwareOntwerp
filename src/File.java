import java.io.*;
import java.util.ArrayList;

/************
 *  FILE    *
 ************/
public class File extends FileSystemLeaf
{

    /* **************
     *  CONSTRUCTOR *
     ****************/

    /** 
     * This constructor creates a new file with the given path and newLine
     * and sets the buffer to a new FileBuffer with the given path and newLine.
     * @post | getpath() == path
     * @param path | The path of the file
    */
    public File(String path) {
        super(new FilePath(path));
    }

    public File(String path, Directory parent) {
        super(new FilePath(path), parent);
    }

    /* **************
     *  LOAD FILE   *
     ****************/

    /**
     *  This method loads the file and returns the content of the file as a String array
     * @param newLine | The new line separator
     * @return        | String[], The content of the file with the given new line separator
     */
    public String[] load(String newLine) throws FileNotFoundException {
        ArrayList<String> content = new ArrayList<>();
        FileInputStream file = new FileInputStream(getPathString());
        byte[] newLineBytes = newLine.getBytes();
        int c;
        StringBuilder line = new StringBuilder();
        try {
            while ((c = file.read()) != -1) {
                if (c != 10 && c != 13 && c < 32 || 127 <= c) {
                    throw new RuntimeException("File" + getPath() + "contains an illegal byte");
                } else {
                    if (c != 13 && c != 10) {
                        line.append((char) c);
                    } else if(isLineSeparator(c, newLineBytes, file)){
                        content.add(line.toString());
                        line = new StringBuilder();
                    }
                }
            }
            content.add(line.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content.toArray(new String[0]);
    }

    /* **************
     *  SAVE BUFFER *
     ****************/

    /** 
     * This method saves the buffer of the file to the file and sets the buffer to not dirty
     * @param newLine | The new line separator
     * @param content | The content to save
     * @return        | void
     */
    public void save(String newLine, String[] content) throws IOException {
        FileOutputStream file = new FileOutputStream(getPathString());
        for (int i = 0; i < content.length; i++){
            file.write(content[i].getBytes());
            if (i != content.length - 1) {
                file.write(newLine.getBytes());
            }
        }
        file.close();
    }

    public View open(LayoutManager manager, Buffer buffer, String newLine) throws FileNotFoundException {
        if (buffer == null) {
            return new FileBufferView(5,5,new Point(1,1), getPathString(), newLine);
        }
        return new FileBufferView(5,5, new Point(1,1), buffer);
    }

    /* ******************
     *  HELP FUNCTIONS  *
     ********************/

    @Override
    public void generate(SimpleJsonGenerator generator) {
        generator.generateFile(this);
    }

    /** 
     * This method returns true if the given parameter c is a line separator
     * @return  | boolean
     */
    private boolean isLineSeparator(int c, byte[] lineSep, FileInputStream file) throws IOException {
        if(c != lineSep[0]){return false;}
        if(c == 13) {
            c = file.read();
            return c==10;
        }
        return c==10;
    }

    @Override
    public String toString() {
        return getName();
    }
}
