import io.github.btj.termios.Terminal;

public class Main {

    public static void main(String[] args)
    {
        Terminal.enterRawInputMode();
        Terminal.clearScreen();
        new Textr(args);
        Terminal.leaveRawInputMode();
    }
}

