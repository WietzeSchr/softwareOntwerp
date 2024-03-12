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
        show();
        initViewPositions();
        updateCursor(0, 0);
        run();
    }

    public void setLayout(Layout newLayout) {
        this.layout = newLayout;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setNewLine(String newLine1) {
        newLine = newLine1;
    }

    public String getNewLine() {
        return newLine;
    }

    public void setFocus(int newFocus) {
        this.focus = newFocus;
    }

    public int getFocus() {
        return focus;
    }

    public FileBufferView getFocusedView() {
        Layout lay = getLayout();
        return lay.getFocusedView(getFocus());
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

    private void show() {
        Terminal.clearScreen();
        Layout lay = getLayout();
        int heigth = lay.getHeigth();
        int width = lay.getWidth();
        lay.show();
    }

    private void initViewPositions() {
        getLayout().initViewPosition(1);
    }

    private void updateCursor(int x, int y) {
        FileBufferView focus = getFocusedView();
        Point insertionPoint = focus.getInsertionPoint();
        Point leftUpperPoint = focus.getLeftUpperCorner();
        Point cursor = new Point((int)(insertionPoint.getX() + leftUpperPoint.getX()) + x, (int)(insertionPoint.getY() + leftUpperPoint.getY()) + y);
        Terminal.moveCursor((int) cursor.getX(), (int) cursor.getY());
    }

    private void run() throws IOException {
        while (getLayout().countViews() > 0) {
            int c = Terminal.readByte();
            if (c == 10 || c == 13) {
                addNewLineBreak();
            }
            else if (c == 17) {
                closeBuffer();
            }
            else if (c == '\033') {
                int c1 = Terminal.readByte();
                if (c1 == ']'){
                    int c2 = Terminal.readByte();
                    if (c2 == 'A') {
                        updateCursor(-1, 0);
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
            else if (c == 14) {
                changeFocus(1);
            }
            else if (c == 16) {
                changeFocus(-1);
            }
            else if (c == 18) {
                rotateView(1);
            }
            else if (c == 20) {
                rotateView(-1);
            }
            else if (c == 19) {
                safeBuffer();
            }
            else if (c >= 32 && c <= 126) {
                addNewChar((char) c);
            }
        }
    }

    private void addNewLineBreak() {

    }

    private void addNewChar(char c) {

    }

    private void closeBuffer() {

    }

    private void changeFocus(int dir) {

    }

    private void rotateView(int dir) {

    }

    private void safeBuffer() {

    }
}
