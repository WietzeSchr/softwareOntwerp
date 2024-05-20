import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class FileBufferView extends View {
    /* *******************
     *   ABSTRACT EDIT   *
     * *******************/
     abstract class Edit {

        private Edit next;

        private Edit previous;

        /**
         * This constructor creates a new Edit object
         * @post getNext() == this
         * @post getPrevious() == this
         */
        public Edit() {
            this.next = this;
            this.previous = this;
        }

        /**
         * This method returns the next Edit object
         * @return: Edit
         */
        public Edit getNext() {
            return next;
        }

        /**
         * This method sets the next Edit object
         * @param newNext the new next Edit object
         * @post getNext() == newNext
         * @return: void
         */
        public void setNext(Edit newNext) {
            this.next = newNext;
        }

        /**
         * This method returns the previous Edit object
         * @return: Edit
         */
        public Edit getPrevious() {
            return previous;
        }

        /**
         * This method sets the previous Edit object
         * @param newPrevious the new previous Edit object
         * @post getPrevious() == newPrevious
         * @return: void
         */
        public void setPrevious(Edit newPrevious) {
            this.previous = newPrevious;
        }

        public abstract boolean undo();

        public abstract boolean redo();

        public abstract boolean isFirst();
    }

    /* ****************
     *   EMPTY EDIT   *
     * ****************/
    /**
     * This class represents an empty Edit object, so an Edit object when there are no changes
     */
      class EmptyEdit extends Edit {
        public EmptyEdit() {
            super();
        }

        @Override
        public boolean undo() {
            return false;
        }

        @Override
        public boolean redo() {
            return false;
        }

        @Override
        public boolean isFirst() {
            return getPrevious() == this;
        }
    }

    /* *******************
     *   NON-EMPTY EDIT  *
     * *******************/
    abstract class NonEmptyEdit extends Edit {

        private final char change;

        private final Point insertionPoint;

        private final Point insertionPointAfter;

        /**
         * This constructor creates a new NonEmptyEdit object with the given parameters c, insert and insertAfter
         * @param c the character that is changed
         * @param insert the insertion point before the change
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
         * @return: char
         */
        public char getChange() {
            return change;
        }

        /**
         * This method returns the insertion point before the change
         * @return: Point
         */
        public Point getInsertionPoint() {
            return insertionPoint;
        }

        /**
         * This method returns the insertion point after the change
         * @return: Point
         */
        public Point getInsertionPointAfter() {
            return insertionPointAfter;
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
         * This constructor creates a new Insertion object with the given parameters c, insert and insertAfter
         * when the change is a line break or adding a character
         */
        public Insertion(char c, Point insert, Point insertAfter) {
            super(c, insert, insertAfter);
        }

        /**
         * This method undoes the insertion and deletes the character at the insertion point or the line break
         * @return: boolean, true if the undo was successful, false otherwise
         */
        public boolean undo() {
            buffer.deleteChar(getInsertionPointAfter());
            insertionPoint = getInsertionPoint();
            return true;
        }

        /**
         * This method redoes the insertion and adds the character at the insertion point or adds the line break back
         * @return: boolean, true if the redo was successful, false otherwise
         */
        public boolean redo() {
            if (getChange() == 13) {
                buffer.insertLineBreak(getInsertionPoint());
            }
            else {
                buffer.addNewChar(getChange(), getInsertionPoint());
            }
            insertionPoint = getInsertionPointAfter();
            return true;
        }
    }

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
         * @return: boolean, true if the undo was successful, false otherwise
         */
        public boolean undo() {
            if (getChange() == 13) {
                buffer.insertLineBreak(getInsertionPointAfter());
            }
            else {
                buffer.addNewChar(getChange(), getInsertionPointAfter());
            }
            insertionPoint = getInsertionPoint();
            return true;
        }

        /**
         * This method redoes the deletion and deletes the character at the insertion point or the line break
         * @return: boolean, true if the redo was successful, false otherwise
         */
        public boolean redo() {
            buffer.deleteChar(getInsertionPoint());
            insertionPoint = getInsertionPointAfter();
            return true;
        }
    }

    /* ******************
     *  FILEBUFFERVIEW  *
     ********************/
    private int verticalScrollState;

    private int horizontalScrollState;

    private int position;

    private Point insertionPoint;

    private Buffer buffer;

    private Edit lastEdit;

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/

    /** This constructor creates a new FileBufferView with the given heigth, width, leftUpperCorner, filepath and newLine
     * @post getVerticalScrollState() == 1
     * @post getHorizontalScrollState() == 1
     */
    public FileBufferView(int heigth, int witdh, Point leftUpperCorner, String filepath, String newLine) throws FileNotFoundException {
        super(heigth, witdh, leftUpperCorner);
        this.buffer = new FileBuffer(filepath, newLine);
        this.lastEdit = new EmptyEdit();
        this.verticalScrollState = 1;
        this.horizontalScrollState = 1;
        this.insertionPoint = new Point(1,1);
        buffer.subscribeView(this);
    }

    /** This method sets the verticalScrollState of the FileBufferView
     * @post getVerticalScrollState() == newVerticalScrollState
     * @return: void
     */
    public FileBufferView(int heigth, int width, Point leftUpperCorner, Buffer buffer) {
        super(heigth, width, leftUpperCorner);
        this.buffer = buffer;
        this.lastEdit = new EmptyEdit();
        this.verticalScrollState = 1;
        this.horizontalScrollState = 1;
        this.insertionPoint = new Point(1,1);
        buffer.subscribeView(this);
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    public void setVerticalScrollState(int newVerticalScrollState) {
        this.verticalScrollState = newVerticalScrollState;
    }

    /** This method returns the verticalScrollState of the FileBufferView
     * @return: int
     */
    public int getVerticalScrollState() {
        return verticalScrollState;
    }

    /** This method sets the horizontalScrollState of the FileBufferView
     * @post getHorizontalScrollState() == newHorizontalScrollState
     * @return: void
     */
    public void setHorizontalScrollState(int newHorizontalScrollState) {
        this.horizontalScrollState = newHorizontalScrollState;
    }

    /** This method returns the horizontalScrollState of the FileBufferView
     * @return: int
     */
    public int getHorizontalScrollState() {
        return horizontalScrollState;
    }

    /** This method sets the file of the FileBufferView to parameter newFile
     * @post getFile() == newFile
     * @return: void
     */
    public void setBuffer(Buffer newBuffer) {
        this.buffer = newBuffer;
    }       // Deze kan handig zijn in toekomst als we de wijzigingen ongedaan moeten maken en de file opnieuw zouden
                // moeten inladen.

    /** This method returns the file of the FileBufferView
     * @return: File
     */
    public Buffer getBuffer() {
        return buffer;
    }

    /** This method sets the position of the FileBufferView to parameter newPosition
     * @post getPosition() == newPosition
     * @return: void
     */

    public Point getInsertionPoint() {
        return insertionPoint;
    }

    public void setInsertionPoint(Point newInsertionPoint) {
        Point insert = getBuffer().getNewInsertionPoint(newInsertionPoint);
        if (insert != null) {
            this.insertionPoint = insert;
        }
    }

    public void setPosition(int newPosition) {
        this.position = newPosition;
    }

    /** This method returns the position of the FileBufferView
     * @return: int
     */
    public int getPosition() {
        return this.position;
    }

    public void setLastEdit(Edit newLastEdit) {
        this.lastEdit = newLastEdit;
    }

    public Edit getLastEdit() {
        return lastEdit;
    }

    Path getPath() {
        return getBuffer().getFile().getPath();
    }

    String getPathString() {
        return getPath().toString();
    }

    /* **********************
     *  DERIVED ATTRIBUTES  *
     * **********************/

    /** This method returns the content of the FileBufferView
     * @return: String[]
     */
    public String[] getContent() {
        return getBuffer().getContent();
    }

    /** This method returns the number of rows
     * @return: int
     */
    public int getRowCount() {
        return getBuffer().getRowCount();
    }

    /** This method returns the number of columns
     * @return: int
     */
    public int getColumnCount() {
        return getBuffer().getColumnCount();
    }

    /** This method returns the number of characters in the buffer
     * @return: int
     */
    int getCharacterCount() {
        return getBuffer().countCharacters();
    }

    public String getFileName() {
        return getPath().getName();
    }

    /** This method returns the position of the cursor
     * @return: Point
     */
    @Override
    public Point getCursor() {
        Point insert = getInsertionPoint();
        Point leftUp = getLeftUpperCorner();
        return leftUp.add(insert).minus(new Point(getVerticalScrollState(), getHorizontalScrollState()));
    }

    /* ******************
     *   TEST LASTEDIT  *
     * ******************/

    boolean lastEditIsEmptyEdit() {
        return getLastEdit().getClass() == EmptyEdit.class;
    }

    boolean lastEditEquals(char c, boolean deletion, Point insert, Point insertAfter) {
        if (getLastEdit().getClass() == EmptyEdit.class) {
            return false;
        }
        NonEmptyEdit lastEdit = (NonEmptyEdit) getLastEdit();
        if (lastEdit.getChange() != c) {
            return false;
        }
        if (deletion && lastEdit.getClass() == Insertion.class) {
            return false;
        }
        if (! deletion && lastEdit.getClass() == Deletion.class) {
            return false;
        }
        if (! lastEdit.getInsertionPoint().equals(insert)) {
            return false;
        }
        if (! lastEdit.getInsertionPointAfter().equals(insertAfter)) {
            return false;
        }
        return true;
    }

    /* ******************
     *  INSPECT CONTENT *
     * ******************/

    /** This method moves the insertion point with the given parameter dir
     * @return: void
     */
    @Override
    public void move(Direction dir) {
        Point newInsertionPoint = getInsertionPoint().add(dir.point);
        setInsertionPoint(newInsertionPoint);
        updateScrollStates();
    }

    /* **********************
     *  EDIT BUFFER CONTENT *
     ************************/

    /** 
     * This method adds a new line break to the buffer 
     * It also makes a new Edit object and set this new Edit as the lastEdit
     * @return: View
     */
    public void addNewLineBreak(String newLine) {
        Point insert = getInsertionPoint();
        getBuffer().insertLineBreak(insert);
        setInsertionPoint(new Point(insert.getX()+1, 1));
        NonEmptyEdit nextEdit = new Insertion((char) 13, insert, getInsertionPoint());
        nextEdit.setPrevious(getLastEdit());
        getLastEdit().setNext(nextEdit);
        setLastEdit(nextEdit);
    }

    /**
     *  This method adds a new character to the fileBuffer at insertion point
     *  It also makes a new Edit object and set this new Edit as the lastEdit
     * @param c | The character to add
     * @return  | boolean
     */
    public void addNewChar(char c) {
        Point insert = getInsertionPoint();
        getBuffer().addNewChar(c, insert);
        Edit nextEdit = new Insertion(c, insert, getInsertionPoint());
        nextEdit.setPrevious(getLastEdit());
        getLastEdit().setNext(nextEdit);
        setLastEdit(nextEdit);
    }

    /** 
     * This method deletes the character before the insertionPoint.
     * It also makes a new Edit object and set this new Edit as the lastEdit
     * @return  | boolean
     */
    public void deleteChar() {
        if (! getInsertionPoint().equals(new Point(1,1))) {
            Point insert = getInsertionPoint();
            char c;
            Point newInsert;
            if (insert.getY() == 1) {
                c = (char) 13;
                newInsert = new Point(insert.getX() - 1, getContent()[insert.getX() - 2].length() + 1);
            } else {
                c = getContent()[insert.getX() - 1].charAt(insert.getY() - 2);
                newInsert = new Point(insert.getX(), insert.getY() - 1);
            }
            getBuffer().deleteChar(insert);
            setInsertionPoint(newInsert);
            Edit nextEdit = new Deletion(c, insert, getInsertionPoint());
            nextEdit.setPrevious(getLastEdit());
            getLastEdit().setNext(nextEdit);
            setLastEdit(nextEdit);
        }
    }

    /* ******************
     *   CLOSE VIEW     *
     * ******************/

    /** This method checks whether this View is focused. If this view is focused, the corresponding buffer
     *  whill close
     *  -   When the buffer is dirty, the user needs to press y or n to either discard the changes or close the buffer
     * @return  | FileBufferView || null
     */
    @Override
    public FileBufferView closeView(int focus, CompositeLayout parent) throws IOException {
        if (getPosition() != focus) {
            return this;
        }
        else {
            if (getBuffer().getDirty()) {
                showCloseErr();
                int c = 0;
                long deadline = System.currentTimeMillis() + 3000;     // Testing purposes
                while (c != 121 && c != 89 && c != 78 && c != 110) {
                    try {
                        c = terminalHandler.readByte(deadline);
                    } catch (TimeoutException e) {
                        c = 78;
                    }
                }
                if (c == 121 || c == 89) {
                    buffer.unSubscribeView(this);
                    getBuffer().close();
                    return null;
                }
                return this;
            }
            else {
                buffer.unSubscribeView(this);
                return null;
            }
        }
    }

    private void showCloseErr() {
        terminalHandler.clearScreen();
        terminalHandler.printText(1,1, "The buffer is dirty! are you sure the changes should be discarded (y|n)");
    }

    /* ******************
     *    SAVE BUFFER   *
     * ******************/

    /** 
     * This method saves the buffer of this view to the file with the given line seperator
     * @pre | newLine == "\n" || newLine == "\r\n"
     * @param newLine  | The line seperator to add to the buffer
     * @return  | void
     */
    @Override
    public void saveBuffer(String newLine) throws IOException {
        getBuffer().saveBuffer(newLine);
    }

    private void clearEdits() {
        setLastEdit(new EmptyEdit());
    }

    /* ******************
     *   UNDO / REDO    *
     * ******************/

    /**
     * This method undoes the last edit and uses therefor the undo method of the lastEdit
     * It also sets the lastEdit to the previous edit
     */
    public void undo() {
        if (getLastEdit().getClass().isInstance(new EmptyEdit())) setLastEdit(getLastEdit().getPrevious());
        getLastEdit().undo();
        setLastEdit(getLastEdit().getPrevious());
        if (getLastEdit().isFirst()) {
            getBuffer().setDirty(false);
        }
    }

    /**
     * This method redoes the last edit and uses therefor the redo method of the lastEdit
     * It also sets the lastEdit to the next edit
     */
    public void redo() {
        getLastEdit().getNext().redo();
        setLastEdit(getLastEdit().getNext());
    }

    /* ****************
     *    RUN SNAKE   *
     * ****************/

   /**
    * This method returns the next deadline of the system
    * @return  | long, the next deadline
    */
    @Override
    public long getNextDeadline() {
        return System.currentTimeMillis();
    }

    public void tick() {return;}

    /* ************************
     *  OPEN FILEBUFFER VIEW  *
     * ************************/

    /**
     * This method duplicates the FileBufferView
     * @return  | View[], an array with the FileBufferView duplicated
     */
    @Override
    public View[] duplicate() {
        return new View[]
                {new FileBufferView(getHeigth(), getWidth(), getLeftUpperCorner(), getBuffer())};
    }

    public void updateViewSaved() {
        clearEdits();
    }

    public void updateViewNewLineBreak(Point insert) {
        if (insert.getX() < getInsertionPoint().getX()) {
            move(Direction.SOUTH);
            setVerticalScrollState(getVerticalScrollState() + 1);
        }
        else if (insert.getX() == getInsertionPoint().getX() && insert.getY() <= getInsertionPoint().getY()) {
            setInsertionPoint(new Point(getInsertionPoint().getX() + 1, getInsertionPoint().getY() - insert.getY() + 1));
        }
    }

    public void updateViewDelLineBreak(Point insert) {
        if (insert.getX() < getInsertionPoint().getX()) {
            move(Direction.NORD);
            setVerticalScrollState(getVerticalScrollState() - 1);
        }
        else if (insert.getX() == getInsertionPoint().getX()) {
            setInsertionPoint(new Point(getInsertionPoint().getX() - 1, getInsertionPoint().getY()));
        }
    }

    public void updateViewNewChar(Point insert) {
        if (insert.getX() == getInsertionPoint().getX() && insert.getY() <= getInsertionPoint().getY()) {
            move(Direction.EAST);
        }
    }

    public void updateViewDelChar(Point insert) {
        if (insert.getX() == getInsertionPoint().getX() && insert.getY() <= getInsertionPoint().getY()) {
            move(Direction.WEST);
        }
    }

    public View[] getDirectoryView(LayoutManager manager) {
        String path = getParentPath();
        return new View[] {new DirectoryView(getHeigth(), getWidth(), getLeftUpperCorner(), path, manager)};
    }

    public String getParentPath() {
        return getPath().getParentPath();
    }

    public View[] parseJson(LayoutManager manager) {
        String[] content = getContent();
        StringBuilder jsonString = new StringBuilder();
        for (int i = 0; i < content.length; i++) {
            jsonString.append(content[i]);
            if (i != content.length - 1) {
                jsonString.append("\n");
            }
        }
        try {
            JsonObject json = SimpleJsonParser.parseJsonObject(jsonString.toString(), getBuffer());
            getBuffer().acquireLock();
            return new View[] {new DirectoryView(getHeigth(), getWidth(), getLeftUpperCorner(), json, manager)};
        }
        catch (SimpleJsonParserException exception) {
            setInsertionPoint(new Point(exception.location.line() + 1, exception.location.column() + 1));
        }
        return new View[] {};
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    /** 
     * This method shows the content of the FileBufferView and the updated scrollbars
     * @return  | String, the content of the FileBufferView
     * Visibile for testing
     */
    @Override
    String[] makeShow() {
        updateScrollStates();
        String[] result = new String[getHeigth() - 1];
        String[] cont = getContent();
        for (int i = 0; i < getHeigth() - 1; i++) {
            int row = i + getVerticalScrollState() - 1;
            if (row >= getRowCount()) {
                break;
            }
            if (cont[row] != null) {
                if (cont[row].length() > getHorizontalScrollState() - 1) {
                    if (cont[row].length() >= getWidth() + getHorizontalScrollState() - 2) {
                        result[i] = cont[row].substring(getHorizontalScrollState() - 1, getHorizontalScrollState() + getWidth() - 2);
                    } else {
                        result[i] = cont[row].substring(getHorizontalScrollState() - 1);
                    }
                }
            }
        }
        return result;
    }

    /** 
     * This method returns the created vertical scrollbar
     * @return  | char[], the vertical scrollbar
     * Visibile for testing
     */
    char[] makeVerticalScrollBar() {
        char[] result = new char[getHeigth() - 1];
        if (getHeigth() > getRowCount()) {
            Arrays.fill(result, '#');
            return result;
        }
        int rows = (int) (Math.ceil((float) getRowCount() / (float) (getHeigth() - 1)) * (getHeigth() - 1));
        int start = (int) Math.floor((float) getVerticalScrollState() / ((float) rows) * getHeigth());
        int end = (int) Math.ceil((float) (getVerticalScrollState()  + getHeigth() - 1 )/ (float) rows * (getHeigth())) - 1;
        for (int i = 0; i < result.length; i++) {
            if (i < start || i > end) {
                result[i] = '|';
            }
            else {
                result[i] = '#';
            }
        }
        return result;
    }

    /** 
     * This method returns the created horizontal scrollbar
     * @return  | String, the horizontal scrollbar
     * Visibile for testing
     */
    String makeHorizontalScrollBar() {
        StringBuilder result = makeFileHeader();
        if (getWidth() > getColumnCount()) {
            while (result.length() < getWidth()) {
                result.append('#');
            }
        }
        else {
            int start = (int) Math.floor((float) getHorizontalScrollState() / (float) (getHorizontalScrollState() + getWidth()) * (getWidth() - result.length()) + 1);
            int end = (int) Math.floor((float) (getHorizontalScrollState() + getWidth() - 1)/ (float) getColumnCount() * (getWidth() - result.length()));
            int i = 1;
            while (result.length() < getWidth()) {
                if (i < start || i > end) {
                    result.append('-');
                }
                else {
                    result.append('#');
                }
                i++;
            }
        }
        return result.toString();
    }

    /**
     * This method makes the header of the file for the horizontal scrollbar
     * @return  | StringBuilder, the header of the file
     */
    public StringBuilder makeFileHeader() {
        StringBuilder result = new StringBuilder();
        String filename = getFileName();
        if (getBuffer().getDirty()) {
            result.append("* ");
        }
        result.append(filename + ", r: " + getRowCount() +
                ", char: " + getCharacterCount() +
                ", insert: (" + getInsertionPoint().getX() + ", " + getInsertionPoint().getY() + ") ");
        return result;
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    /** 
     * This method updates the scroll states so that the insertion point is visible in the view
     * @return  | void
     */
    public void updateScrollStates() {
        if (getInsertionPoint().getY() > getHorizontalScrollState() + getWidth() - 2) {
            setHorizontalScrollState(getInsertionPoint().getY());
        }
        else if (getInsertionPoint().getY() < getHorizontalScrollState()) {
            while (getInsertionPoint().getY() < getHorizontalScrollState()){
                setHorizontalScrollState(getHorizontalScrollState() -  getWidth()+1);
            }
        }
        if (getInsertionPoint().getX() > getVerticalScrollState() + getHeigth() - 2) {
            setVerticalScrollState(getInsertionPoint().getX());
        }
        else if (getInsertionPoint().getX() < getVerticalScrollState()) {
            setVerticalScrollState(getVerticalScrollState() - getHeigth()+1);
        }
        if (getHorizontalScrollState() < 1) {
            setHorizontalScrollState(1);
        }
        if (getVerticalScrollState() < 1) {
            setVerticalScrollState(1);
        }
    }

    /** 
     * This method updates the size of the layout to the given parameters heigth, width and leftUpperCorner
     * and updates the scroll states
     * @post    | getHeigth() == heigth
     * @post    | getWidth() == width
     * @post    | getLeftUpperCorner() == leftUpperCorner
     * @return  | void
     */
    @Override
    public void updateSize(int heigth, int width, Point leftUpperCorner) {
        setHeigth(heigth);
        setWidth(width);
        setLeftUpperCorner(leftUpperCorner);
    }

    @Override
    public Buffer getBufferByName(String name) {
        if (getFileName().equals(name)) {
            return getBuffer();
        }
        return null;
    }

    @Override
    public long getTick() {
        return 0;
    }

}
