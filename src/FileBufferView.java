import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class FileBufferView extends View
{
    /* *******************
     *   ABSTRACT EDIT   *
     * *******************/
    abstract static class Edit {

        private Edit next;

        private Edit previous;

        public Edit() {
            this.next = this;
            this.previous = this;
        }

        public Edit getNext() {
            return next;
        }

        public void setNext(Edit newNext) {
            this.next = newNext;
        }

        public Edit getPrevious() {
            return previous;
        }

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
     static class EmptyEdit extends Edit {
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
    abstract static class NonEmptyEdit extends Edit {

        private final char change;

        private final Point insertionPoint;

        private final Point insertionPointAfter;
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

        public char getChange() {
            return change;
        }

        public Point getInsertionPoint() {
            return insertionPoint;
        }

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
        public Insertion(char c, Point insert, Point insertAfter) {
            super(c, insert, insertAfter);
        }

        public boolean undo() {
            fileBuffer.deleteChar(getInsertionPointAfter());
            insertionPoint = getInsertionPoint();
            return true;
        }

        public boolean redo() {
            if (getChange() == 13) {
                fileBuffer.insertLineBreak(getInsertionPoint());
            }
            else {
                fileBuffer.addNewChar(getChange(), getInsertionPoint());
            }
            insertionPoint = getInsertionPointAfter();
            return true;
        }
    }

    /* *******************
     *   DELETION EDIT   *
     * *******************/
    class Deletion extends NonEmptyEdit {
        public Deletion(char c, Point insert, Point insertAfter) {
            super(c, insert, insertAfter);
        }

        public boolean undo() {
            if (getChange() == 13) {
                fileBuffer.insertLineBreak(getInsertionPointAfter());
            }
            else {
                fileBuffer.addNewChar(getChange(), getInsertionPointAfter());
            }
            insertionPoint = getInsertionPoint();
            return true;
        }

        public boolean redo() {
            fileBuffer.deleteChar(getInsertionPoint());
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

    private FileBuffer fileBuffer;

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
        this.fileBuffer = new FileBuffer(filepath, newLine);
        this.lastEdit = new EmptyEdit();
        this.verticalScrollState = 1;
        this.horizontalScrollState = 1;
        this.insertionPoint = new Point(1,1);
    }

    /** This constructor creates a new FileBufferView with the given heigth, width, parent, leftUpperCorner, filepath and newLine
     * @post getVerticalScrollState() == 1
     * @post getHorizontalScrollState() == 1
     */ /*
    public FileBufferView(int heigth, int witdh, CompositeLayout parent, Point leftUpperCorner, String filepath, String newLine) throws FileNotFoundException {
        super(heigth, witdh, parent, leftUpperCorner);
        this.fileBuffer = new FileBuffer(filepath, newLine);
        this.lastEdit = new EmptyEdit();
        this.verticalScrollState = 1;
        this.horizontalScrollState = 1;
        this.insertionPoint = new Point(1,1);
    } */

    /** This method sets the verticalScrollState of the FileBufferView
     * @post getVerticalScrollState() == newVerticalScrollState
     * @return: void
     */
    public FileBufferView(int heigth, int width, Point leftUpperCorner, FileBuffer fileBuffer) {
        super(heigth, width, leftUpperCorner);
        this.fileBuffer = fileBuffer;
        this.lastEdit = new EmptyEdit();
        this.verticalScrollState = 1;
        this.horizontalScrollState = 1;
        this.insertionPoint = new Point(1,1);
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
    public void setBuffer(FileBuffer newFileBuffer) {
        this.fileBuffer = newFileBuffer;
    }       // Deze kan handig zijn in toekomst als we de wijzigingen ongedaan moeten maken en de file opnieuw zouden
                // moeten inladen.

    /** This method returns the file of the FileBufferView
     * @return: File
     */
    public FileBuffer getBuffer() {
        return fileBuffer;
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

    /** This method returns the path of the file
     * @return: String
     */
    public String getPath() {
        return getBuffer().getPath();
    }

    public String getFileName() {
        String[] filepath = getPath().split("/");
        filepath = filepath[filepath.length - 1].split("\\\\");
        return filepath[filepath.length - 1];
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

    /** This method adds a new line break to the buffer
     * It also makes a new Edit object and set this new Edit as the lastEdit
     * @return: void
     */
    public boolean addNewLineBreak() {
        Point insert = getInsertionPoint();
        getBuffer().insertLineBreak(insert);
        setInsertionPoint(new Point(insert.getX()+1, 1));
        NonEmptyEdit nextEdit = new Insertion((char) 13, insert, getInsertionPoint());
        nextEdit.setPrevious(getLastEdit());
        getLastEdit().setNext(nextEdit);
        setLastEdit(nextEdit);
        updateScrollStates();
        return true;
    }

    /** This method adds a new character to the file
     *  It also makes a new Edit object and set this new Edit as the lastEdit
     * @return: void
     */
    public boolean addNewChar(char c) {
        Point insert = getInsertionPoint();
        getBuffer().addNewChar(c, insert);
        move(Direction.EAST);
        Edit nextEdit = new Insertion(c, insert, getInsertionPoint());
        nextEdit.setPrevious(getLastEdit());
        getLastEdit().setNext(nextEdit);
        setLastEdit(nextEdit);
        return true;
    }

    /** This method deletes the character before the insertionPoint.
     *  It also makes a new Edit object and set this new Edit as the lastEdit
     * @return: void
     */
    public boolean deleteChar() {
        if (! getInsertionPoint().equals(new Point(1,1))) {
            Point insert = getInsertionPoint();
            char c;
            if (getInsertionPoint().getY() == 1) {
                c = (char) 13;
                setInsertionPoint(new Point(getInsertionPoint().getX() - 1, getContent()[getInsertionPoint().getX() - 2].length() + 1));
                updateScrollStates();
            }
            else {
                c = getContent()[getInsertionPoint().getX() - 1].charAt(getInsertionPoint().getY() - 2);
                setInsertionPoint(new Point(getInsertionPoint().getX(), getInsertionPoint().getY() - 1));
            }
            getBuffer().deleteChar(insert);
            Edit nextEdit = new Deletion(c, insert, getInsertionPoint());
            nextEdit.setPrevious(getLastEdit());
            getLastEdit().setNext(nextEdit);
            setLastEdit(nextEdit);
            return true;
        }
        return false;
    }

    /* ******************
     *   CLOSE VIEW     *
     * ******************/

    /** This method checks whether this View is focused. If this view is focused, the corresponding buffer
     *  whill close
     *  -   When the buffer is dirty, the user needs to press y or n to either discard the changes or close the buffer
     * @return: FileBufferView || null
     */
    @Override
    public FileBufferView closeView(int focus, CompositeLayout parent) throws IOException {
        if (getPosition() != focus) {
            return this;
        }
        else {
            if (getBuffer().getDirty()) {
                showCloseErr();
                int c = terminalHandler.readByte();
                while (c != 121 && c != 89 && c != 78 && c != 110) {
                    c = terminalHandler.readByte();
                }
                if (c == 121 || c == 89) {
                    return null;
                }
                return this;
            }
            else {
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

    /** This method saves the buffer of the file and updates the scroll states
     * @return: void
     */
    public void saveBuffer(String newLine) throws IOException {
        getBuffer().saveBuffer(newLine);
    }

    /* *****************
     *    ROTATE VIEW  *
     * *****************/

    /** This method returns the focused Layout
     * @return: FileBufferView
     */
    @Override
    protected FileBufferView rotateView(int dir, int focus) {
         return this;
    }

    /* ******************
     *   UNDO / REDO    *
     * ******************/

    public boolean undo() {
        if (getLastEdit().getClass().isInstance(new EmptyEdit())) setLastEdit(getLastEdit().getPrevious());
        boolean result = getLastEdit().undo();
        setLastEdit(getLastEdit().getPrevious());
        if (getLastEdit().isFirst()) {
            getBuffer().setDirty(false);
        }
        return result;
    }

    public boolean redo() {
        boolean result = getLastEdit().getNext().redo();
        setLastEdit(getLastEdit().getNext());
        return result;
    }

    /* ****************
     *    RUN SNAKE   *
     * ****************/

    @Override
    public long getNextDeadline() {
        return System.currentTimeMillis();
    }

    public void tick() {return;}

    /* ************************
     *  OPEN FILEBUFFER VIEW  *
     * ************************/

    @Override
    public Layout openNewFileBuffer(int focus, Layout parent) {
        if (getPosition() == focus) {
            return new SideBySideLayout(1, 1, new Point(1, 1),
                    new Layout[] {this, new FileBufferView(1, 1, new Point(1, 1), getBuffer())});
        }
        return this;
    }

    @Override
    public Layout[] duplicate() {
        return new Layout[] {this, new FileBufferView(1, 1, new Point(1, 1), getBuffer())};
    }

    @Override
    public void updateViews(int focus, Point insert, char c, boolean isDeleted, FileBuffer buffer) {
        if (getPosition() != focus && getBuffer() == buffer) {
            if (c == (char) 13) {
                if (insert.getX() < getInsertionPoint().getX()) {
                    if (isDeleted) {
                        setVerticalScrollState(getVerticalScrollState() - 1);
                        move(Direction.NORD);
                    }
                    else {
                        setVerticalScrollState(getVerticalScrollState() + 1);
                        move(Direction.SOUTH);
                    }
                }
                else if (insert.getX() == getInsertionPoint().getX()) {
                    if (insert.getY() < getInsertionPoint().getY()) {
                        if (isDeleted) {

                        }
                        else {
                            setInsertionPoint(new Point(insert.getX() + 1, getInsertionPoint().getY() - insert.getY()));
                        }
                    }
                }
            }
            else {
                if (insert.getX() == getInsertionPoint().getX()) {
                    if (isDeleted) {
                        move(Direction.WEST);
                    }
                    else {
                        move(Direction.EAST);
                    }
                }
            }
        }
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

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

    /** This method returns the created vertical scrollbar
     * @return: char[]
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

    /** This method returns the created horizontal scrollbar
     * @return: String
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

    /** This method updates the scroll states
     * @return: void
     */
    public void updateScrollStates() {
        if (getInsertionPoint().getY() > getHorizontalScrollState() + getWidth() - 2) {
            setHorizontalScrollState(getInsertionPoint().getY());
        }
        else if (getInsertionPoint().getY() < getHorizontalScrollState()) {
            setHorizontalScrollState(getHorizontalScrollState() -  getWidth() + 1);
        }
        if (getInsertionPoint().getX() > getVerticalScrollState() + getHeigth() - 2) {
            setVerticalScrollState(getInsertionPoint().getX());
        }
        else if (getInsertionPoint().getX() < getVerticalScrollState()) {
            setVerticalScrollState(getVerticalScrollState() - getHeigth() + 1);
        }
    }

    /** This method updates the size of the layout to the given parameters heigth, width and leftUpperCorner
     * and updates the scroll states
     * @post getHeigth() == heigth
     * @post getWidth() == width
     * @post getLeftUpperCorner() == leftUpperCorner
     * @return: void
     */
    @Override
    public void updateSize(int heigth, int width, Point leftUpperCorner) {
        setHeigth(heigth);
        setWidth(width);
        setLeftUpperCorner(leftUpperCorner);
    }

    @Override
    public long getTick() {
        return 0;
    }
}
