import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface TerminalInterface {
    public void clearScreen();
    public void enterRawInputMode();
    public void leaveRawInputMode();
    public void moveCursor(int row, int column);
    public void printText(int row, int column, String text);
    public int readByte() throws IOException;
    public int readByte(long deadline) throws IOException, TimeoutException;
    public void reportTextAreaSize();
}
