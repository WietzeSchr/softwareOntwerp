import io.github.btj.termios.Terminal;

import java.io.IOException;

public class Main {

    public static void main(String[] args)
    {
        Terminal.clearScreen();
        Terminal.leaveRawInputMode();
        new Textr(args);
        Terminal.enterRawInputMode();
        Terminal.clearScreen();
    }
}

