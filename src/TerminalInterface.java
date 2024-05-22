import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface TerminalInterface {
    public void clearScreen();
    public void init();
    public void close();
    public void moveCursor(int row, int column);
    public void printText(int row, int column, String text);
    public int readByte() throws IOException;
    public int readByte(long deadline) throws IOException, TimeoutException;
    public int response(long deadline) throws IOException, TimeoutException;
    public Point getArea() throws IOException;
    public void setInputListener(Runnable runnable);
}
