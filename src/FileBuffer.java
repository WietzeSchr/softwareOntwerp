import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/* *************
 *  FILEBUFFER *
 ***************/
public class FileBuffer {
    private String[] content;

    private File file;

    private boolean dirty;

    /*****************
     *  CONSTRUCTORS *
     *****************/

    public FileBuffer(String path, String newLine) throws FileNotFoundException {
        this.file = new File(path);
        this.content = getFile().load(newLine);
        this.dirty = false;
    }

    public FileBuffer(String[] content, String path) {
        this.file = new File(path);
        this.content = content;
        this.dirty = false;
    }

    /* **********************
     *  GETTERS AND SETTERS *
     ************************/
    public void setFile(File newFile) {
        this.file = newFile;
    }
        //  Kan handig zijn voor saven naar een andere file als waar van gelezen is (save as)

    public File getFile() {
        return file;
    }

    public String getPath() {
        return getFile().getPath();
    }

    /** This method returns true if the buffer is dirty and returns false if the buffer is not dirty
     * @return: boolean
     */
    public boolean getDirty() {
        return dirty;
    }

    /** This method sets the buffer boolean value from parameter dirty
     * @post getDirty() == dirty
     * @return: void
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /** This method returns the content of the buffer
     * @return: String[]
     */
    public String[] getContent() {
        return content;
    }

    /** This method sets the content of the buffer to the given parameter newContent
     * @post getContent() == newContent
     * @return: void
     */
    public void setContent(String[] newContent) {
        this.content = newContent;
    }

    /* *********************
     *  DERIVED ATTRIBUTES *
     ***********************/

    /** This method returns the number of rows 
     * @return: int
     */
    public int getRowCount() {
        return getContent().length;
    }

     /** This method returns the number of columns 
     * @return: int
     */
    public int getColumnCount() {
        String[] cont = getContent();
        int result = 1;
        for (int i = 0; i < getRowCount(); i++) {
            if (cont[i] != null) {
                if (cont[i].length() > result) {
                    result = cont[i].length();
                }
            }
        }
        return result;
    }
 
    /** This method returns the number of characters in the buffer
     * @return: int
     */
    public int countCharacters() {
        int result = 0;
        for (String row : getContent()) {
            result += row.length();
        }
        return result;
    }

    /* **********************
     *  EDIT BUFFER CONTENT *
     ************************/

    /** This method inserts a line break at the insertion point and sets the buffer to dirty
     * @post getDirty() == true
     * @return: void
     */
    public void insertLineBreak(Point insert){
        int row = insert.getX()-1;
        int col = insert.getY()-1;
        ArrayList<String> cont = new ArrayList<String>(Arrays.asList(getContent()));
        String currentRow = getContent()[row];
        String firstPart = currentRow.substring(0, col);
        String secondPart = currentRow.substring(col);
        cont.set(row, firstPart);
        cont.add(row + 1, secondPart);
        setContent(cont.toArray(new String[0]));
        setDirty(true);
    }


    /** This method adds a new character to the buffer and sets the buffer to dirty and moves the insertion point
     * @post getDirty() == true
     * @return: void
     */
    public void addNewChar(char c, Point insert) {
        String[] content = getContent();
        if (content.length == 0)
        {
            content = new String[1];
            content[0] = String.valueOf(c);
        }
        else {
            String row = content[insert.getX() - 1];
            if (row == null) {
                row = String.valueOf(c);
                content[insert.getX() - 1] = row;
            }
            else {
                StringBuilder eRow = new StringBuilder();
                for (int i = 0; i < row.length(); i++) {
                    if (i == insert.getY() - 1) {
                        eRow.append(c);
                    }
                    eRow.append(row.toCharArray()[i]);
                }
                if (insert.getY() > row.length()) {
                    eRow.append(c);
                }
                content[insert.getX() - 1] = eRow.toString();
            }
        }
        setContent(content);
        setDirty(true);
    }

    /** This method deletes a character from the buffer and sets the buffer to dirty and moves the insertion point
     * @post getDirty() == true
     * @return: void
     */
    public void deleteChar(Point insert) {
        String[] content = getContent();
        String[] newContent;
        if (insert.getY() == 1)  {
            if(insert.getX() == 1) return;
            newContent = new String[content.length - 1];
            int j = 0;
            for (int i = 0; i < content.length; i++) {
                if (i != insert.getX() - 1) {
                    newContent[j] = content[i];
                    j++;
                }
                else {
                    String secondPart = String.copyValueOf(content[i].toCharArray());
                    String firstPart = String.copyValueOf(content[i - 1].toCharArray());
                    newContent[i - 1] = firstPart + secondPart;
                }
            }
        }
        else {
            newContent = new String[content.length];
            for (int i = 0; i < content.length; i++) {
                if (i != insert.getX() - 1) {
                    newContent[i] = String.copyValueOf(content[i].toCharArray());
                }
                else {
                    StringBuilder newRow = new StringBuilder();
                    for (int j = 0; j < content[i].length(); j++) {
                        if (j != insert.getY() - 2) {
                            newRow.append(content[i].toCharArray()[j]);
                        }
                    }
                    newContent[i] = newRow.toString();
                }
            }
        }
        setContent(newContent);
        setDirty(true);
    }

    /* ******************
     *    SAVE BUFFER   *
     * ******************/

    public void saveBuffer(String newLine) throws IOException {
        getFile().save(newLine, getContent());
        setDirty(false);
    }

    /* ******************
     *   UNDO / REDO    *
     * ******************/



    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    /** This method sets the insertion point of the buffer to the given parameter insertionPoint
     * @post getInsertionPoint() == insertionPoint
     * @return: void
     */
    public Point getNewInsertionPoint(Point insertionPoint) {
        if (insertionPoint.getX()<1 || insertionPoint.getY()<1 || insertionPoint.getX() > content.length){
            return null;
        }
        //spring naar laatste character van target lijn
        int currRowLength = content[insertionPoint.getX()-1].length();
        if (insertionPoint.getY() > currRowLength){
            insertionPoint = new Point(insertionPoint.getX(), currRowLength+1);
        }
        return insertionPoint;
    }
}
