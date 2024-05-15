import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LayoutManager {
    private Layout layout;
    private int focus;
    private String newLine;

    LayoutManager(Layout layout, int focus, String newLine){
        this.layout = layout;
        this.focus = focus;
        this.newLine = newLine;
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
    void setLayout(Layout newLayout) {
        this.layout = newLayout;
        initViewPositions();
    }

    /**
     * This method returns the layout
     * @return   | Layout, the layout of Textr
     */
    Layout getLayout() {
        return this.layout;
    }

    /**
     * This method sets the focus to newFocus

     * @param newFocus this is the new focus that will be set
     * @return: void
     * @post : getFocus() == newFocus
     */
    void setFocus(int newFocus) {
        this.focus = newFocus;
    }

    /**
     * This method returns the newLine
     * @return  | String, the newLine
     */
    private String getNewLine() {
        return this.newLine;
    }

    /**
     * This method returns the focussed view
     * @return  | int, the index of the focussed view
     * Visible for testing
     */
    int getFocus() {
        return this.focus;
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
        return getLayout().getFocusedView(getFocus());
    }

    Point getCursor() {
        return getFocusedView().getCursor();
    }

    long getTick() {
        return getFocusedView().getTick();
    }

    /* ******************
     *  INSPECT CONTENT *
     * ******************/

    /**
     * This method changes focus to the next view and
     * moves the cursor's position to the new focus' insertion point
     * Visible for testing
     */
    void changeFocusNext() {setFocus(nextFocus());}

    /**
     * This method changes the focus to the previous view and
     * moves the cursor's position to the new focus' insertion point
     * @return: void
     * Visible for testing
     */
    void changeFocusPrevious() {setFocus(previousFocus());}

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
    void addNewLineBreak() throws FileNotFoundException {
        View focussedView = getFocusedView();
        replace(focussedView, focussedView.addNewLineBreak(getNewLine()));
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
    void deleteChar() { getLayout().deleteChar(getFocus());
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

    void openDirectoryView() {
        setLayout(getLayout().openDirectoryView(getFocus()));
    }

    /* ******************
     *  SHOW FUNCTIONS  *
     * ******************/

    /**
     * This method shows the layout on the Terminal
     * @return: void
     */
    void show() {
        setFocus(getLayout().getNewFocus(getFocus()));
        View focused = getFocusedView();
        initViewPositions();
        setFocus(focused.getPosition());
        getLayout().show();
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


    /* ************
     *   HELP     *
     * ************/
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
    void replace(View oldView, View newView) {
        if (oldView != newView) {
            if (oldView.getParent() == null) {
                oldView.getParent().replace(oldView, newView);
            } else {
                setLayout(newView);
            }
        }
    }

    void updateSize(int heigth, int width) {
        getLayout().updateSize(heigth, width, new Point(1, 1));
    }
}
