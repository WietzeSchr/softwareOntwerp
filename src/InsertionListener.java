public class InsertionListener implements FileBufferListenerInterface {

    private final FileBufferView view;

    public InsertionListener(FileBufferView view) {
        this.view = view;
    }

    public FileBufferView getView() {
        return view;
    }

    public void update(char c, Point insert) {
        if (c == (char) 13) {
            getView().updateViewNewLineBreak(insert);
        }
        else {
            getView().updateViewNewChar(insert);
        }
    }
}
