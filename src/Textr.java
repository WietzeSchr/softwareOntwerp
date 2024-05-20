import io.github.btj.termios.Terminal;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;


/* ******************
 *      TEXTR       *
 * ******************/

public class Textr
{
    static TerminalHandler terminalHandler = new TerminalHandler();

    interface FallibleRunnable {
        void run() throws Throwable;
    }

    static void handleFailure(FallibleRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            terminalHandler.close();
            t.printStackTrace();
            System.exit(1);
        }
    }

    private LayoutManager layoutManager;

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
            this.layoutManager = new LayoutManager(new StackedLayout(1,1, new Point(1,1), filepaths, newLine), 1, newLine);
        }
        else {
            this.layoutManager = new LayoutManager(new FileBufferView(1,1, new Point(1, 1), filepaths[0], newLine), 1, newLine);
        }
        updateSize(size.getX(), size.getY());
        initViewPositions();
        show();
        runApp();
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
        this.layoutManager = new LayoutManager(layout, 1, newLine);
        initViewPositions();
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    private LayoutManager getLayoutManager() {return this.layoutManager;}

    /** 
     * This method sets the layout to newLayout
     * @post     | getLayout() == newLayout
     * @return   |void
     */
    private void setLayout(Layout newLayout) {
        getLayoutManager().setLayout(newLayout);
    }

    /** 
     * This method returns the layout
     * @return   | Layout, the layout of Textr
     */
    protected Layout getLayout() {
        return getLayoutManager().getLayout();
    }

    /** 
     * This method sets the focus to newFocus

     * @param newFocus this is the new focus that will be set
     * @return: void
     * @post : getFocus() == newFocus
     */
    private void setFocus(int newFocus) {
        getLayoutManager().setFocus(newFocus);
    }

    /** 
     * This method returns the focussed view
     * @return  | int, the index of the focussed view
     * Visible for testing
     */
    int getFocus() {
        return getLayoutManager().getFocus();
    }

    /* **************
     *      RUN     *
     * **************/

    /** 
     * This method runs the main loop of the program, reading bytes from user input and calling the appropriate methods
     * for handling user input. The loop stops when all files are closed (layout == null)
     * @return  | void
     */
    public void runApp() {
        terminalHandler.init();
        class App {

            App() {
                javax.swing.Timer timer = new javax.swing.Timer(100, e -> handleFailure(() -> show()));
                //timer.start();

                JFrame dummyFrame = new JFrame();
                dummyFrame.pack();
                terminalHandler.setInputListener(new Runnable() {
                    public void run(){
                        java.awt.EventQueue.invokeLater(() -> handleFailure(() -> {
                            int c = terminalHandler.readByte(getNextDeadline());
                            tick();
                            if (getFocusedView().getTick() != 0) show();

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
                            }
                            else if (c == 59) {
                                int c1 = terminalHandler.readByte();
                                if (c1 == 50) {
                                    int c2 = 0;
                                    c2 = terminalHandler.readByte();
                                    if (c2 == 83) {         // Shift + F4
                                        closeView();
                                    }
                                }
                            } else if (c == 4) {
                                duplicateView();            //  Ctrl + D
                            } else if (c == 7) {
                                openGameView();             //  Ctrl + G
                                changeFocusNext();
                            } else if (c == 13) {             //  ENTER
                                addNewLineBreak();
                            } else if (c == 21) {             //  Ctrl + U
                                redo();
                            } else if (c == -1 || c == 26) {   //  Ctrl + Z
                                undo();
                            } else if (c == 127) {
                                deleteChar();               //  BACKSPACE
                            } else if (c == 14) {             //  Ctrl + N
                                changeFocusNext();
                            } else if (c == 16) {             //  Ctrl + P
                                changeFocusPrevious();
                            } else if (c == 18) {             //  Ctrl + R
                                rotateView(1);
                            } else if (c == 20) {             //  Ctrl + T
                                rotateView(-1);
                            } else if (c == 19) {             //  Ctrl + S
                                saveBuffer();
                            } else if (c == 23) {             //  Ctrl + W
                                openWindow();
                            } else if (c >= 32 && c <= 126) { //  Legal Chars
                                addNewChar((char) c);
                            }
                            if (getFocusedView().getTick() == 0 && c != 0) show();
                            else if (getFocusedView().getTick() != 0) show();
                            terminalHandler.setInputListener(this);
                        }));
                    }
                });
            }
        }
        new App();
    }

    /* **********************
     *  DERIVED ATTRIBUTES  *
     * **********************/

    /** 
     * This method returns the focussed view
     * @return  | View
     * Visible for testing
     */
    View getFocusedView() {
        return getLayoutManager().getFocusedView();
    }

    /* ******************
     *  INSPECT CONTENT *
     * ******************/

    /** 
     * This method changes focus to the next view and
     * moves the cursor's position to the new focus' insertion point
     * Visible for testing
     */
    void changeFocusNext() {getLayoutManager().changeFocusNext();
    }

    /** 
     * This method changes the focus to the previous view and
     * moves the cursor's position to the new focus' insertion point
     * @return: void
     * Visible for testing
     */
    void changeFocusPrevious() {getLayoutManager().changeFocusPrevious();
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
        getLayoutManager().arrowPressed(dir);
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
        getLayoutManager().addNewLineBreak();
    }

    /** 
     * This method handles character key presses. If the focused view is a FileBufferView, character is added
     * at its insertion point. If the focused view is a GameView, nothing happens
     * @param c  | The char that was pressed
     * @return   | void
     * Visible for testing
     */
    protected void addNewChar(char c) {
        getLayoutManager().addNewChar(c);
    }

    /** 
     * This method handles backspace key presses. If the focused view is a FileBufferView, a character or line break
     * is deleted at its insertion point. If the focused view is a GameView, nothing happens
     * @return   | void
     * Visible for testing
     */
    void deleteChar() { getLayoutManager().deleteChar();
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
        getLayoutManager().closeView();
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
        getLayoutManager().saveBuffer();
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
        getLayoutManager().rotateView(dir);
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
        getLayoutManager().duplicateView();
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
        getLayoutManager().openGameView();
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
        getLayoutManager().undo();
    }

    /**
     * This method (tries) to redo the last undo done by the focused view if it is a FileBufferView, possibly
     * changing scrollStates on all FileBufferView with the same FileBuffer as the focused view. If this is a
     * GameView nothing happens
     * @return: void
     * Visible for testing
     */
    void redo() {
        getLayoutManager().redo();
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
        getLayoutManager().tick();
    }

    /* *****************
     * OPEN NEW WINDOW *
     * *****************/
    void openWindow(){
        getLayoutManager().openWindow();
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
        getLayoutManager().show();
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
        return getLayoutManager().getNextDeadline();
    }

    /** 
     * This method returns the size of the terminalHandler
     * @return  | Point, the size of the terminalHandler
     */
    private Point getSize() throws IOException {
        return terminalHandler.getArea();
    }
}
