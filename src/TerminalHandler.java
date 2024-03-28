import io.github.btj.termios.Terminal;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TerminalHandler implements TerminalInterface{

    TerminalHandler(){}
    @Override
    public void clearScreen() {
        Terminal.clearScreen();
    }
    @Override
    public void enterRawInputMode() {
        Terminal.enterRawInputMode();
    }
    @Override
    public void leaveRawInputMode() {
        Terminal.enterRawInputMode();
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
    @Override
    public void reportTextAreaSize() {
        Terminal.reportTextAreaSize();
    }
}
