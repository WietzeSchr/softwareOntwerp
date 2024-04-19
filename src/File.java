import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/************
 *  FILE    *
 ************/
public class File
{
   
    private final String path;

    /* **************
     *  CONSTRUCTOR *
     ****************/

    /** 
     * This constructor creates a new file with the given path and newLine
     * and sets the buffer to a new FileBuffer with the given path and newLine.
     * @param path the path of the file
     * @post getpath() == path
    */
    public File(String path) {
        this.path = path;
    }

    /* **********************
     *  GETTERS AND SETTERS *
     ************************/

    /** This method returns the path of the file.
     * @return: String
     */
    public String getPath(){
        return this.path;
    }

    /****************
     *  LOAD FILE   *
     ****************/

    /**
     *  This method loads the file and returns the content of the file as a String array
     * @param newLine the new line separator
     * @return: String[] the content of the file with the given new line separator
     */
    public String[] load(String newLine) throws FileNotFoundException {
        ArrayList<String> content = new ArrayList<>();
        FileInputStream file = new FileInputStream(path);
        byte[] newLineBytes = newLine.getBytes();
        int c;
        StringBuilder line = new StringBuilder();
        int column = 1;
        try {
            while ((c = file.read()) != -1) {
                if (c != 10 && c != 13 && c < 32 || 127 <= c) {
                    throw new RuntimeException("File" + path + "contains an illegal byte");
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
     * @param newLine the new line separator
     * @param content the content to save
     * @return: void
     */
    public void save(String newLine, String[] content) throws IOException {
        FileOutputStream file = new FileOutputStream(getPath());
        for (int i = 0; i < content.length; i++){
            file.write(content[i].getBytes());
            if (i != content.length - 1) {
                file.write(newLine.getBytes());
            }
        }
        file.close();
    }

    /* ******************
     *  HELP FUNCTIONS  *
     ********************/

    /** 
     * This method returns true if the given parameter c is a line separator
     * @return: boolean
     */
    private boolean isLineSeparator(int c, byte[] lineSep, FileInputStream file) throws IOException {
        if(c != lineSep[0]){return false;}
        if(c == 13) {
            c = file.read();
            return c==10;
        }
        return c==10;
    }
}
