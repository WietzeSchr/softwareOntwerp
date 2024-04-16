import java.io.IOException;

import static java.util.Arrays.copyOfRange;

public class Main {

    public static void main(String[] args) throws IOException {
        TerminalHandler terminalHandler = new TerminalHandler();
        terminalHandler.enterRawInputMode();
        terminalHandler.clearScreen();
        String newLine = "";
        if(args.length < 1) {
            throw new RuntimeException("please give one or more filepaths to open");
        }
        if (args[0].equals("--lf") || args[0].equals("-lf")) {
            newLine = "\n";
            args = copyOfRange(args, 1, args.length);
        } else if (args[0].equals("--crlf") || args[0].equals("-crlf")) {
            newLine = "\r\n";
            args = copyOfRange(args, 1, args.length);
        }
        else {
            newLine = System.lineSeparator();
        }
        new Textr(newLine, args);
        terminalHandler.clearScreen();
        terminalHandler.leaveRawInputMode();
    }
}

