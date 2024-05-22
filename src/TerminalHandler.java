import io.github.btj.termios.Terminal;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TerminalHandler implements TerminalInterface{


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

    TerminalHandler(){}
    @Override
    public void clearScreen() { Terminal.clearScreen();
    }
    @Override
    public void init() {
        Terminal.enterRawInputMode();
        Terminal.clearScreen();
    }
    @Override
    public void close() {
        Terminal.clearScreen();
        Terminal.leaveRawInputMode();
    }
    @Override
    public void moveCursor(int row, int column) {
        Terminal.moveCursor(row, column);
    }
    @Override
    public void printText(int row, int column, String text) {
        Terminal.printText(row, column, text);
    }
    @Override
    public int readByte() throws IOException { return Terminal.readByte();}
    @Override
    public int readByte(long deadline) throws IOException, TimeoutException {return Terminal.readByte(deadline);}
    public Point getArea() throws IOException {
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

    @Override
    public void setInputListener(Runnable runnable) {
        Terminal.setInputListener(runnable);
    }

    @Override
    public void clearInputListener() {Terminal.clearInputListener();}

    @Override
    public int response(long deadline) throws IOException, TimeoutException {
        return readByte(deadline);
    }
}
