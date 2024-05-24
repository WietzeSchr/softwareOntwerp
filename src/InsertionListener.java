/* **********************
 *  INSERTION LISTENER  *
 * **********************/
public class InsertionListener implements FileBufferListenerInterface {

    private final FileBufferView view;

    /**
     * This constructor creates a new InsertionListener for the given view
     * @param view  The view to notify
     */
    public InsertionListener(FileBufferView view) {
        this.view = view;
    }

    /**
     * This method returns the view
     * @return  FileBufferView
     */
    public FileBufferView getView() {
        return view;
    }

    /**
     * This method updates the view
     * @param c         The character that was added || (char) 13 if line break was added
     * @param insert    The point of insert
     */
    public void update(char c, Point insert) {
        if (c == (char) 13) {
            getView().updateViewNewLineBreak(insert);
        }
        else {
            getView().updateViewNewChar(insert);
        }
    }
}
