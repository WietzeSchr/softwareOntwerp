import io.github.btj.termios.Terminal;

import java.io.IOException;
import java.sql.Array;

public class Main {
    public static void main(String[] args)
    {
        Terminal.clearScreen();
        Terminal.leaveRawInputMode();
        int i = 1;
        int j = 1;
        Terminal.moveCursor(i, j);
        for (;;) {
            try {
                int c = Terminal.readByte();
                if (c > 32 && c < 126) {
                    Terminal.printText(i, j, String.valueOf((char) c));
                    if (j < 50)
                    {
                        j += 1;
                    }
                    else
                    {
                        i += 1;
                        j = 1;
                    }
                    Terminal.moveCursor(i, j);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

