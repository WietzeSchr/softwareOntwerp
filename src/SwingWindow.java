import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class SwingWindow extends JFrame implements TerminalInterface {

    char[][] content;
    int row;
    int col;
    int input;

    SwingWindow(int width, int height) {
        super("Textr");
        setFont(new Font("Monospaced", Font.PLAIN, 12));
        FontMetrics fontMetrics = this.getFontMetrics(getFont());
        int fontWidth = fontMetrics.charWidth('m');
        int fontHeight = fontMetrics.getHeight();
        setSize(fontWidth*width, fontHeight*height);

        content = new char[height][width];
        for (char[] chars : content) Arrays.fill(chars, ' ');
        row = 0;
        col = 0;
    }

    public int getRow() {return this.row;}
    public void setRow(int newRow) { this.row = newRow;}
    public int getCol() {return this.col;}
    public void setCol(int newCol) { this.col = newCol;}

    @Override
    public void clearScreen() {
        for (char[] chars : content) Arrays.fill(chars, ' ');
    }

    @Override
    public void init() {clearScreen();}

    @Override
    public void close() {clearScreen();}

    @Override
    public void moveCursor(int row, int column) {
        if(row<0 || row > content.length) {
            return;
        }
        if(column<0 || column > content[0].length) {
            return;
        }
        setRow(row);
        setCol(column);
    }

    @Override
    public void printText(int row, int column, String text) {
        char[] chars = text.toCharArray();
        for(int i=0; i<chars.length; i++) {
            content[row][column++] = chars[i];
        }
    }

    @Override
    public int readByte() throws IOException {
        return 0;
    }

    @Override
    public int readByte(long deadline) throws IOException, TimeoutException {
        return 0;
    }
    @Override
    public Point getArea() throws IOException {
        return new Point(0,0);
    }

    @Override
    public void setInputListener(Runnable runnable) {}

}
