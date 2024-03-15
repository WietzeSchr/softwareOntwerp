import io.github.btj.termios.Terminal;
import java.awt.*;
import java.io.IOException;

class TerminalParser {
    int buffer;
    boolean bufferFull;

    int peekByte() throws IOException {
        if (! bufferFull) {
            buffer = Terminal.readByte();
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

public class Textr
{
    private Layout layout;

    private String newLine;

    private int focus;

    /** This constructor creates a new Textr with the given newLine and filepaths
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
        if (filepaths.length > 1) {
            this.layout = new StackedLayout((int) size.getX(), (int) size.getY(), new Point(1,1), filepaths, newLine);
        }
        else {
            this.layout = new FileBufferView((int) size.getX(), (int) size.getY(), new Point(1, 1), filepaths[0], newLine);
        }
        this.newLine = newLine;
        this.focus = 1;
        initViewPositions();
        show();
        run();
    }

    /** This method sets the layout to newLayout
     * @return: void
     * @post : getLayout() == newLayout
     */
    private void setLayout(Layout newLayout) {
        this.layout = newLayout;
    }

    /** This method returns the layout
     * @return: Layout
     */
    private Layout getLayout() {
        return layout;
    }

    /** This method sets the newLine to newLine1
     * @return: void
     * @post : getNewLine() == newLine1
     */
    private void setNewLine(String newLine1) {
        newLine = newLine1;
    }

    /** This method returns the newLine
     * @return: String
     */
    private String getNewLine() {
        return newLine;
    }

    /** This method sets the focus to newFocus
     * @return: void
     * @post : getFocus() == newFocus
     */
    private void setFocus(int newFocus) {
        this.focus = newFocus;
    }

    /** This method returns the focussed view
     * @return: int
     */
    private int getFocus() {
        return focus;
    }

    /** This method returns the focussed view
     * @return: FileBufferView
     */
    private FileBufferView getFocusedView() {
        Layout lay = getLayout();
        return lay.getFocusedView(getFocus());
    }

    /** This method returns the view at position
     * @return: FileBufferView
     */
    private FileBufferView getView(int position) {
        return layout.getFocusedView(position);
    }

    /** This method returns the size of the terminal
     * @return: Point 
     */
    private Point getSize() throws IOException {
        Terminal.reportTextAreaSize();
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

    /** This method returns the next focus
     * @return: int 
     */
    private int nextFocus() {
        if (getFocus() < countViews()) {
            return getFocus() + 1;
        }
        return 1;
    }
    
    /** This method returns the previous focus
     * @return: int 
     */
    private int previousFocus() {
        if (getFocus() > 1) {
            return getFocus() - 1;
        }
        return countViews();
    }

    /** This method shows the layout and the cursor
     * @return: void 
     */
    private void show() {
        Terminal.clearScreen();
        Layout lay = getLayout();
        int heigth = lay.getHeigth();
        int width = lay.getWidth();
        lay.show();
        showCursor();
    }

    /** This method initializes the view positions
     * @return: void 
     */
    private void initViewPositions() {
        layout.initViewPosition(1);
    }

    /** This method updates the size of the layout
     * @return: void
     */
    private void updateSize(int heigth, int width) {
        layout.updateSize(heigth, width, new Point(1,1));
    }

    /** This method shows the cursor and moves the cursor's position
     * @return: void 
     */
    private void showCursor() {
        FileBufferView focus = getFocusedView();
        Point cursor = focus.getCursor();
        Terminal.moveCursor((int) cursor.getX(), (int) cursor.getY());
    }

    /** This method updates the cursor's position and optionally the scroll states if needed 
     * @return: void 
     */
    private void updateCursor(int x, int y) throws IOException {
        FileBufferView focus = getFocusedView();
        Point insert = focus.getInsertionPoint();
        Point newInsert = new Point((int)insert.getX() + x, (int)insert.getY() + y);
        FileBuffer buffer = focus.getBuffer();
        buffer.setInsertionPoint(newInsert);
        focus.updateScrollStates();
        show();
    }

    /** This method returns the number of views
     * @return: int 
     */
    private int countViews() {
        return getLayout().countViews();
    }

    /** This method runs the main loop of the program and checks for the input and handles it
     * @return: void 
     */
    private void run() throws IOException {
        while (getLayout() != null) {
            int c = Terminal.readByte();
            if (c == 13) {
                addNewLineBreak();
            }
            else if (c == 27) {
                int c1 = Terminal.readByte();
                if (c1 == 91) {
                    int c2 = Terminal.readByte();
                    if (c2 == 65) {
                        updateCursor(-1, 0); //UP
                    } else if (c2 == 66) {
                        updateCursor(1, 0); //DOWN
                    } else if (c2 == 67) {
                        updateCursor(0, 1); //RIGHT
                    } else if (c2 == 68) {
                        updateCursor(0, -1); //LEFT
                    }
                }
            }
            else if (c == 59) {
                int c1 = Terminal.readByte();
                if (c1 == 50) {
                    int c2 = Terminal.readByte();
                    if (c2 == 83) {
                        closeBuffer();
                    }
                }
            }
            else if (c == 127) {
                deleteChar();
            }
            else if (c == 14) {     //  Ctrl + N
                changeFocusNext();
            }
            else if (c == 16) {     //  Ctrl + P
                changeFocusPrevious();
            }
            else if (c == 18) {     //  Ctrl + R
                rotateView(1);
            }
            else if (c == 20) {     //  Ctrl + T
                rotateView(-1);
            }
            else if (c == 19) {     //  Ctrl + S
                safeBuffer();
            }
            else if (c >= 32 && c <= 126) {
                addNewChar((char) c);
            }
        }
    }

    /** This method adds a new line break to the focused file buffer at the insertion point. It also updates the
     *  cursor's position and optionally the scroll states if needed.
     * @return: void
     */
    private void addNewLineBreak() {
        getFocusedView().addNewLineBreak();
        show();
    }

    /** This method adds char c to the focused file buffer at the insertion point
     * It also changes the cursor's position and optionally changes the scroll states and bars if needed
     * @return: void
     */
    private void addNewChar(char c) {
        getFocusedView().addNewChar(c);
        show();
    }

    /** This method deletes the character at the insertion point in the focused file buffer
     *  It also updates the cursor's position and optionally the scroll states if needed
     * @return: void
     */
    private void deleteChar() {
        getFocusedView().deleteChar();
        show();
    }

    /** This method closes the focused file buffer and removes it from the layout
     *  It also updates the layout and the cursor's position and optionally the scroll states if needed
     * @return: void
     */
    private void closeBuffer() throws IOException {
        int heigth = getLayout().getHeigth();
        int width = getLayout().getWidth();
        if (countViews() == 1) {
            setLayout(null);
            return;
        }
        FileBufferView focus = getFocusedView();
        CompositeLayout parent = focus.getParent();
        Layout newLayout = getLayout().closeBuffer(getFocus(), parent);
        setLayout(newLayout);
        initViewPositions();
        if (getFocus() > countViews()) {
            setFocus(getFocus() - 1);
        }
        updateSize(heigth, width);
        show();
    }

    /** This method changes the focus to the next view
     *  It also updates the cursor's position and optionally the scroll states if needed
     * @return: void
     */
    private void changeFocusNext() {
        setFocus(nextFocus());
        show();
    }

    /** This method changes the focus to the previous view
     *  It also updates the cursor's position and optionally the scroll states if needed
     * @return: void
     */
    private void changeFocusPrevious() {
        setFocus(previousFocus());
        show();
    }

    /** This method rotates the layout in the direction of parameter dir
     *  It also updates the layout, the size and the cursor's position and optionally the scroll states if needed
     * @return: void
     */
    private void rotateView(int dir) {
        int height = getLayout().getHeigth();
        int width = getLayout().getWidth();
        Layout newLayout = null;
        if ( countViews() != 1) {
            FileBufferView focus = getFocusedView();
            FileBufferView next = getView(nextFocus());
            if (focus.getParent() == next.getParent()) {
                newLayout = getLayout().rotateView(dir, focus.getParent(), getFocus());
            }
        }
        else {
            newLayout = getLayout();
        }
        setLayout(newLayout);
        updateSize(height, width);
        FileBufferView focus = getFocusedView();
        initViewPositions();
        setFocus(focus.getPosition());
        show();
    }

    /** This method saves the focused file buffer 
     *  It shows the updated view without dirty sign
     * @return: void
     */
    private void safeBuffer() throws IOException {
        getFocusedView().safeBuffer(getNewLine());
        show();
    }
}
