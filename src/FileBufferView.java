import io.github.btj.termios.Terminal;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

/* ******************
 *  FILEBUFFERVIEW  *
 ********************/
public class FileBufferView extends Layout
{
    private int verticalScrollState;

    private int horizontalScrollState;

    private int position;

    private Point insertionPoint;

    private FileBuffer fileBuffer;

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
    }

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
    private int getCharacterCount() {
        return getBuffer().countCharacters();
    }

    /** This method returns the path of the file
     * @return: String
     */
    public String getPath() {
        return getBuffer().getPath();
    }

    /** This method returns the position of the cursor
     * @return: Point
     */
    public Point getCursor() {
        Point insert = getInsertionPoint();
        Point leftUp = getLeftUpperCorner();
        return new Point((int) (leftUp.getX() + insert.getX() - getVerticalScrollState()), (int) (leftUp.getY() + insert.getY() - getHorizontalScrollState()));
    }

    /* ******************
     *  INSPECT CONTENT *
     * ******************/

    /** This method moves the insertion point with the given parameter dir
     * @return: void
     */
    public void moveInsertionPoint(Point dir) {
        Point newInsertionPoint = new Point((int) (getInsertionPoint().getX() + dir.getX()),
                (int) (getInsertionPoint().getY() + dir.getY()));
        setInsertionPoint(newInsertionPoint);
    }

    @Override
    public int getNextFocus(int focus) {
        return focus;
    }

    @Override
    public int getPreviousFocus(int focus) {
        return focus;
    }

    /* **********************
     *  EDIT BUFFER CONTENT *
     ************************/

    /** This method adds a new line break to the buffer and updates the scroll states
     * @return: void
     */
    public void addNewLineBreak() {
        getBuffer().insertLineBreak(getInsertionPoint());
        setInsertionPoint(new Point((int)getInsertionPoint().getX()+1, 1));
        updateScrollStates();
    }

    /** This method adds a new character to the file and updates the scroll states
     * @return: void
     */
    public void addNewChar(char c) {
        getBuffer().addNewChar(c, getInsertionPoint());
        moveInsertionPoint(new Point(0, 1));
        updateScrollStates();
    }

    /** This method deletes a character of the buffer and updates the scroll states
     * @return: void
     */
    public void deleteChar() {
        getBuffer().deleteChar(getInsertionPoint());
        if (getInsertionPoint().getY() == 1) {
            setInsertionPoint(new Point((int) (getInsertionPoint().getX() - 1), getContent()[(int) (getInsertionPoint().getX() - 2)].length() + 1));
        }
        else {
            moveInsertionPoint(new Point(0, -1));
        }
        updateScrollStates();
    }

    /* ******************
     *   CLOSE BUFFER   *
     * ******************/

    /** This method closes the buffer and updates the subLayouts
     * @return: FileBufferView || null
     */
    @Override
    public FileBufferView closeBuffer(int focus, CompositeLayout parent) throws IOException {
        if (getPosition() != focus) {
            return this;
        }
        else {
            if (getBuffer().getDirty()) {
                Terminal.clearScreen();
                Terminal.printText(1,1, "The buffer is dirty! are you sure the changes should be discarded (y|n)");
                int c = Terminal.readByte();
                while (c != 121 && c != 89 && c != 78 && c != 110) {
                    c = Terminal.readByte();
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

    /* ******************
     *    SAVE BUFFER   *
     * ******************/

    /** This method saves the buffer of the file and updates the scroll states
     * @return: void
     */
    public void saveBuffer(String newLine) throws IOException {
        getBuffer().saveBuffer(newLine);
        updateScrollStates();
    }

    /* *****************
     *    ROTATE VIEW  *
     * *****************/

    /** This method returns the focused Layout
     * @return: FileBufferView
     */
     @Override
    protected FileBufferView rotateView(int dir, CompositeLayout parent, int focus, int nextFocus) {
        return this;
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    /** This method shows the content of the FileBufferView and the updated scrollbars
     * @return: void
     */
    public void show() {
        updateScrollStates();
        String[] cont = getContent();
        for (int i = 0; i < getHeigth() - 1; i++) {
            int row = i + getVerticalScrollState() - 1;
            if (row >= getRowCount()) {
                break;
            }
            if (cont[row] != null) {
                if (cont[row].length() > getHorizontalScrollState() - 1) {
                    if (cont[row].length() >= getWidth() + getHorizontalScrollState() - 2) {
                        Terminal.printText((int) getLeftUpperCorner().getX() + i,
                                (int) getLeftUpperCorner().getY(), cont[row].substring(getHorizontalScrollState() - 1, getHorizontalScrollState() + getWidth() - 3));
                    } else {
                        Terminal.printText((int) getLeftUpperCorner().getX() + i,
                                (int) getLeftUpperCorner().getY(), cont[row].substring(getHorizontalScrollState() - 1));
                    }
                }
            }
        }
        showScrollbars();
    }

    /** This method shows the scrollbars
     * @return: void
     */
    private void showScrollbars() {
        char[] verticalScrollBar = makeVerticalScrollBar();
        String horizontalScrollBar = makeHorizontalScrollBar();
        Terminal.printText((int) (getLeftUpperCorner().getX() + getHeigth()) - 1, (int) getLeftUpperCorner().getY(), horizontalScrollBar);
        for (int i = 0; i < verticalScrollBar.length; i++) {
            Terminal.printText((int) (getLeftUpperCorner().getX() + i),
                    (int) (getLeftUpperCorner().getY() + getWidth() - 1), String.valueOf(verticalScrollBar[i]));
        }
    }

    /** This method returns the created vertical scrollbar
     * @return: char[]
     */
    private char[] makeVerticalScrollBar() {
        int h = getHeigth()-1;
        int partsAbove = (int)getInsertionPoint().getX()-1 / h;
        float chunkSize = ((float)h / (int) Math.ceil((float)getRowCount() / h));
        char[] result = new char[h];
        for (int i=0; i<h; i++) {
            if(i<Math.floor(chunkSize*partsAbove)) result[i] = '|';
            else if(i<Math.ceil(chunkSize*(partsAbove+1))) result[i] = '#';
            else result[i] = '|';
        }
        return result;
    }

    /** This method returns the created horizontal scrollbar
     * @return: String
     */
    private String makeHorizontalScrollBar() {
        StringBuilder result = new StringBuilder();
        String[] filepath = getPath().split("/");
        filepath = filepath[filepath.length - 1].split("\\\\");
        String filename = filepath[filepath.length - 1];
        if (getBuffer().getDirty()) {
            result.append("* ");
        }
        result.append(filename + ", r: " + String.valueOf(getRowCount()) + ", char: " + String.valueOf(getCharacterCount()) + " ");
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

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    /** This method updates the scroll states
     * @return: void
     */
    public void updateScrollStates() {
        if (getInsertionPoint().getY() > getHorizontalScrollState() + getWidth() - 2) {
            setHorizontalScrollState((int) getInsertionPoint().getY());
        }
        else if (getInsertionPoint().getY() < getHorizontalScrollState()) {
            setHorizontalScrollState(getHorizontalScrollState() -  getWidth() + 1);
        }
        if (getInsertionPoint().getX() > getVerticalScrollState() + getHeigth() - 2) {
            setVerticalScrollState((int) getInsertionPoint().getX());
        }
        else if (getInsertionPoint().getX() < getVerticalScrollState()) {
            setVerticalScrollState(getVerticalScrollState() - getHeigth() + 1);
        }
    }

    /** This method returns the focused view at the given index i
     * @return: int
     */
    @Override
    public void initViewPosition(int i) {
        setPosition(i);
    }

    /** This method returns the focused view at the given index i
     * @return: FileBufferView || null
     */
    @Override
    public FileBufferView getFocusedView(int i) {
        if (getPosition() == i) {
            return this;
        }
        return null;
    }

    /** This method returns the number of views
     * @return: int
     */
    @Override
    public int countViews() {
        return 1;
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
        updateScrollStates();
    }
}
