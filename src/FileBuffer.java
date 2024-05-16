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

    private final FileBufferListenerService listenerService = new FileBufferListenerService();

    /* ***************
     *  CONSTRUCTORS *
     *****************/

    /**
     * This constructor creates a new FileBuffer object with the given path and newLine
     * @param path    | The path of the file
     * @param newLine | The new line separator
     * @post  | getPath() == path
     * @post  | getContent() == getFile().load(newLine)
     * @post  | getDirty() == false
     */
    public FileBuffer(String path, String newLine) throws FileNotFoundException {
        this.file = new File(path);
        this.content = getFile().load(newLine);
        this.dirty = false;
    }

    /**
     * This constructor creates a new FileBuffer object with the given content and path
     * @param content  | The content of the file
     * @param path     | The path of the file
     * @post | getPath() == path
     * @post | getContent() == content
     * @post | getDirty() == false
     */
    public FileBuffer(String[] content, String path) {
        this.file = new File(path);
        this.content = content;
        this.dirty = false;
    }

    /* **********************
     *  GETTERS AND SETTERS *
     ************************/
    /**
     * This method sets the file of the buffer to the given parameter newFile
     * @post  | getFile() == newFile
     * @param newFile | the new file
     * @return        | void
     */
    public void setFile(File newFile) {
        this.file = newFile;
    }
        //  Kan handig zijn voor saven naar een andere file als waar van gelezen is (save as)

    /** 
     * This method returns the file of the buffer
     * @return  | File, file of the buffer
     */
    public File getFile() {
        return file;
    }

    /** 
     * This method returns the path of the file of the buffer
     * @return  | String, path of the file
     */
    public String getPath() {
        return getFile().getPathString();
    }

    /** 
     * This method returns true if the buffer is dirty and returns false if the buffer is not dirty
     * @return  | boolean, true if the buffer is dirty, false if the buffer is not dirty
     */
    public boolean getDirty() {
        return dirty;
    }

    /** 
     * This method sets the buffer boolean value from parameter dirty
     * @post  | getDirty() == dirty
     * @param dirty | The new value of dirty, true if the buffer is dirty, false if the buffer is not dirty
     * @return  | void
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /** 
     * This method returns the content of the buffer
     * @return  | String[], content of the buffer
     */
    public String[] getContent() {
        return content;
    }

    /** 
     * This method sets the content of the buffer to the given parameter newContent
     * @post    | getContent() == newContent
     * @return  | void
     */
    public void setContent(String[] newContent) {
        this.content = newContent;
    }

    /* *********************
     *  DERIVED ATTRIBUTES *
     ***********************/

    /** 
     * This method returns the number of rows 
     * @return  | int, number of rows
     */
    public int getRowCount() {
        return getContent().length;
    }

     /** 
      * This method returns the number of columns, this is the length of the longest row
      * @return  | int, number of columns
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
 
    /** 
     * This method returns the number of characters in the buffer
     * @return  | int, number of characters in the buffer
     */
    public int countCharacters() {
        int result = 0;
        for (String row : getContent()) {
            result += row.length();
        }
        return result;
    }

    /* ***************
     *   OBSERVER    *
     * ***************/

    public void subscribeView(FileBufferView view) {
        listenerService.addInsertionListener(new InsertionListener(view));
        listenerService.addDeletionListener(new DeletionListener(view));
        listenerService.addSaveListener(new SaveListener(view));
    }

    public void unSubscribeView(FileBufferView view) {
        listenerService.removeInsertionListener(view);
        listenerService.removeDeletionListener(view);
        listenerService.removeSaveListener(view);
    }

    private void fireNewChar(Point insert) {
        listenerService.fireNewChar(insert);
    }

    private void fireNewLineBreak(Point insert) {
        listenerService.fireNewLineBreak(insert);
    }

    private void fireDelChar(Point insert) {
        listenerService.fireDelChar(insert);
    }

    private void fireDelLineBreak(Point insert) {
        listenerService.fireDelLineBreak(insert);
    }

    private void fireSaved() {
        listenerService.fireSaved();
    }

    /* **********************
     *  EDIT BUFFER CONTENT *
     ************************/

    /** 
     * This method inserts a line break at the insertion point and sets the buffer to dirty
     * @post    | getDirty() == true
     * @return  | void
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
        fireNewLineBreak(insert);
    }


    /** 
     * This method adds a new character to the buffer and sets the buffer to dirty and moves the insertion point
     * @post | getDirty() == true
     * @param c      | The character to add
     * @param insert | The insertion point
     * @return       | void
     */
    public void addNewChar(char c, Point insert) {  //  Hier geeft substring soms een CheckBoundsBeginEnd Error !
        String[] content = getContent();
        if (content.length == 0)
        {
            content = new String[1];
            content[0] = String.valueOf(c);
        }
        else if (insert.getX() > getRowCount()) {
            content = new String[getRowCount() + 1];
            for (int i = 0; i < getRowCount(); i++) {
                content[i] = getContent()[i];
            }
            content[getRowCount()] = String.valueOf(c);
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
        fireNewChar(insert);
    }

    /** 
     * This method deletes a character from the buffer and sets the buffer to dirty and moves the insertion point
     * @post  | getDirty() == true
     * @param insert | the insertion point
     * @return       | void
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
            fireDelLineBreak(insert);
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
            fireDelChar(insert);
        }
        setContent(newContent);
        setDirty(true);
    }

    /* ******************
     *    SAVE BUFFER   *
     * ******************/

    /**
     * This method saves the buffer to the file with the given newLine and sets the buffer to not dirty
     * @post | getDirty() == false
     * @param newLine | The new line separator to save the file with
     * @return        | void
     */
    public void saveBuffer(String newLine) throws IOException {
        getFile().save(newLine, getContent());
        setDirty(false);
        fireSaved();
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    /** 
     * This method sets the insertion point of the buffer to the given parameter insertionPoint
     * @post    | getInsertionPoint() == insertionPoint
     * @return  | void
     */
    public Point getNewInsertionPoint(Point insertionPoint) {
        if (insertionPoint.getX()<1 || insertionPoint.getY()<1){
            return null;
        }
        if(insertionPoint.getX() > getRowCount()){
            insertionPoint = new Point(getRowCount(), insertionPoint.getY());
        }
        int currRowLength = content[insertionPoint.getX()-1].length();
        if (insertionPoint.getY() > currRowLength){
            insertionPoint = new Point(insertionPoint.getX(), currRowLength+1);
        }
        return insertionPoint;
    }
}
