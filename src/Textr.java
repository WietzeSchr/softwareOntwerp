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

    public Textr(String[] filepaths){
        Point size;
        try {
            size = getSize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (filepaths.length > 1) {
            this.layout = new StackedLayout((int) size.getX(), (int) size.getY(), new Point(1,1), filepaths);
        }
        else {
            this.layout = new FileBufferView((int) size.getX(), (int) size.getY(), new Point(1, 1), filepaths[0]);
        }
        this.newLine = System.lineSeparator();
        this.focus = 1;
        show();
        initViewPositions();
        updateCursor();
        for (;;) {}
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
        getLayout().show();
    }

    private void initViewPositions() {
        getLayout().initViewPosition(1);
    }

    private void updateCursor() {
        FileBufferView focus = getFocusedView();
        Point cursor = focus.getInsertionPoint();
        Terminal.moveCursor((int) cursor.getX(), (int) cursor.getY());
    }

}
