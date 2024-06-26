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
     * @param path     The path of the file
     * @param newLine  The new line separator
     * @post           getPath() == path
     * @post           getContent() == getFile().load(newLine)
     * @post           getDirty() == false
     */
    public FileBuffer(String path, String newLine) throws FileNotFoundException {
        super(new File(path), newLine);
    }

    /**
     * This constructor creates a new FileBuffer object with the given content and path
     *
     * @param content   The content of the file
     * @param path      The path of the file
     * @post            getPath() == path
     * @post            getContent() == content
     * @post            getDirty() == false
     */
    public FileBuffer(String[] content, String path) {
        super(new File(path), content);
    }
}

class JsonBuffer extends Buffer {

    /**
     * This constructor creates a new JsonBuffer
     * @param value     The JsonValue of the buffer
     * @param newLine   The line seperator used
     */
    public JsonBuffer(JsonValue value, String newLine) throws FileNotFoundException {
        super(value, newLine);
    }

    /**
     * This method returns the edits made to get to the current buffer state
     * @return  Edit[]
     */
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

    /**
     * This method saves the content of the buffer to the JsonValue
     * @param newLine   The line seperator used
     */
    @Override
    public void saveBuffer(String newLine) throws IOException {
        Edit[] edits = getCurrentEdits();
        getFile().save(newLine, getContent(), edits);
        setDirty(false);
    }

    /**
     * This method opens the directory the current file is in, does nothing for JsonValue
     * @param manager   The LayoutManager
     * @return          View[]  The new views
     */
    @Override
    public View[] getDirectoryView(LayoutManager manager) {
        return new View[] {};
    }
}

public abstract class Buffer {

    private String[] content;

    private FileSystemLeaf file;

    private boolean dirty;

    private final FileBufferListenerService listenerService = new FileBufferListenerService();

    private Edit lastEdit;

    private final JsonEditLock lock = new JsonEditLock();

    /**
     * This constructor creates a new Buffer
     * @param fsLeaf    The FileSystemLeaf
     * @param newLine   The line seperator used
     * @throws FileNotFoundException
     */
    public Buffer(FileSystemLeaf fsLeaf, String newLine) throws FileNotFoundException {
        this.file = fsLeaf;
        this.dirty = false;
        this.content = file.load(newLine);
        this.lastEdit = new EmptyEdit();
    }

    /**
     * This constructor creates a new buffer
     * @param fsLeaf    The FileSystemLeaf
     * @param content   The content of the buffer
     */
    public Buffer(FileSystemLeaf fsLeaf, String[] content) {
        this.file = fsLeaf;
        this.dirty = false;
        this.content = content;
        this.lastEdit = new EmptyEdit();
    }

    /**
     * This method returns the content of the buffer
     * @return  String[]    content
     */
    public String[] getContent() {
        return content;
    }

    /**
     * This method sets the content to the new content
     * @param newContent    The new content
     */
    public void setContent(String[] newContent) {
        this.content = newContent;
    }

    /**
     * This method returns the FileSystemLeaf of this buffer
     * @return  FileSystemLeaf
     */
    public FileSystemLeaf getFile() {
        return file;
    }

    /**
     * This method sets the FileSystemLeaf to the new leaf
     * @param newFile   The new FileSystemLeaf
     */
    public void setFile(FileSystemLeaf newFile) {
        this.file = newFile;
    }

    /**
     * This method returns true if the buffer is dirty, returns else otherwise
     * @return  boolean
     */
    public boolean getDirty() {
        return dirty;
    }

    /**
     * This method marks the buffer as being dirty or not
     * @param dirty new mark
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }


    private JsonEditLock getLock() {
        return lock;
    }

    /**
     * This method sets the last edit to the new last edit
     * @param newLastEdit   The new last edit
     */
    public void setLastEdit(Edit newLastEdit) {
        this.lastEdit = newLastEdit;
    }

    /**
     * This method returns the last edit
     * @return  Edit
     */
    public Edit getLastEdit() {
        return lastEdit;
    }

    /**
     * This method clears the edits of the buffer
     */
    protected void clearEdits() {
        setLastEdit(new EmptyEdit());
    }

    /* **********************
     *  DERIVED ATTRIBUTES  *
     * **********************/

    /**
     * This method returns the amount of rows in this buffer
     * @return  int
     */
    public int getRowCount() {
        return getContent().length;
    }

    /**
     * This method return the amount of columns in this buffer
     * This is equal to the maximum line length
     * @return  int
     */
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

    /**
     * This method returns the amount of characters in the buffer
     * @return  int
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

    /**
     * This method adds a new line break and adds a new edit object representing this linebreak
     * @param insert        The insertion point before adding the linebreak
     * @param newInsert     The insertion point after adding the linebreak
     */
    public void insertLineBreak(Point insert, Point newInsert) {
        NonEmptyEdit nextEdit = new Insertion((char) 13, insert, newInsert);
        addEdit(nextEdit);
        insertLineBreak(insert);
    }

    /**
     * This method adds a new line break at the given insertion point
     * @param insert    The insertion point
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

    /**
     * This method adds a new char at the insertion point and makes a new edit object representing this insertion
     * @param c         The added character
     * @param insert    The insertion point before the character was added
     * @param newInsert The insertion point after the character is added
     */
    public void addNewChar(char c, Point insert, Point newInsert) {
        Edit nextEdit = new Insertion(c, insert, newInsert);
        addEdit(nextEdit);
        addNewChar(c, insert);
    }

    /**
     * This method adds a new char at the given insertion point
     * @param c         The character to be added
     * @param insert    The point of insertion
     */
    public void addNewChar(char c, Point insert) {
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

    /**
     * This method deletes a character and makes a new edit object representing this deletion
     * @param insert    The insertion point before the deletion
     * @param newInsert The insertion point after the deletion
     */
    public void deleteChar(Point insert, Point newInsert) {
        char c = deleteChar(insert);
        if (c != (char) 1) {
            Edit nextEdit = new Deletion(c, insert, newInsert);
            addEdit(nextEdit);
        }
    }

    /**
     * This method deletes a character from the buffer at the given insertion point
     * @param insert    The insertion point
     * @return  char    The deleted character
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

    /**
     * This method adds a new edit to the edit chain
     * @param newEdit   The edit to be added
     */
    private void addEdit(Edit newEdit) {
        newEdit.setPrevious(getLastEdit());
        getLastEdit().setNext(newEdit);
        setLastEdit(newEdit);
    }

    /**
     * This method adds a new save edit containing a list of edits to be undone or redone at once
     * @param edits The list of edits
     */
    public void addSaveEdit(Edit[] edits) {
        SaveEdit newEdit = new SaveEdit(edits);
        addEdit(newEdit);
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

    /* ******************
     *   CLOSE VIEW     *
     * ******************/
    public void close() {
        getFile().close();
    }

    /* *******************
     *    SAVE BUFFER    *
     * *******************/

    /**
     * This method saves the buffer to the corresponding file
     * @param newLine   The line seperator to add to the file
     */
    public void saveBuffer(String newLine) throws IOException {
        clearEdits();
        getFile().save(newLine, getContent(), null);
        setDirty(false);
    }

    /* ***********************
     *  OPEN DIRECTORY VIEW  *
     * ***********************/

    /**
     * This method returns a new view on the directory of the current file
     * @param manager   The layout manager
     * @return          View[] {newView}
     */
    public View[] getDirectoryView(LayoutManager manager) {
        return new View[] {new DirectoryView(1,1, new Point(1,1), getParentPath(), manager)};
    }


    /* ******************
     *   JSON LOCKING   *
     * ******************/

    /**
     * This method acquires a new lock on this buffer
     */
    protected void acquireLock() {
        getLock().acquireNewLock();
    }

    /**
     * This method releases a lock on this buffer
     */
    protected void releaseLock() {
        getLock().releaseLock();
    }

    /**
     * This method returns true if this buffer is not locked, returns false otherwise
     * @return
     */
    public boolean isNotLocked() {
        return !getLock().isLocked();
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

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

    /**
     * This method returns the absolute path of the FileSystemLeaf this buffer contains
     * @return  String  absolute path
     */
    public String getPathString() {
        return getFile().getPathString();
    }

    /**
     * This method return the name of the FileSystemLeaf this buffer contains
     * @return  String  leafName
     */
    public String getName() {
        return getFile().getName();
    }

    /**
     * This method returns the absolute path of the parent, returns null if this has no parent
     * @return  String || null
     */
    public String getParentPath() {
        return getFile().getParentPath();
    }

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
         * @return:  Edit
         */
        public Edit getPrevious() {
            return previous;
        }

        /**
         * This method sets the previous Edit object
         *
         * @param newPrevious the new previous Edit object
         * @post getPrevious() == newPrevious
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

        public void mapToStart(String newLine, Point startLocation) {}

        public void mapToStringLocation(String newLine) {}
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

    /* **************
     *   SAVE EDIT  *
     * **************/
    class SaveEdit extends Edit {

        final Edit[] edits;

        /**
         * This constructor creates a new save edit, with the given edits that where made
         * This save edit is used to undo or redo more edits at once
         * @param edits The edits that should be undone or redone
         */
        public SaveEdit(Edit[] edits) {
            super();
            setNext(new EmptyEdit());
            getNext().setPrevious(this);
            this.edits = edits;
        }


        /**
         * This method returns the edits that were made in this save
         * @return  Edit[]  edits
         */
        private Edit[] getEdits() {
            return edits;
        }

        /**
         * This method undoes this edit by undoing all the edits it contains in the opposite order
         */
        @Override
        public void undo() {
            for (int i = getEdits().length - 1; i >= 0; i--) {
                getEdits()[i].undo(Buffer.this);
            }
        }

        /**
         * This method undoes this edit by undoing all the edits it contains in the opposite orde
         * @param buffer    The buffer the edits should be undone on (used for mapped edits)
         */
        @Override
        public void undo(Buffer buffer) {
            for (int i = getEdits().length - 1; i >= 0; i--) {
                getEdits()[i].undo(buffer);
            }
        }

        /**
         * This method redoes this edit by redoing all the edits it contains in the same order
         */
        @Override
        public void redo() {
            for (int i = 0; i < getEdits().length; i++) {
                getEdits()[i].redo(Buffer.this);
            }
        }

        /**
         * This method redoes this save edit on the given buffer by redoing all the edits it contains in the same order
         * @param buffer    The buffer the edits should be redone on (used for mapped edits)
         */
        @Override
        public void redo(Buffer buffer) {
            for (int i = 0; i < getEdits().length; i++) {
                getEdits()[i].redo(buffer);
            }
        }

        /**
         * This method is used to maps the edit to the location of the edit in a new buffer, containing the original
         * string at the given start location
         * @param newLine       The line seperator used
         * @param startLocation The start location of the original string
         */
        @Override
        public void mapToStart(String newLine, Point startLocation) {
            for (Edit edit: getEdits()) {
                edit.mapToStart(newLine, startLocation);
            }
        }

        /**
         * This method maps this edit, made in an array of strings to the location as if it
         * was made in a single array buffer containing newline characters
         * @param newLine   The line seperator that is used
         */
        @Override
        public void mapToStringLocation(String newLine) {
            for (Edit edit : getEdits()) {
                edit.mapToStringLocation(newLine);
            }
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

        /**
         * This method is used to maps the edit to the location of the edit in a new buffer, containing the original
         * string at the given start location
         * @param newLine       The line seperator used
         * @param startLocation The start location of the original string
         */
        @Override
        public void mapToStart(String newLine, Point startLocation) {
            setInsertionPoint(getInsertionPoint().add(startLocation).minus(new Point(1,0)));
            setInsertionPointAfter(getInsertionPointAfter().add(startLocation).minus(new Point(1,0)));
        }

        /**
         * This method maps this edit, made in an array of strings to the location as if it
         * was made in a single array buffer containing newline characters
         * @param newLine
         */
        @Override
        public void mapToStringLocation(String newLine) {

            String[] content = getContent();
            if (getInsertionPoint().getX() != 1) {
                int row1 = getInsertionPoint().getX() - 1;
                int position1 = 0;
                for (int i = 0; i < row1; i++) {
                    position1 += content[i].length();
                    position1 += newLine.length();
                }
                position1 += getInsertionPoint().getY() - 2;
                setInsertionPoint(new Point(1, position1));
            }
            if (getInsertionPointAfter().getX() != 1) {
                int row2 = getInsertionPointAfter().getX() - 1;
                int position2 = 0;
                for (int i = 0; i < row2; i++) {
                    position2 = content[i].length();
                    position2 += newLine.length();
                }
                position2 += getInsertionPointAfter().getY() - 2;
                setInsertionPointAfter(new Point(1, position2));
            }
        }

        @Override
        public boolean isFirst() {
            return false;
        }
    }

    /* *******************
     *   INSERTION EDIT  *
     * *******************/
    class Insertion extends NonEmptyEdit {

        /**
         * This constructor creates a new Insertion edit
         * @param c             The character that was added, (char) 13 if line break is added
         * @param insert        The insertion point before the insertion
         * @param insertAfter   The insertion point after the insertion
         */
        public Insertion(char c, Point insert, Point insertAfter) {
            super(c, insert, insertAfter);
        }

        /**
         * This method undoes the insertion and deletes the character or line break at the insertion point
         */
        public void undo() {
            deleteChar(getInsertionPointAfter());
        }

        /**
         * This method undoes the insertion and deletes the character or line break that was added at the insertion point
         * @param buffer    The buffer on which the edit should be undone (used for mapped edits)
         */
        public void undo(Buffer buffer) {
            buffer.deleteChar(getInsertionPointAfter());
        }

        /**
         * This method redoes the insertion and adds the character or line break at the insertion point
         */
        public void redo() {
            if (getChange() == 13) {
                insertLineBreak(getInsertionPoint());
            } else {
                addNewChar(getChange(), getInsertionPoint());
            }
        }

        /**
         * This method redoes the insertion and adds the character or line break at the insertion point
         * @param buffer    The buffer on which the edit should be undone (used for mapped edits)
         */
        public void redo(Buffer buffer) {
            if (getChange() == 13) {
                buffer.insertLineBreak(getInsertionPoint());
            }
            else {
                buffer.addNewChar(getChange(), getInsertionPoint());
            }
        }
    }

    /* *******************
     *   DELETION EDIT   *
     * *******************/
    class Deletion extends NonEmptyEdit {

        /**
         * This constructor creates a new Deletion edit
         * @param c             The character that was deleted
         * @param insert        The insertion point before de deletion
         * @param insertAfter   The insertion point after the deletion
         */
        public Deletion(char c, Point insert, Point insertAfter) {
            super(c, insert, insertAfter);
        }

        /**
         * This method undoes the deletion and adds the character or line break at the insertion point
         */
        public void undo() {
            if (getChange() == 13) {
                insertLineBreak(getInsertionPointAfter());
            } else {
                addNewChar(getChange(), getInsertionPointAfter());
            }
        }

        /**
         * This method undoes the deletion and adds the character or line break at the insertion point
         */
        public void undo(Buffer buffer) {
            if (getChange() == 13) {
                buffer.insertLineBreak(getInsertionPointAfter());
            } else {
                buffer.addNewChar(getChange(), getInsertionPointAfter());
            }
        }

        /**
         * This method redoes the deletion and deletes the character or line break at the insertion point
         */
        public void redo() {
            deleteChar(getInsertionPoint());
        }

        /**
         * This method redoes the deletion and deletes the character or line break at the insertion point
         */
        public void redo(Buffer buffer) {
            buffer.deleteChar(getInsertionPoint());
        }
    }
}
