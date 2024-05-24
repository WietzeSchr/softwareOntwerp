/* **********************
 *  DELETION LISTENER   *
 * **********************/
public class DeletionListener implements FileBufferListenerInterface {
    
    private final FileBufferView view;

    /**
     * This constructor creates a new DeletionListener that notifies the given view
     * @param view  The view to be notified
     */
    public DeletionListener(FileBufferView view) {
        this.view = view;
    }

    /**
     * This method returns the view to be notified
     * @return  FileBufferView
     */
    public FileBufferView getView() {
        return view;
    }

    /**
     * This method updates the view
     * @param c         The character that was deleted || (char) 13 if line break is deleted
     * @param insert    The point of insert
     */
    public void update(char c, Point insert) {
        if (c == (char) 13) {
            getView().updateViewDelLineBreak(insert);
        }
        else {
            getView().updateViewDelChar(insert);
        }
    }
}
