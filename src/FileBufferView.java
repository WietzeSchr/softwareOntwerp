import java.io.FileNotFoundException;
import java.io.IOException;

public class FileBufferView extends View
{
    /* *******************
     *   ABSTRACT EDIT   *
     * *******************/
    private abstract static class Edit {

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

        public abstract void undo();

        public abstract void redo();

        public abstract boolean isFirst();
    }

    /* ****************
     *   EMPTY EDIT   *
     * ****************/
    private static class EmptyEdit extends Edit {
        public EmptyEdit() {
            super();
        }

        @Override
        public void undo() {
            return;
        }

        @Override
        public void redo() {
            return;
        }

        @Override
        public boolean isFirst() {
            return getPrevious() == this;
        }
    }

    /* *******************
     *   NON-EMPTY EDIT  *
     * *******************/
    private abstract static class NonEmptyEdit extends Edit {

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
    private class Insertion extends NonEmptyEdit {
        public Insertion(char c, Point insert, Point insertAfter) {
            super(c, insert, insertAfter);
        }

        public void undo() {
            fileBuffer.deleteChar(getInsertionPointAfter());
            insertionPoint = getInsertionPoint();
        }

        public void redo() {
            if (getChange() == 13) {
                fileBuffer.insertLineBreak(getInsertionPoint());
            }
            else {
                fileBuffer.addNewChar(getChange(), getInsertionPoint());
            }
            insertionPoint = getInsertionPointAfter();
        }
    }

    /* *******************
     *   DELETION EDIT   *
     * *******************/
    private class Deletion extends NonEmptyEdit {
        public Deletion(char c, Point insert, Point insertAfter) {
            super(c, insert, insertAfter);
        }

        public void undo() {
            if (getChange() == 13) {
                fileBuffer.insertLineBreak(getInsertionPointAfter());
            }
            else {
                fileBuffer.addNewChar(getChange(), getInsertionPointAfter());
            }
            insertionPoint = getInsertionPoint();
        }

        public void redo() {
            fileBuffer.deleteChar(getInsertionPoint());
            insertionPoint = getInsertionPointAfter();
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
    public FileBufferView(int heigth, int witdh, Point leftUpperCorner, String filepath, String newLine) {
        super(heigth, witdh, leftUpperCorner);
        try {
            this.fileBuffer = new FileBuffer(filepath, newLine);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.lastEdit = new EmptyEdit();
        this.verticalScrollState = 1;
        this.horizontalScrollState = 1;
        this.insertionPoint = new Point(1,1);
    }

    /** This constructor creates a new FileBufferView with the given heigth, width, parent, leftUpperCorner, filepath and newLine
     * @post getVerticalScrollState() == 1
     * @post getHorizontalScrollState() == 1
     */
    public FileBufferView(int heigth, int witdh, CompositeLayout parent, Point leftUpperCorner, String filepath, String newLine) {
        super(heigth, witdh, parent, leftUpperCorner);
        try {
            this.fileBuffer = new FileBuffer(filepath, newLine);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.lastEdit = new EmptyEdit();
        this.verticalScrollState = 1;
        this.horizontalScrollState = 1;
        this.insertionPoint = new Point(1,1);
    }

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

    /** This method adds a new line break to the buffer and updates the scroll states
     * @return: void
     */
    public void addNewLineBreak() {
        Point insert = getInsertionPoint();
        getBuffer().insertLineBreak(insert);
        setInsertionPoint(new Point(insert.getX()+1, 1));
        NonEmptyEdit nextEdit = new Insertion((char) 13, insert, getInsertionPoint());
        nextEdit.setPrevious(getLastEdit());
        getLastEdit().setNext(nextEdit);
        setLastEdit(nextEdit);
        updateScrollStates();
    }

    /** This method adds a new character to the file and updates the scroll states
     * @return: void
     */
    public void addNewChar(char c) {
        Point insert = getInsertionPoint();
        getBuffer().addNewChar(c, insert);
        move(Direction.EAST);
        Edit nextEdit = new Insertion(c, insert, getInsertionPoint());
        nextEdit.setPrevious(getLastEdit());
        getLastEdit().setNext(nextEdit);
        setLastEdit(nextEdit);
    }

    /** This method deletes a character of the buffer and updates the scroll states
     * @return: void
     */
    public void deleteChar() {
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
        }
    }

    /* ******************
     *   CLOSE VIEW     *
     * ******************/

    /** This method closes the buffer and updates the subLayouts
     * @return: FileBufferView || null
     */
    @Override
    public FileBufferView closeView(int focus, CompositeLayout parent) throws IOException {
        if (getPosition() != focus) {
            return this;
        }
        else {
            return null;
            /*
                if (getBuffer().getDirty()) {
                    terminalHandler.clearScreen();
                    terminalHandler.printText(1,1, "The buffer is dirty! are you sure the changes should be discarded (y|n)");
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

             */
        }
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
    protected FileBufferView rotateSiblingsFlip(int dir, int focus, int nextFocus, CompositeLayout parent) {
        return this;
    }
    protected FileBufferView rotateSiblings(int dir, int focus, int nextFocus, CompositeLayout parent) {
        return this;
    }


    protected FileBufferView rotateNonSiblings(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2) {
        return this;
    }

     protected FileBufferView rotateNonSiblingsPromote(int dir, int focus, View nextView, CompositeLayout parent1, CompositeLayout parent2) {
         return this;
     }

    /* ******************
     *   UNDO / REDO    *
     * ******************/

    public void undo() {
        if (getLastEdit().getClass().isInstance(new EmptyEdit())) setLastEdit(getLastEdit().getPrevious());
        getLastEdit().undo();
        setLastEdit(getLastEdit().getPrevious());
        if (getLastEdit().isFirst()) {
            getBuffer().setDirty(false);
        }
    }

    public void redo() {
        getLastEdit().getNext().redo();
        setLastEdit(getLastEdit().getNext());
    }

    /* ****************
     *    RUN SNAKE   *
     * ****************/

    @Override
    public long getNextDeadline() {
        return System.currentTimeMillis();
    }

    public void tick() {return;}

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    @Override
    String[] makeShow() {
        setInsertionPoint(getInsertionPoint());
        String[] result = new String[getHeigth()];
        String[] cont = getContent();
        char[] verticalScrollBar = makeVerticalScrollBar();
        for (int i = 0; i < getHeigth() - 1; i++) {
            int row = i + getVerticalScrollState() - 1;
            if (row >= getRowCount()) {
                break;
            }
            if (cont[row] != null) {
                if (cont[row].length() > getHorizontalScrollState() - 1) {
                    if (cont[row].length() >= getWidth() + getHorizontalScrollState() - 2) {
                        result[i] = cont[row].substring(getHorizontalScrollState() - 1, getHorizontalScrollState() + getWidth() - 3);
                    } else {
                        result[i] = cont[row].substring(getHorizontalScrollState() - 1);
                    }
                }
            }
        }
        for(int i=0; i<result.length-1; i++){
            if(result[i] == null){
                result[i] = "";
            }
            while(result[i].length() < getWidth()-1){
                result[i] = result[i] + ' ';
            }
            result[i] = result[i] + verticalScrollBar[i];
        }
        result[getHeigth()-1] = makeHorizontalScrollBar();
        return result;
    }

    /** This method returns the created vertical scrollbar
     * @return: char[]
     */
    private char[] makeVerticalScrollBar() {
        int rows = (int) (Math.ceil((float) getRowCount() / (float) (getHeigth() - 1)) * (getHeigth() - 1));
        int start = (int) Math.floor((float) getVerticalScrollState() / ((float) rows) * getHeigth());
        int end = (int) Math.ceil((float) (getVerticalScrollState()  + getHeigth() - 1 )/ (float) rows * (getHeigth())) - 1;
        char[] result = new char[getHeigth() - 1];
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
    private String makeHorizontalScrollBar() {
        StringBuilder result = makeFileHeader();
        if (getWidth() - 1 > getColumnCount()) {
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
