import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class SwingWindow extends JFrame implements InputInterface {

    class TerminalPanel extends JPanel {
        char[][] buffer;

        void clearBuffer() {
            for (char[] chars : buffer) Arrays.fill(chars, ' ');
        }

        TerminalPanel(int width, int heigth) {
            setFont(new Font("Monospaced", Font.PLAIN, 12));
            buffer = new char[heigth][width];
            clearBuffer();
        }

        public void putBuffer(int row, int col, char c){
            row--;
            col--;
            buffer[row][col] = c;
        }

        public void putString(int row, int col, String s){
            char[] chars = s.toCharArray();
            for(int i=0; i< chars.length; i++){
                putBuffer(row, col+i, chars[i]);
            }
        }

        char[][] getBuffer(){
            char[][] bufferCopy = new char[buffer.length][buffer[0].length];
            for(int i=0; i< buffer.length; i++) {
                bufferCopy[i] = Arrays.copyOf(buffer[i], buffer[0].length);
            }
            return bufferCopy;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            FontMetrics fontMetrics = g.getFontMetrics();
            int fontHeight = fontMetrics.getHeight();
            int baseLineOffset = fontHeight - fontMetrics.getDescent();

            int y = baseLineOffset;
            for (int lineIndex = 0; lineIndex < buffer.length; lineIndex++) {
                g.drawChars(buffer[lineIndex], 0, buffer[lineIndex].length, 0, y);
                y += fontHeight;
            }
        }

        @Override
        public Dimension getPreferredSize() {
            FontMetrics fontMetrics = this.getFontMetrics(getFont());
            int width = fontMetrics.charWidth('m');
            return new Dimension(width * buffer[0].length, fontMetrics.getHeight() * buffer.length);
        }

        @Override
        public boolean isFocusable() {
            return true;
        }
    }

    private TerminalPanel terminalPanel;

    private SwingListenerService listenerService;

    int lastKey;

    int row;
    int col;

    SwingWindow(int width, int height, Textr textr) {
        super("Textr");

        terminalPanel = new TerminalPanel(width, height);
        getContentPane().add(terminalPanel);
        updateBuffer();

        listenerService = new SwingListenerService(textr);
        listenerService.fireFocusEvent(this);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        setResizable(false);

        row = 0;
        col = 0;

        terminalPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int input = e.getKeyChar();
                if(e.getKeyCode() == KeyEvent.VK_UP) {input = -2;}
                if(e.getKeyCode() == KeyEvent.VK_DOWN) {input = -3;}
                if(e.getKeyCode() == KeyEvent.VK_RIGHT) {input = -4;}
                if(e.getKeyCode() == KeyEvent.VK_LEFT) {input = -5;}
                if(e.getKeyCode() == KeyEvent.VK_F4) {input = -6;}
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {input = 13;}
                if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {input = 127;}
                if(input > 127 || input < -6){return;}

                lastKey = input;
                try {
                    listenerService.fireKeyEvent(input);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                listenerService.fireFocusEvent((SwingWindow)e.getSource());
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                listenerService.fireFocusEvent(null);
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                listenerService.fireCloseEvent((SwingWindow) e.getSource());
            }

            @Override
            public void windowClosing(WindowEvent e){
                listenerService.fireFocusEvent(null);
            }

            @Override
            public void windowOpened(WindowEvent e) {
                listenerService.fireFocusEvent((SwingWindow) e.getSource());
            }
        });
        pack();
        setLocationRelativeTo(null);

    }

    public SwingListenerService getListenerService(){
        return this.listenerService;
    }

    void updateBuffer() {
        terminalPanel.repaint();
    }


    /**********************
     *  TerminalInterface *
     **********************/

    @Override
    public void clearScreen() {
        terminalPanel.clearBuffer();
        terminalPanel.repaint();
    }

    @Override
    public void init() {}

    @Override
    public void close(int openWindows) {
        dispose();
    }

    @Override
    public void prepareToClose(){}


    @Override
    public void moveCursor(int row, int column) {
        if(row < 1 || row > terminalPanel.getBuffer().length+1) {
            throw new RuntimeException(("RowIndex out of bounds"));
        }
        if(column < 1 || column > terminalPanel.getBuffer()[0].length+1) {
            throw new RuntimeException("ColumnIndex out of bounds");
        }
        this.row = row;
        this.col = column;
    }

    @Override
    public void printText(int row, int column, String text) {
        terminalPanel.putString(row, column, text);
        terminalPanel.repaint();
    }

    @Override
    public int readByte() throws IOException {return 0;}

    @Override
    public int readByte(long deadline) throws IOException, TimeoutException {return 0;}

    @Override
    public Point getArea() throws IOException {
        char[][] buffer = terminalPanel.getBuffer();
        return new Point(buffer.length, buffer[0].length);
    }

    @Override
    public void setInputListener(Runnable runnable) {}

    @Override
    public void clearInputListener() {}

    @Override
    public int response(long deadline){
        int resp = lastKey;
        return resp;
    }

    /**
     * These methods are for testing
     */

    public void testClose(){
        listenerService.fireCloseEvent(this, true);
        dispose();
    }

    public void testKeyInput(int key) throws IOException {
        listenerService.fireKeyEvent(key);
    }

}
