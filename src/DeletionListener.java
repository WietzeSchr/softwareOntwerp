public class DeletionListener implements FileBufferListenerInterface {
    
    private final FileBufferView view;
    
    public DeletionListener(FileBufferView view) {
        this.view = view;
    }
    
    public FileBufferView getView() {
        return view;
    }
    
    public void update(char c, Point insert) {
        if (c == (char) 13) {
            getView().updateViewDelLineBreak(insert);
        }
        else {
            getView().updateViewDelChar(insert);
        }
    }
}
