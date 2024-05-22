import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/* *************
 *  FILEBUFFER *
 ***************/
class FileBuffer extends Buffer {

    /* ***************
     *  CONSTRUCTORS *
     *****************/

    /**
     * This constructor creates a new FileBuffer object with the given path and newLine
     *
     * @param path    | The path of the file
     * @param newLine | The new line separator
     * @post | getPath() == path
     * @post | getContent() == getFile().load(newLine)
     * @post | getDirty() == false
     */
    public FileBuffer(String path, String newLine) throws FileNotFoundException {
        super(new File(path), newLine);
    }

    /**
     * This constructor creates a new FileBuffer object with the given content and path
     *
     * @param content | The content of the file
     * @param path    | The path of the file
     * @post | getPath() == path
     * @post | getContent() == content
     * @post | getDirty() == false
     */
    public FileBuffer(String[] content, String path) {
        super(new File(path), content);
    }

    @Override
    public void close() {
    }
}

class JsonBuffer extends Buffer {

    public JsonBuffer(JsonValue value, String newLine) throws FileNotFoundException {
        super(value, newLine);
    }

    public Edit[] getCurrentEdits() {
        Edit lastEdit = getLastEdit();
        Edit firstEdit = getLastEdit();
        while (! firstEdit.isFirst()) {
            firstEdit = firstEdit.getPrevious();
        }
        ArrayList<Edit> edits = new ArrayList<>();
        while (firstEdit != lastEdit) {
            firstEdit = firstEdit.getNext();
            edits.add(firstEdit);
        }
        return edits.toArray(new Edit[0]);
    }

    @Override
    public void saveBuffer(String newLine) throws IOException {
        Edit[] edits = getCurrentEdits();
        getFile().save(newLine, getContent(), edits);
        setDirty(false);
    }

    @Override
    public void close() {
        getFile().close();
    }
}

public abstract class Buffer {

    private String[] content;

    private FileSystemLeaf file;

    private boolean dirty;

    private final FileBufferListenerService listenerService = new FileBufferListenerService();

    private Edit lastEdit;

    private final JsonEditLock lock = new JsonEditLock();

    public Buffer(FileSystemLeaf file, String newLine) throws FileNotFoundException {
        this.file = file;
        this.dirty = false;
        this.content = file.load(newLine);
        this.lastEdit = new EmptyEdit();
    }

    public Buffer(FileSystemLeaf file, String[] content) {
        this.file = file;
        this.dirty = false;
        this.content = content;
        this.lastEdit = new EmptyEdit();
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

    public void setLastEdit(Edit newLastEdit) {
        this.lastEdit = newLastEdit;
    }

    public Edit getLastEdit() {
        return lastEdit;
    }

    protected void clearEdits() {
        setLastEdit(new EmptyEdit());
    }

    /* **********************
     *  EDIT BUFFER CONTENT *
     ************************/

    public void insertLineBreak(Point insert, Point newInsert) {
        NonEmptyEdit nextEdit = new Insertion((char) 13, insert, newInsert);
        nextEdit.setPrevious(getLastEdit());
        getLastEdit().setNext(nextEdit);
        setLastEdit(nextEdit);
        insertLineBreak(insert);
    }

    /**
     * This method inserts a line break at the insertion point and sets the buffer to dirty
     *
     * @return | void
     * @post | getDirty() == true
     */
    public void insertLineBreak(Point insert) {

        if (isNotLocked()) {
            int row = insert.getX() - 1;
            int col = insert.getY() - 1;
            ArrayList<String> cont = new ArrayList<>(Arrays.asList(getContent()));
            String currentRow = getContent()[row];
            String firstPart = currentRow.substring(0, col);
            String secondPart = currentRow.substring(col);
            cont.set(row, firstPart);
            cont.add(row + 1, secondPart);
            setContent(cont.toArray(new String[0]));
            setDirty(true);
            fireNewLineBreak(insert);
        }
    }

    public void addNewChar(char c, Point insert, Point newInsert) {
        Edit nextEdit = new Insertion(c, insert, newInsert);
        nextEdit.setPrevious(getLastEdit());
        getLastEdit().setNext(nextEdit);
        setLastEdit(nextEdit);
        addNewChar(c, insert);
    }

    /**
     * This method adds a new character to the buffer and sets the buffer to dirty and moves the insertion point
     *
     * @param c      | The character to add
     * @param insert | The insertion point
     * @return | void
     * @post | getDirty() == true
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
                        eRow.append(c);
                    }
                    content[insert.getX() - 1] = eRow.toString();
                }
            }
            setContent(content);
            setDirty(true);
            fireNewChar(insert);
        }
    }

    public void deleteChar(Point insert, Point newInsert) {
        char c = deleteChar(insert);
        if (c != (char) 1) {
            Edit nextEdit = new Deletion(c, insert, newInsert);
            nextEdit.setPrevious(getLastEdit());
            getLastEdit().setNext(nextEdit);
            setLastEdit(nextEdit);
        }
    }

    /**
     * This method deletes a character from the buffer and sets the buffer to dirty and moves the insertion point
     *
     * @param insert | the insertion point
     * @return | void
     * @post | getDirty() == true
     */
    public char deleteChar(Point insert) {
        char c = (char) 1;
        if (isNotLocked()) {
            String[] content = getContent();
            String[] newContent;

            if (insert.getY() == 1) {
                if (insert.getX() == 1) return c;
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
                c = (char) 13;
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
                            } else {
                                c = content[i].toCharArray()[j];
                            }
                        }
                        newContent[i] = newRow.toString();
                    }
                }
                fireDelChar(insert);
            }
            setContent(newContent);
            setDirty(true);
            return c;
        }
        return c;
    }

    /* ***************
     *   OBSERVER    *
     * ***************/

    public void subscribeView(FileBufferView view) {
        listenerService.subscribeView(view);
    }

    public void unSubscribeView(FileBufferView view) {
        listenerService.unSubscribeView(view);
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

    /* ******************
     *   UNDO / REDO    *
     * ******************/

    /**
     * This method undoes the last edit and uses therefor the undo method of the lastEdit
     * It also sets the lastEdit to the previous edit
     */
    public void undo() {
        if (isNotLocked()) {
            if (getLastEdit().isLast()) setLastEdit(getLastEdit().getPrevious());
            getLastEdit().undo();
            setLastEdit(getLastEdit().getPrevious());
            if (getLastEdit().isFirst()) {
                setDirty(false);
            }
        }
    }

    /**
     * This method redoes the last edit and uses therefor the redo method of the lastEdit
     * It also sets the lastEdit to the next edit
     */
    public void redo() {
        if (isNotLocked()) {
            getLastEdit().getNext().redo();
            setLastEdit(getLastEdit().getNext());
        }
    }


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
     * This method sets the insertion point of the buffer to the given parameter insertionPoint
     *
     * @return | void
     * @post | getInsertionPoint() == insertionPoint
     */
    public Point getNewInsertionPoint(Point insertionPoint) {
        if (insertionPoint.getX() < 1 || insertionPoint.getY() < 1) {
            return null;
        }
        if (insertionPoint.getX() > getRowCount()) {
            insertionPoint = new Point(getRowCount(), insertionPoint.getY());
        }
        int currRowLength = content[insertionPoint.getX() - 1].length();
        if (insertionPoint.getY() > currRowLength) {
            insertionPoint = new Point(insertionPoint.getX(), currRowLength + 1);
        }
        return insertionPoint;
    }

    public void addSaveEdit(Edit[] edits) {
        SaveEdit newEdit = new SaveEdit(edits);
        getLastEdit().setNext(newEdit);
        newEdit.setPrevious(getLastEdit());
        setLastEdit(newEdit);
    }

    public abstract void close();


    /* *******************
     *   ABSTRACT EDIT   *
     * *******************/
    abstract class Edit {

        private Edit next;

        private Edit previous;

        /**
         * This constructor creates a new Edit object
         *
         * @post getNext() == this
         * @post getPrevious() == this
         */
        public Edit() {
            this.next = this;
            this.previous = this;
        }

        /**
         * This method returns the next Edit object
         *
         * @return: Edit
         */
        public Edit getNext() {
            return next;
        }

        /**
         * This method sets the next Edit object
         *
         * @param newNext the new next Edit object
         * @post getNext() == newNext
         * @return: void
         */
        public void setNext(Edit newNext) {
            this.next = newNext;
        }

        /**
         * This method returns the previous Edit object
         *
         * @return: Edit
         */
        public Edit getPrevious() {
            return previous;
        }

        /**
         * This method sets the previous Edit object
         *
         * @param newPrevious the new previous Edit object
         * @post getPrevious() == newPrevious
         * @return: void
         */
        public void setPrevious(Edit newPrevious) {
            this.previous = newPrevious;
        }

        public abstract void undo();

        public void undo(Buffer buffer) {}

        public abstract void redo();

        public void redo(Buffer buffer) {}

        public boolean isFirst() {
            return false;
        }

        public boolean isLast() {
            return false;
        }

        public Edit moveToStart(Point startLocation) {
            return this;
        }
    }

    /**
     * This class represents an empty Edit object, so an Edit object when there are no changes
     */
    class EmptyEdit extends Edit {
        public EmptyEdit() {
            super();
        }

        @Override
        public void undo() {}

        @Override
        public void redo() {}

        @Override
        public boolean isFirst() {
            return getPrevious() == this;
        }

        @Override
        public boolean isLast() {
            return getNext() == this;
        }
    }

    class SaveEdit extends Edit {

        Edit[] edits;
        public SaveEdit(Edit[] edits) {
            super();
            this.edits = edits;
        }

        private void setEdits(Edit[] newEdits) {
            this.edits = newEdits;
        }

        private Edit[] getEdits() {
            return edits;
        }

        @Override
        public void undo() {
            for (int i = getEdits().length - 1; i >= 0; i--) {
                getEdits()[i].undo(Buffer.this);
            }
        }

        @Override
        public void redo() {
            for (int i = 0; i < getEdits().length; i++) {
                getEdits()[i].redo(Buffer.this);
            }
        }

        public void mapToStartLocation(Point startLocation) {
            Edit[] result = new Edit[getEdits().length];
            for (int i = 0; i < getEdits().length; i++) {
                result[i] = getEdits()[i].moveToStart(startLocation);
            }
            setEdits(result);
        }
    }

    /* *******************
     *   NON-EMPTY EDIT  *
     * *******************/
    abstract class NonEmptyEdit extends Edit {

        private final char change;

        private Point insertionPoint;

        private Point insertionPointAfter;

        /**
         * This constructor creates a new NonEmptyEdit object with the given parameters c, insert and insertAfter
         *
         * @param c           the character that is changed
         * @param insert      the insertion point before the change
         * @param insertAfter the insertion point after the change
         * @post getChange() == c
         * @post getInsertionPoint() == insert
         * @post getInsertionPointAfter() == insertAfter
         */
        public NonEmptyEdit(char c, Point insert, Point insertAfter) {
            super();
            this.change = c;
            this.insertionPoint = insert;
            this.insertionPointAfter = insertAfter;
            setPrevious(new EmptyEdit());
            setNext(new EmptyEdit());
            getNext().setPrevious(this);
            getPrevious().setNext(this);
        }

        /**
         * This method returns the character that is changed
         *
         * @return: char
         */
        public char getChange() {
            return change;
        }

        /**
         * This method returns the insertion point before the change
         *
         * @return: Point
         */
        public Point getInsertionPoint() {
            return insertionPoint;
        }

        private void setInsertionPoint(Point newInsertionPoint) {
            this.insertionPoint = newInsertionPoint;
        }

        private void setInsertionPointAfter(Point newInsertionPointAfter) {
            this.insertionPointAfter = newInsertionPointAfter;
        }

        /**
         * This method returns the insertion point after the change
         *
         * @return: Point
         */
        public Point getInsertionPointAfter() {
            return insertionPointAfter;
        }

        @Override
        public Edit moveToStart(Point startLocation) {
            setInsertionPoint(getInsertionPoint().add(startLocation).minus(new Point(1,0)));
            setInsertionPointAfter(getInsertionPointAfter().add(startLocation).minus(new Point(1,0)));
            return this;
        }

        @Override
        public boolean isFirst() {
            return false;
        }
    }

    public void saveBuffer(String newLine) throws IOException {
        clearEdits();
        getFile().save(newLine, getContent());
        setDirty(false);
    }
    /* ******************
     *   TEST LASTEDIT  *
     * ******************/

    boolean lastEditIsEmptyEdit() {
        return getLastEdit().getClass() == EmptyEdit.class;
    }

    /* *******************
     *   INSERTION EDIT  *
     * *******************/
    class Insertion extends NonEmptyEdit {

        /**
         * This constructor creates a new Insertion object with the given parameters c, insert and insertAfter
         * when the change is a line break or adding a character
         */
        public Insertion(char c, Point insert, Point insertAfter) {
            super(c, insert, insertAfter);
        }

        /**
         * This method undoes the insertion and deletes the character at the insertion point or the line break
         *
         * @return: boolean, true if the undo was successful, false otherwise
         */
        public void undo() {
            deleteChar(getInsertionPointAfter());
        }

        public void undo(Buffer buffer) {
            buffer.deleteChar(getInsertionPointAfter());
        }

        /**
         * This method redoes the insertion and adds the character at the insertion point or adds the line break back
         *
         * @return: boolean, true if the redo was successful, false otherwise
         */
        public void redo() {
            if (getChange() == 13) {
                insertLineBreak(getInsertionPoint());
            } else {
                addNewChar(getChange(), getInsertionPoint());
            }
        }

        public void redo(Buffer buffer) {
            if (getChange() == 13) {
                buffer.insertLineBreak(getInsertionPoint());
            }
            else {
                buffer.addNewChar(getChange(), getInsertionPoint());
            }
        }
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    /* *******************
     *   DELETION EDIT   *
     * *******************/
    class Deletion extends NonEmptyEdit {
        /**
         * This constructor creates a new Deletion object with the given parameters c, insert and insertAfter
         * when the change is deleting a line break or deleting a character
         */
        public Deletion(char c, Point insert, Point insertAfter) {
            super(c, insert, insertAfter);
        }

        /**
         * This method undoes the deletion and adds the character at the insertion point or adds the line break back
         *
         * @return: boolean, true if the undo was successful, false otherwise
         */
        public void undo() {
            if (getChange() == 13) {
                insertLineBreak(getInsertionPointAfter());
            } else {
                addNewChar(getChange(), getInsertionPointAfter());
            }
        }

        /**
         * This method undoes the deletion and adds the character at the insertion point or adds the line break back
         *
         * @return: boolean, true if the undo was successful, false otherwise
         */
        public void undo(Buffer buffer) {
            if (getChange() == 13) {
                buffer.insertLineBreak(getInsertionPointAfter());
            } else {
                buffer.addNewChar(getChange(), getInsertionPointAfter());
            }
        }

        /**
         * This method redoes the deletion and deletes the character at the insertion point or the line break
         *
         * @return: boolean, true if the redo was successful, false otherwise
         */
        public void redo() {
            deleteChar(getInsertionPoint());
        }

        /**
         * This method redoes the deletion and deletes the character at the insertion point or the line break
         *
         * @return: boolean, true if the redo was successful, false otherwise
         */
        public void redo(Buffer buffer) {
            buffer.deleteChar(getInsertionPoint());
        }
    }
}
