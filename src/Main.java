import io.github.btj.termios.Terminal;

import java.io.IOException;

public class Main {

    public static void main(String[] args)
    {
        Terminal.clearScreen();
        new Textr(args);
        Terminal.enterRawInputMode();
        Terminal.clearScreen();
    }
}

