import java.util.ArrayList;

/* ****************************
 *  BUFFER LISTENER SERVICE   *
 * ****************************/
public class FileBufferListenerService {
    
    private final ArrayList<InsertionListener> insertionListeners = new ArrayList<>();
    
    private final ArrayList<DeletionListener> deletionListeners = new ArrayList<>();

    /**
     * This method returns the insertion listeners
     * @return  insertionListeners
     */
    private ArrayList<InsertionListener> getInsertionListeners() {
        return new ArrayList<>(insertionListeners);
    }

    /**
     * This method returns the deletion listeners
     * @return  deletionListeners
     */
    private ArrayList<DeletionListener> getDeletionListeners() {
        return new ArrayList<>(deletionListeners);
    }

    /**
     * This method adds a new insertion listener to the array list of insertion listeners
     * @param listener  The new listener
     */
    private void addInsertionListener(InsertionListener listener) {
        insertionListeners.add(listener);
    }

    /**
     * This method removes all insertion listeners that notify the given view
     * @param view  View that should not be notified anymore
     */
    private void removeInsertionListener(FileBufferView view) {
        ArrayList<InsertionListener> insertListeners = getInsertionListeners();
        for (InsertionListener listener : insertListeners) {
            if (listener.getView() == view) {
                insertionListeners.remove(listener);
            }
        }
    }

    /**
     * This method adds a new deletion listener to the array list of deletionListeners
     * @param listener  The new deletion listener
     */
    private void addDeletionListener(DeletionListener listener) {
        deletionListeners.add(listener);
    }

    /**
     * This method removes all deletion listeners that notify the given view
     * @param view  View that should not be notified anymore
     */
    private void removeDeletionListener(FileBufferView view) {
        ArrayList<DeletionListener> deleteListeners = getDeletionListeners();
        for (DeletionListener listener : deleteListeners) {
            if (listener.getView() == view) {
                deletionListeners.remove(listener);
            }
        }
    }

    /**
     * This method subscribes a view to be notified about all insertions and deletions;
     * Adds a new insertionListener and a new DeletionListener
     * @param view  The view that wants to be notified
     */
    void subscribeView(FileBufferView view) {
        addInsertionListener(new InsertionListener(view));
        addDeletionListener(new DeletionListener(view));
    }

    /**
     * This method unsubscribes a view, deleting a insertionlistener and a deletion listener
     * @param view  The view that should not be notified anymore
     */
    void unSubscribeView(FileBufferView view) {
        removeInsertionListener(view);
        removeDeletionListener(view);
    }

    /**
     * THis method is used to notify all listeners about a new character
     * @param insert    The point of insert
     */
    void fireNewChar(Point insert) {
        for (InsertionListener listener : insertionListeners) {
            listener.update('x', insert);
        }
    }

    /**
     * This method is used to notify all listeners about a new line break
     * @param insert    The point of insert
     */
    void fireNewLineBreak(Point insert) {
        for (InsertionListener listener : insertionListeners) {
            listener.update((char) 13, insert);
        }
    }

    /**
     * This method notifies all listeners about a character being deleted
     * @param insert    The point of inser
     */
    void fireDelChar(Point insert) {
        for (DeletionListener listener : deletionListeners) {
            listener.update('x', insert);
        }
    }

    /**
     * This method notifies all listeners about a line break being deleted
     * @param insert    The point of insert
     */
    void fireDelLineBreak(Point insert) {
        for (DeletionListener listener : deletionListeners) {
            listener.update((char) 13, insert);
        }
    }
}
