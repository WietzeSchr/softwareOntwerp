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
     * This constructor creates a new file
     * @param path  The absolute path of the file
    */
    public File(String path) {
        super(new FilePath(path));
    }

    /**
     * This constructor creates a new file
     * @param path      The absolute path of the file
     * @param parent    The directory containing this file
     */
    public File(String path, Directory parent) {
        super(new FilePath(path), parent);
    }

    /* **************
     *  LOAD FILE   *
     ****************/

    /**
     * This method loads the file and returns the content of the file as a String array
     * @param newLine   The new line separator
     * @return          String[], The content of the file with the given new line separator
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
                    throw new RuntimeException("File" + getPathString() + "contains an illegal byte");
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
     * @param newLine   The new line separator
     * @param content   The content to save
     */
    public void save(String newLine, String[] content, Buffer.Edit[] edits) throws IOException {
        FileOutputStream file = new FileOutputStream(getPathString());
        for (int i = 0; i < content.length; i++){
            file.write(content[i].getBytes());
            if (i != content.length - 1) {
                file.write(newLine.getBytes());
            }
        }
        file.close();
    }

    /* **************
     *  OPEN ENTRY  *
     * **************/

    /**
     * This method opens a new view for this File
     * @param manager   The Layout Manager, not used for File
     * @param buffer    FileBuffer if there already was a view on this File
     * @param newLine   The line seperator used to read the file from disc
     * @return          View    The opened view
     */
    public View open(LayoutManager manager, Buffer buffer, String newLine) throws FileNotFoundException {
        if (buffer == null) {
            return new FileBufferView(5,5,new Point(1,1), getPathString(), newLine);
        }
        return new FileBufferView(5,5, new Point(1,1), buffer);
    }

    /* ******************
     *  JSON GENERATOR  *
     * ******************/

    /**
     * Method needed for compiling without SDK 19 preview 3 (handling switch in JsonGenerator)
     * @param generator     The generator
     */
    @Override
    public void generate(SimpleJsonGenerator generator) {
        generator.generateFile(this);
    }

    /* ******************
     *  HELP FUNCTIONS  *
     ********************/

    /** 
     * This method returns true if the given parameter c is a line separator
     * @return   boolean
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
