public class SaveListener implements FileBufferListenerInterface {

    private final FileBufferView view;

    public SaveListener(FileBufferView view) {
        this.view = view;
    }

    public FileBufferView getView() {
        return view;
    }

    public void update(char c, Point insert) {
        //getView().updateViewSaved();
    }
}
