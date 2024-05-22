
import io.github.btj.termios.Terminal;

import javax.swing.*;

import java.io.FileNotFoundException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/* ******************
 *      TEXTR       *
 * ******************/

public class Textr implements InputListener, KeyBoardFocusListener
{
    TerminalHandler stdHandler = new TerminalHandler();
    TerminalInterface inputHandler = stdHandler;


    interface FallibleRunnable {
        void run() throws Throwable;
    }

    void handleFailure(FallibleRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            inputHandler.close();
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
            this.layoutManager = new LayoutManager(new StackedLayout(size.getX(), size.getY(), new Point(1,1), filepaths, newLine), 1, newLine);
        }
        else {
            this.layoutManager = new LayoutManager(new FileBufferView(size.getX(), size.getY(), new Point(1, 1), filepaths[0], newLine), 1, newLine);
        }
        inputHandler.init();
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
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    LayoutManager getLayoutManager() {return this.layoutManager;}

    /** 
     * This method returns the layout
     * @return   | Layout, the layout of Textr
     */
    protected Layout getLayout() {
        return getLayoutManager().getLayout();
    }

    void setInputHandler(TerminalInterface handler){
        inputHandler = handler;
    }

    void resetInputHandler(){
        setInputHandler(stdHandler);
    }

    TerminalInterface getInputHandler(){
        return inputHandler;
    }


    /** 
     * This method returns the focussed view
     * @return  | int, the index of the focussed view
     * Visible for testing
     *
     * Only used in testing -> delete
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
        class App {
            App() {
                JFrame dummyFrame = new JFrame();
                dummyFrame.pack();
                inputHandler.setInputListener(new Runnable() {
                    public void run(){
                        java.awt.EventQueue.invokeLater(() -> handleFailure(() -> {
                            int c = inputHandler.readByte(getNextDeadline());
                            tick();
                            if (layoutManager.getFocusedView().getTick() != 0) show();

                            // Arrows
                            if (c == 27) {
                                int c1 = inputHandler.readByte();
                                if (c1 == 91) {
                                    int c2 = inputHandler.readByte();
                                    if (c2 == 65) {
                                        handleInput(-2);
                                    } else if (c2 == 66) {
                                        handleInput(-3);
                                    } else if (c2 == 67) {
                                        handleInput(-4);
                                    } else if (c2 == 68) {
                                        handleInput(-5);
                                    }
                                }
                            }
                            // Shift + F4
                            else if (c == 59) {
                                int c1 = inputHandler.readByte();
                                if (c1 == 50) {
                                    int c2 = 0;
                                    c2 = inputHandler.readByte();
                                    if (c2 == 83) {
                                        handleInput(-6);
                                    }
                                }
                            }

                            // Ctrl keybindings + legal chars
                            else {handleInput(c);}

                            if (layoutManager.getFocusedView().getTick() == 0 && c != 0) show();
                            else if (layoutManager.getFocusedView().getTick() != 0) show();
                            inputHandler.setInputListener(this);
                        }));
                    }
                });
            }

        }
        new App();
    }


    public void handleInput(int input) throws IOException {
        switch (input){
            case -2:           // UP
                arrowPressed(Direction.NORD);
                break;
            case -3:           // DOWN
                arrowPressed(Direction.SOUTH);
                break;
            case -4:           //RIGHT
                arrowPressed(Direction.EAST);
                break;
            case -5:           // LEFT
                arrowPressed(Direction.WEST);
                break;
            case -6:           // Shift + F4
                closeView(inputHandler);
                break;
            case 4:             // Ctrl + D
                duplicateView();
                break;
            case 7:             // Ctrl + G
                openGameView();
                changeFocusNext();
                break;
            case 14:            // Ctrl + N
                changeFocusNext();
                break;
            case 16:            // Ctrl + P
                changeFocusPrevious();
                break;
            case 18:            // Ctrl + R
                rotateView(1);
                break;
            case 19:            // Ctrl + S
                saveBuffer();
                break;
            case 20:            // Ctrl + T
                rotateView(-1);
                break;
            case 21:            // Ctrl + U
                redo();
                break;
            case 23:            // Ctrl + W
                openWindow();
                break;
            case 26: case -1:   // Ctrl + Z
                undo();
                break;
            case 13:            // Enter
                addNewLineBreak();
                break;
            case 127:           // Backspace
                deleteChar();
                break;

            default:            // Legal Chars
                if(input >= 32 && input <= 126) {
                    addNewChar((char)input);
                }
        }
        show();
    }

    /**
     * Listener for keyEvent coming in from SwingWindow
     * @param key : the int value of the pressed key
     */
    @Override
    public void respondTo(int key) {
        try {
            handleInput(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Listener
     * @param focussed
     */
    @Override
    public void updateKeyboardFocus(TerminalInterface focussed) {
        if(focussed==null)  {resetInputHandler();}
        else                {setInputHandler(focussed);}
    }

    /* **********************
     *  DERIVED ATTRIBUTES  *
     * **********************/

    long getTick() {
        return getLayoutManager().getTick();
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
    void addNewLineBreak() throws FileNotFoundException {
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
    void closeView(TerminalInterface printer) throws IOException {
        getLayoutManager().closeView(printer);
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

    void openWindow() {
        getLayoutManager().openWindow(this);
    }

    void openDirectoryView() {
        getLayoutManager().openDirectoryView();
    }

    void parseJson() {
        getLayoutManager().parseJson();
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    /** 
     * This method shows the layout on the Terminal
     * @return: void
     */
    void show() {
        inputHandler.clearScreen();
        getLayoutManager().show(inputHandler);
        showCursor();
    }

    /** 
     * This method shows the cursor at the focused view's insertion point
     * @return: void
     */
    private void showCursor() {
        Point cursor = getLayoutManager().getCursor();
        inputHandler.moveCursor(cursor.getX(), cursor.getY());
    }

    /*
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
     * This method returns the size of the inputHandler
     * @return  | Point, the size of the inputHandler
     */
    private Point getSize() throws IOException {
        return inputHandler.getArea();
    }
}
