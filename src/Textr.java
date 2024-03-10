import io.github.btj.termios.Terminal;
import java.awt.*;
import java.io.IOException;

public class Textr
{
    private Layout layout;

    private String newLine;

    public Textr(String[] filepaths){
        Point size = null;
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
        show();
    }

    private Point getSize() throws IOException {
        for (int i = 0; i < 4; i++) {
            char c = (char) Terminal.readByte();
        }
        int heigth = readNumber();
        int width = readNumber();
        return new Point(heigth, width);
    }

    private int readNumber() throws IOException {
        int res = 0;
        char c = (char) Terminal.readByte();
        while (true) {
            if (c < '0' || c > '9') {
                if (c == ';' || c == 't') {
                    break;
                }
                else {
                    throw new RuntimeException();
                }
            }
            res *= 10;
            res += c - '0';
            c = (char) Terminal.readByte();
        }
        System.out.println(res);
        return res;
    }

    private void show() {
        Terminal.clearScreen();
        layout.show();
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
}
