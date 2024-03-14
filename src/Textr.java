import io.github.btj.termios.Terminal;
import java.awt.*;
import java.io.File;
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

    private void setLayout(Layout newLayout) {
        this.layout = newLayout;
    }

    private Layout getLayout() {
        return layout;
    }

    private void setNewLine(String newLine1) {
        newLine = newLine1;
    }

    private String getNewLine() {
        return newLine;
    }

    private void setFocus(int newFocus) {
        this.focus = newFocus;
    }

    private int getFocus() {
        return focus;
    }

    private FileBufferView getFocusedView() {
        Layout lay = getLayout();
        return lay.getFocusedView(getFocus());
    }

    private FileBufferView getView(int position) {
        return layout.getFocusedView(position);
    }

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

    private int nextFocus() {
        if (getFocus() < countViews()) {
            return getFocus() + 1;
        }
        return 1;
    }

    private int previousFocus() {
        if (getFocus() > 1) {
            return getFocus() - 1;
        }
        return countViews();
    }
    private void show() {
        Terminal.clearScreen();
        Layout lay = getLayout();
        int heigth = lay.getHeigth();
        int width = lay.getWidth();
        lay.show();
        showCursor();
    }

    private void initViewPositions() {
        layout.initViewPosition(1);
    }

    private void updateSize(int heigth, int width) {
        layout.updateSize(heigth, width, new Point(1,1));
    }

    private void showCursor() {
        FileBufferView focus = getFocusedView();
        Point cursor = focus.getCursor();
        Terminal.moveCursor((int) cursor.getX(), (int) cursor.getY());
    }

    private void updateCursor(int x, int y) throws IOException {
        FileBufferView focus = getFocusedView();
        Point insert = focus.getInsertionPoint();
        showCursor();
    }

    private int countViews() {
        return getLayout().countViews();
    }

    private void run() throws IOException {
        while (countViews() > 0) {
            int c = Terminal.readByte();
            if (c == 10 || c == 13) {
                addNewLineBreak();
            }
            else if (c == 17) {     //  F4
                closeBuffer();
            }
            else if (c == '\033') {
                int c1 = Terminal.readByte();
                if (c1 == ']'){
                    int c2 = Terminal.readByte();
                    if (c2 == 'A') {
                        updateCursor(0, 1);
                    }
                    else if (c2 == 'B') {
                        updateCursor(1,0);
                    }
                    else if (c2 == 'C') {
                        updateCursor(0, -1);
                    }
                    else if (c2 == 'D') {
                        updateCursor(0, 1);
                    }
                }
                else if(c1 == 'O') {
                    int c2 = Terminal.readByte();
                    if(c2 == 'S') {
                        closeBuffer();
                    }
                }
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

    /** Adds a new line break to the focused file buffer at the insertion point. It also updates the
     *  cursor's position and optionally the scroll states if needed.
     */
    private void addNewLineBreak() {
        getFocusedView().addNewLineBreak();
        show();
    }

    /** Adds char c to the focused file buffer at the insertion point. It also changes
     *  the cursor's position and optionally changes the scroll states and bars if needed.
     * @param c : the char which needs to be added
     */
    private void addNewChar(char c) {
        getFocusedView().addNewChar(c);
        show();
    }

    private void closeBuffer() {

    }

    private void changeFocusNext() {
        setFocus(nextFocus());
        show();
    }

    private void changeFocusPrevious() {
        setFocus(previousFocus());
        show();
    }

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

    private void safeBuffer() {

    }
}
