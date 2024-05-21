import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Buffer {

    private String[] content;

    private FileSystemLeaf file;

    private boolean dirty;

    private final FileBufferListenerService listenerService = new FileBufferListenerService();

    private final JsonEditLock lock = new JsonEditLock();

    public Buffer(FileSystemLeaf file, String newLine) throws FileNotFoundException {
        this.file = file;
        this.dirty = false;
        this.content = file.load(newLine);
    }

    public Buffer(FileSystemLeaf file, String[] content) {
        this.file = file;
        this.dirty = false;
        this.content = content;
    }

    public String[] getContent() {
        return content;
    }

    public void setContent(String[] newContent) {
        this.content = newContent;
    }

    public FileSystemLeaf getFile() {
        return file;
    }

    public void setFile(FileSystemLeaf newFile) {
        this.file = newFile;
    }

    public boolean getDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    private JsonEditLock getLock() {
        return lock;
    }

    public int getRowCount() {
        return getContent().length;
    }

    public int getColumnCount() {
        String[] content = getContent();
        int result = 1;
        for (int i = 0; i < getRowCount(); i++) {
            if (content[i] != null) {
                if (content[i].length() > result) {
                    result = content[i].length();
                }
            }
        }
        return result;
    }

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


    protected void acquireLock() {
        getLock().acquireNewLock();
    }

    protected void releaseLock() {
        getLock().releaseLock();
    }

    public boolean isNotLocked() {
        return !getLock().isLocked();
    }

    /**
     * This method inserts a line break at the insertion point and sets the buffer to dirty
     * @post    | getDirty() == true
     * @return  | void
     */
    public void insertLineBreak(Point insert){

        if (isNotLocked()) {
            int row = insert.getX() - 1;
            int col = insert.getY() - 1;
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
        if (isNotLocked()) {
            String[] content = getContent();
            if (content.length == 0) {
                content = new String[1];
                content[0] = String.valueOf(c);
            } else if (insert.getX() > getRowCount()) {
                content = new String[getRowCount() + 1];
                for (int i = 0; i < getRowCount(); i++) {
                    content[i] = getContent()[i];
                }
                content[getRowCount()] = String.valueOf(c);
            } else {
                String row = content[insert.getX() - 1];
                if (row == null) {
                    row = String.valueOf(c);
                    content[insert.getX() - 1] = row;
                } else {
                    StringBuilder eRow = new StringBuilder();
                    for (int i = 0; i < row.length(); i++) {
                        if (i == insert.getY() - 1) {
                            eRow.append(c);
                        }
                        eRow.append(row.toCharArray()[i]);
                    }
                    if (insert.getY() > row.length()) {
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
                    content[insert.getX() - 1] = eRow.toString();
                }
            }
            setContent(content);
            setDirty(true);
            fireNewChar(insert);
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
        if (isNotLocked()) {
            String[] content = getContent();
            String[] newContent;
            if (insert.getY() == 1) {
                if (insert.getX() == 1) return;
                newContent = new String[content.length - 1];
                int j = 0;
                for (int i = 0; i < content.length; i++) {
                    if (i != insert.getX() - 1) {
                        newContent[j] = content[i];
                        j++;
                    } else {
                        String secondPart = String.copyValueOf(content[i].toCharArray());
                        String firstPart = String.copyValueOf(content[i - 1].toCharArray());
                        newContent[i - 1] = firstPart + secondPart;
                    }
                }
                fireDelLineBreak(insert);
            } else {
                newContent = new String[content.length];
                for (int i = 0; i < content.length; i++) {
                    if (i != insert.getX() - 1) {
                        newContent[i] = String.copyValueOf(content[i].toCharArray());
                    } else {
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
            fireDelChar(insert);

        }
    }

    public void saveBuffer(String newLine) throws IOException {
        getFile().save(newLine, getContent());
        setDirty(false);
        fireSaved();
    }

    public abstract void close();

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

/* *************
 *  FILEBUFFER *
 ***************/
class FileBuffer extends Buffer {
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
        super(new File(path), newLine);
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
        super(new File(path), content);
    }

    @Override
    public void close() {}
}

class JsonBuffer extends Buffer{

    public JsonBuffer(JsonValue value, String newLine) throws FileNotFoundException {
        super(value, newLine);
    }

    @Override
    public void close() {
        getFile().close();
    }
}
