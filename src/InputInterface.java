import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface InputInterface {
    void clearScreen();
    void init();
    void close(int openWindows);
    void prepareToClose();
    void moveCursor(int row, int column);
    void printText(int row, int column, String text);
    int readByte() throws IOException;
    int readByte(long deadline) throws IOException, TimeoutException;
    int response(long deadline) throws IOException, TimeoutException;
    Point getArea() throws IOException;
    void setInputListener(Runnable runnable);
    void clearInputListener();

}
