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
     * @param newLine this is the newLine that will be used in the Textr
     * @param filepaths this is the array of filepaths that will be opened in the Textr
     * @pre  | filePaths.length > 0
     * @pre  | newLine == "\n" || newLine == "\r\n"
     * @post | getNewLine() == newLine
     * @post | getFocus() == 1
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
     * This constructor creates a new Textr object used for testing.
     * @param newLine this is the newLine that will be used in the Textr
     * @param layout this is the layout that will be used in the Textr
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
     * @post     | getLayout() == newLayout
     * @return   |void
     */
    private void setLayout(Layout newLayout) {
        this.layout = newLayout;
    }

    /** 
     * This method returns the layout
     * @return   | Layout, the layout of Textr
     */
    protected Layout getLayout() {
        return layout;
    }

    /** 
     * This method returns the newLine
     * @return  | String, the newLine
     */
    private String getNewLine() {
        return newLine;
    }

    /** 
     * This method sets the focus to newFocus

     * @param newFocus this is the new focus that will be set
     * @return: void
     * @post : getFocus() == newFocus
     */
    private void setFocus(int newFocus) {
        this.focus = newFocus;
    }

    /** 
     * This method returns the focussed view
     * @return  | int, the index of the focussed view
     * Visible for testing
     */
    int getFocus() {
        return focus;
    }

    /* **************
     *      RUN     *
     * **************/

    /** 
     * This method runs the main loop of the program, reading bytes from user input and calling the appropriate methods
     * for handling user input. The loop stops when all files are closed (layout == null)
     * @return  | void
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
     * @return: View
     * Visible for testing
     */
    View getFocusedView() {
        return getLayout().getFocusedView(getFocus());
    }

    /* ******************
     *  INSPECT CONTENT *
     * ******************/

    /** 
     * This method changes focus to the next view and
     * moves the cursor's position to the new focus' insertion point
     * Visible for testing
     */
    void changeFocusNext() {
        setFocus(nextFocus());
    }

    /** 
     * This method changes the focus to the previous view and
     * moves the cursor's position to the new focus' insertion point
     * @return: void
     * Visible for testing
     */
    void changeFocusPrevious() {
        setFocus(previousFocus());
    }

    /** 
     * This method handles arrow key presses. If the focused view is a FileBufferView, it's insertion point
     * is moved is the given direction. Else if it is a gameView, the game (tries) to move the snake in the given
     * direction changing to the next game state
     * @param dir | the direction of the arrowkey that is pressed indicated by an enum
     * @return    | void
     * Visible for testing
     */
    void arrowPressed(Direction dir) {
        getLayout().arrowPressed(dir, getFocus());
    }

    /* **********************
     *  EDIT BUFFER CONTENT *
     ************************/

    /** 
     * This method handles Enter key presses. If the focused view is a FileBufferView, a new line break
     * is inserted at the insertion point. If the focused view is a GameView, this starts a new game if
     * the game was over
     * @return    | void
     * Visible for testing
     */
    void addNewLineBreak() {
        getLayout().addNewLineBreak(getFocus());
    }

    /** 
     * This method handles character key presses. If the focused view is a FileBufferView, character is added
     * at its insertion point. If the focused view is a GameView, nothing happens
     * @param c  | The char that was pressed
     * @return   | void
     * Visible for testing
     */
    protected void addNewChar(char c) {
        getLayout().addNewChar(c, getFocus());
    }

    /** 
     * This method handles backspace key presses. If the focused view is a FileBufferView, a character or line break
     * is deleted at its insertion point. If the focused view is a GameView, nothing happens
     * @return   | void
     * Visible for testing
     */
    void deleteChar() {
        getLayout().deleteChar(getFocus());
    }

    /* ******************
     *   CLOSE BUFFER   *
     * ******************/

    /** 
     * This method closes the focused view. If the focused View is a FileBufferView and the buffer is dirty, the user
     * can press y to discard changes and close the buffer or press n to cancel. If the user doesn't respond in time the
     * request is cancelled automatically.
     * Closing a view results in a resize of the layout and views, possibly changing scrollstates and shown content
     * for FileBufferViews. Resizing a game could result kill the snake if no possible fit is found
     * @return:  | void
     * Visible for testing
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
     * This method saves the focused file buffer and clears the edits in all FileBufferViews that have the same buffer
     * This shows the views now not dirty
     * @return   | void
     * Visible for testing
     */
    void saveBuffer() throws IOException {
        getLayout().saveBuffer(getFocus(), getNewLine());
    }

    /* *****************
     *    ROTATE VIEW  *
     * *****************/

    /** 
     * This method rotates the focused view with the next view counterclockwise or clockwise. If there is only one view,
     * nothing happens. Else this changes the layout and the sizes of the Views, possibly changing scrollStates,
     * shown content, or ending the game if no possible fit for the snake is found.
     * @pre       | dir == 1 || dir == -1
     * @param dir | 1: counterclockwise, -1: clockwise
     * @return    | void
     * Visible for testing
     */
    void rotateView(int dir) {
        setLayout(getLayout().rotateView(dir, getFocus()));
    }

    /* ******************
     *  DUPLICATE VIEW  *
     * ******************/

    /**
     * If the focused view is a FileBufferView, a new FileBufferView is inserted next to the focused view with
     * the same FileBuffer as the focused view. This changes the layout and the sizes of the Views, possibly
     * changing scrollStates, shown content, or ending the game if no possible fit for the snake is found.
     * If the focused view is a GameView nothing happens.
     * @return  | void
     * Visible for testing
     */
    void duplicateView() {
        setLayout(getLayout().newBufferView(getFocus()));
    }

    /* ******************
     *  OPEN GAME VIEW  *
     * ******************/

    /**
     * This method opens a new GameView next to the focused view. This changes the layout and the sizes of the Views,
     * possibly changing scrollStates, shown content, or ending the game if no possible fit for the snake is found.
     * If the focused view is a GameView nothing happens.
     * @return  | void
     * Visible for testing
     */
    void openGameView() {
        setLayout(getLayout().newGame(getFocus()));
    }

    /* ******************
     *   UNDO / REDO    *
     * ******************/

    /** 
     * This method (tries) to undo the last edit done by the focused view if it is a FileBufferView, possibly
     * changing scrollStates on all FileBufferView with the same FileBuffer as the focused view. If this is a
     * GameView nothing happens
     * @return: void
     * Visible for testing
     */
    void undo() {
        getLayout().undo(getFocus());
    }

    /**
     * This method (tries) to redo the last undo done by the focused view if it is a FileBufferView, possibly
     * changing scrollStates on all FileBufferView with the same FileBuffer as the focused view. If this is a
     * GameView nothing happens
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
     * This method ticks the view. If the focused view is a FileBufferView nothing happens. If the focused view is
     * a GameView the game changes to the next game state
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
     * This method shows the layout on the Terminal
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
     * This method shows the cursor at the focused view's insertion point
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
     * This method gives the next focus
     * @return  | int, the index of next focus
     */
    private int nextFocus() {
        return getLayout().getNextFocus(getFocus());
    }

    /** 
     * This method gives the previous focus
     * @return  | int, the index of previous focus
     */
    private int previousFocus() {
        return getLayout().getPreviousFocus(getFocus());
    }

    /** 
     * This method initializes the view positions, assigning all views a position from 1 to the amount of view in the
     * layout. The views are ordered depth first
     * @return  | void
     * Visible for testing
     */
    void initViewPositions() {
        getLayout().initViewPosition(1);
    }

    /** 
     * This method updates the size of the layout to the given height and width and sets the leftUpperCorner to (1,1)
     * @param heigth | The new height of the layout
     * @param width  | The new width of the layout
     * @return       |void
     * Visible for testing
     */
    void updateSize(int heigth, int width) {
        getLayout().updateSize(heigth, width, new Point(1,1));
    }

    /** 
     * This method returns the next deadline. If the focused view is a FileBufferView the nextDeadline is the current
     * time. If the focused view is a GameView the nextDeadline is the time of the last tick + the time in between ticks
     * of the Game
     * @return   | long, the next deadline
     * Visible for testing
     */
    long getNextDeadline() {
        return getLayout().getNextDeadline(getFocus());
    }

    /** 
     * This method returns the size of the terminalHandler
     * @return  | Point, the size of the terminalHandler
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
