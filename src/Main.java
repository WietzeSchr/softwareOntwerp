import io.github.btj.termios.Terminal;

import java.io.IOException;

public class Main {

    public static void main(String[] args)
    {
        Terminal.clearScreen();
        Terminal.leaveRawInputMode();
        for (int i = 0; i < args.length; i++)
        {
            System.out.println(args[i]);
        }
    }
}

