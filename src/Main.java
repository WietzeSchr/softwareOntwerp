import io.github.btj.termios.Terminal;

public class Main {

    public static void main(String[] args)
    {
        Terminal.enterRawInputMode();
        Terminal.clearScreen();
        String newLine = "";
        if (args[0].equals("--lf") || args[0].equals("-lf")) {
            newLine = "lf";
        }
        else if (args[0].equals("--crlf") || args[0].equals("-crlf")) {
            newLine = "lf";
        }
        else {
            String linesep = System.lineSeparator();
            if (linesep.equals("10")) {
                newLine = "lf";
            }
            else if (linesep.equals("\r\n")) {
                newLine = "crlf";
            }
        }
        new Textr(newLine, args);
        Terminal.clearScreen();
        Terminal.leaveRawInputMode();
    }
}

