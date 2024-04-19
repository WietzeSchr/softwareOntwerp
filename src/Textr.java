import java.io.IOException;
import java.util.concurrent.TimeoutException;

class TerminalParser {
    int buffer;
    boolean bufferFull;
    TerminalHandler terminalHandler = new TerminalHandler();
    int peekByte() throws IOException {
        if (! bufferFull) {
            buffer = terminalHandler.readByte();
            bufferFull = true;
        }
        return  buffer;
    }

    void eatByte() throws IOException {
        peekByte();
        bufferFull = false;
    }

    void expect(int n) throws IOException {
        if (peekByte() != n) {
            throw new RuntimeException("Unexpected byte");
        }
        eatByte();
    }

    int expectNumber() throws IOException {
        int c = peekByte();
        if (c < '0' || c > '9') {
            throw  new RuntimeException("Digit expected but got" + c);
        }
        int result = c - '0';
        eatByte();
        for (;;) {
            c = peekByte();
            if (c < '0' || c > '9')
            {
                break;
            }
            else {
                result *= 10;
                result += c -'0';
            }
            eatByte();
        }
        return result;
    }
}

/* ******************
 *      TEXTR       *
 * ******************/

public class Textr
{
    private Layout layout;

    private String newLine;

    private int focus;
    TerminalHandler terminalHandler = new TerminalHandler();

    /* ******************
     *  CONSTRUCTORS    *
     * ******************/

    /** 
     * This constructor creates a new Textr with the given newLine and filepaths
     * @post : getNewLine() == newLine
     * @post : getFocus() == 1
     */
    public Textr(String newLine, String[] filepaths) throws IOException {
        Point size;
        try {
            size = getSize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (filepaths.length == 0) {
            //this.layout = new GameView(size.getX(), size.getY(), new Point(1,1));
            throw new RuntimeException("please give one or more filepaths to open");
        }
        else if (filepaths.length > 1) {
            this.layout = new StackedLayout(1,1, new Point(1,1), filepaths, newLine);
        }
        else {
            this.layout = new FileBufferView(1,1, new Point(1, 1), filepaths[0], newLine);
        }
        this.newLine = newLine;
        this.focus = 1;
        updateSize(size.getX(), size.getY());
        initViewPositions();
        show();
        run();
    }

    /** 
     * This constructor creates a new Textr object that can be used for testing.
     * @pre | newLine == "\n" || newLine == "\r\n"
     * @post | getLayout() = layout
     * @post | getNewLine() = newLine
     */
    public Textr(String newLine, Layout layout ) {
        this.layout = layout;
        this.newLine = newLine;
        this.focus = 1;
        initViewPositions();
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    /** 
     * This method sets the layout to newLayout
     * @return: void
     * @post : getLayout() == newLayout
     */
    private void setLayout(Layout newLayout) {
        this.layout = newLayout;
    }

    /** 
     * This method returns the layout
     * @return: Layout, the layout of Textr
     */
    protected Layout getLayout() {
        return layout;
    }

    /** 
     * This method returns the newLine
     * @return: String, the newLine
     */
    private String getNewLine() {
        return newLine;
    }

    /** 
     * This method sets the focus to newFocus
     * @return: void
     * @post : getFocus() == newFocus
     */
    private void setFocus(int newFocus) {
        this.focus = newFocus;
    }

    /** 
     * This method returns the focussed view
     * @return: int, the index of the focussed view
     * Visible for testing
     */
    int getFocus() {
        return focus;
    }

    /* **************
     *      RUN     *
     * **************/

    /** 
     * This method runs the main loop of the program and checks for the input and handles it
     * @return: void
     */
    private void run() throws IOException {
        while (getLayout() != null) {
            int c = 0;
            try {
                c = terminalHandler.readByte(getNextDeadline());
            } catch (TimeoutException e) {
                tick();
                if (getFocusedView().getTick() != 0) show();
            }

            if (c == 27) {                          //  ARROWS
                int c1 = terminalHandler.readByte();
                if (c1 == 91) {
                    int c2 = terminalHandler.readByte();
                    if (c2 == 65) {
                        arrowPressed(Direction.NORD);  //UP
                    } else if (c2 == 66) {
                        arrowPressed(Direction.SOUTH);   //DOWN
                    } else if (c2 == 67) {
                        arrowPressed(Direction.EAST);   //RIGHT
                    } else if (c2 == 68) {
                        arrowPressed(Direction.WEST);  //LEFT
                    }
                }
            }                               // Shift + F4
            else if (c == 59) {
                int c1 = terminalHandler.readByte();
                if (c1 == 50) {
                    int c2 = terminalHandler.readByte();
                    if (c2 == 83) {
                        closeView();
                    }
                }
            }
            else if (c == 4) {
                duplicateView();            //  Ctrl + D
            }
            else if (c == 7) {
                openGameView();             //  Ctrl + G
                changeFocusNext();
            }
            else if (c == 13) {             //  ENTER
                addNewLineBreak();
            }
            else if (c == 21) {             //  Ctrl + U
                redo();
            }
            else if (c == 26) {             //  Ctrl + Z
                undo();
            }
            else if (c == 127) {
                deleteChar();               //  BACKSPACE
            }
            else if (c == 14) {             //  Ctrl + N
                changeFocusNext();
            }
            else if (c == 16) {             //  Ctrl + P
                changeFocusPrevious();
            }
            else if (c == 18) {             //  Ctrl + R
                rotateView(1);
            }
            else if (c == 20) {             //  Ctrl + T
                rotateView(-1);
            }
            else if (c == 19) {             //  Ctrl + S
                saveBuffer();
            }
            else if (c >= 32 && c <= 126) { //  Legal Chars
                addNewChar((char) c);
            }
            if (getLayout() == null) break;
            if (getFocusedView().getTick() == 0 && c != 0) show();
            else if (getFocusedView().getTick() != 0) show();
        }
    }

    /* **********************
     *  DERIVED ATTRIBUTES  *
     * **********************/

    /** 
     * This method returns the focussed view
     * @return: FileBufferView
     * Visible for testing
     */
    View getFocusedView() {
        return getLayout().getFocusedView(getFocus());
    }

    /* ******************
     *  INSPECT CONTENT *
     * ******************/

    /** 
     * This method changes the focus to the next view
     * It also updates the cursor's position and optionally the scroll states if needed
     * @return: void
     * Visible for testing
     */
    void changeFocusNext() {
        setFocus(nextFocus());
    }

    /** 
     * This method changes the focus to the previous view
     *  It also updates the cursor's position and optionally the scroll states if needed
     * @return: void
     * Visible for testing
     */
    void changeFocusPrevious() {
        setFocus(previousFocus());
    }

    /** 
     * This method updates the cursor's position and optionally the scroll states if needed when a arrowkey is pressed
     * @param dir this is the direction of the arrowkey that is pressed indicated by an enum
     * @return: void
     */
    void arrowPressed(Direction dir) {
        getLayout().arrowPressed(dir, getFocus());
    }

    /* **********************
     *  EDIT BUFFER CONTENT *
     ************************/

    /** 
     * This method adds a new line break to the focused file buffer at the insertion point. It also updates the
     * cursor's position and optionally the scroll states if needed.
     * @return: void
     * Visible for testing
     */
    void addNewLineBreak() {
        getLayout().addNewLineBreak(getFocus());
    }

    /** 
     * This method adds char c to the focused file buffer at the insertion point
     * It also changes the cursor's position and optionally changes the scroll states and bars if needed
     * @param c this is the char that will be added to the focused filebufferview
     * @return: void
     * Visible for testing
     */
    protected void addNewChar(char c) {
        getLayout().addNewChar(c, getFocus());
    }

    /** 
     * This method deletes the character at the insertion point in the focused file buffer
     *  It also updates the cursor's position and optionally the scroll states if needed
     * @return: void
     * Visible for testing
     */
    void deleteChar() {
        getLayout().deleteChar(getFocus());
    }

    /* ******************
     *   CLOSE BUFFER   *
     * ******************/

    /** 
     * This method closes the focused file buffer and removes it from the layout
     * It also updates the layout and the cursor's position and optionally the scroll states if needed
     * @return: void
     */
    void closeView() throws IOException {
        setLayout(getLayout().closeView(getFocus()));
        if (getLayout() != null) {
            setFocus(getLayout().getNewFocus(getFocus()));
        }
    }

    /* ******************
     *    SAVE BUFFER   *
     * ******************/

    /**
     *  This method saves the focused file buffer
     *  It shows the updated view without dirty sign
     * @return: void
     */
    void saveBuffer() throws IOException {
        getLayout().saveBuffer(getFocus(), getNewLine());
    }

    /* *****************
     *    ROTATE VIEW  *
     * *****************/

    /** 
     * This method rotates the layout, it also updates the layout, the size and the cursor's position and optionally the scroll states if needed
     * @param dir this is an integer that gives the direction in which the view will be rotated
     * @return: void
     */
    void rotateView(int dir) {
        setLayout(getLayout().rotateView(dir, getFocus()));
    }

    /* ******************
     *  DUPLICATE VIEW  *
     * ******************/
    /**
     * This method duplicates the focused view and adds it to the layout
     * @return: void
     * Visible for testing
     */

    void duplicateView() {
        setLayout(getLayout().newBufferView(getFocus()));
    }

    /* ******************
     *  OPEN GAME VIEW  *
     * ******************/

    /**
     * This method opens a new game view and adds it to the layout
     * @return: void
     * Visible for testing
     */

    void openGameView() {
        setLayout(getLayout().newGame(getFocus()));
    }

    /* ******************
     *   UNDO / REDO    *
     * ******************/

    /** 
     * This method undoes the last action in the focused filebufferview
     * @return: void
     * Visible for testing
     */

    void undo() {
        getLayout().undo(getFocus());
    }

    /** 
     * This method redoes the last action in the focused filebufferview
     * @return: void
     * Visible for testing
     */
    void redo() {
        getLayout().redo(getFocus());
    }

    /* ****************
     *    RUN SNAKE   *
     * ****************/
    
    /** 
     * This method ticks the view, it doesn't do anything if the focussedview is a filebufferview, but it does if it's a gameview
     * @return: void
     * Visible for testing
     */
    void tick() throws IOException {
        getLayout().tick(getFocus());
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    /** 
     * This method ensures that the layout and the cursor is well shown on the terminal
     * @return: void
     */
    void show() {
        terminalHandler.clearScreen();
        setFocus(getLayout().getNewFocus(getFocus()));
        View focused = getFocusedView();
        initViewPositions();
        setFocus(focused.getPosition());
        getLayout().show();
        showCursor();
    }

    /** 
     * This method shows the cursor and moves the cursor's position
     * @return: void
     */
    private void showCursor() {
        View focussed = getFocusedView();
        Point cursor = focussed.getCursor();
        terminalHandler.moveCursor(cursor.getX(), cursor.getY());
    }

    /* ******************
     *  HELP FUNCTIONS  *
     * ******************/

    /** 
     * This method sets the focus to the next focus and returns the next focus
     * @return: int, the index of next focus
     */
    private int nextFocus() {
        return getLayout().getNextFocus(getFocus());
    }

    /** 
     * This method sets the focus to the previous focus and returns the previous focus
     * @return: int, the index of previous focus
     */
    private int previousFocus() {
        return getLayout().getPreviousFocus(getFocus());
    }

    /** 
     * This method initializes the view positions
     * @return: void 
     * Visible for testing
     */
    void initViewPositions() {
        getLayout().initViewPosition(1);
    }

    /** 
     * This method updates the size of the layout to the given height and width and sets the leftUpperCorner to (1,1)
     * @param heigth this is the new height of the layout
     * @param width this is the new width of the layout
     * @return: void
     * Visible for testing
     */
    void updateSize(int heigth, int width) {
        getLayout().updateSize(heigth, width, new Point(1,1));
    }

    /** 
     * This method returns the next deadline
     * @return: long, the next deadline
     * Visible for testing
     */
    long getNextDeadline() {
        return getLayout().getNextDeadline(getFocus());
    }

    /** 
     * This method returns the size of the terminalHandler
     * @return: Point, the size of the terminalHandler
     */
    private Point getSize() throws IOException {
        terminalHandler.reportTextAreaSize();
        TerminalParser parser = new TerminalParser();
        for (int i = 0; i < 4; i++) {
            parser.eatByte();
        }
        int heigth = parser.expectNumber();
        parser.expect(';');
        int width = parser.expectNumber();
        parser.expect('t');
        return new Point(heigth, width);
    }
}
