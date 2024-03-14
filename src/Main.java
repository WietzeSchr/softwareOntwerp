import io.github.btj.termios.Terminal;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Terminal.enterRawInputMode();
        Terminal.clearScreen();
        String newLine = "";
        if (args[0].equals("--lf") || args[0].equals("-lf")) {
            newLine = "\n";
        }
        else if (args[0].equals("--crlf") || args[0].equals("-crlf")) {
            newLine = "\r\n";
        }
        else {
            newLine = System.lineSeparator();
        }
        new Textr(newLine, args);
        Terminal.clearScreen();
        Terminal.leaveRawInputMode();
    }
}

