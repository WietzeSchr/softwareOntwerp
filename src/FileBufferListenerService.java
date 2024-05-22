import java.util.ArrayList;

public class FileBufferListenerService {
    
    private final ArrayList<InsertionListener> insertionListeners = new ArrayList<>();
    
    private final ArrayList<DeletionListener> deletionListeners = new ArrayList<>();

    public ArrayList<InsertionListener> getInsertionListeners() {
        return new ArrayList<>(insertionListeners);
    }

    public ArrayList<DeletionListener> getDeletionListeners() {
        return new ArrayList<>(deletionListeners);
    }

    private void addInsertionListener(InsertionListener listener) {
        insertionListeners.add(listener);
    }
    
    private void removeInsertionListener(FileBufferView view) {
        ArrayList<InsertionListener> insertListeners = getInsertionListeners();
        for (InsertionListener listener : insertListeners) {
            if (listener.getView() == view) {
                insertionListeners.remove(listener);
            }
        }
    }
    
    private void addDeletionListener(DeletionListener listener) {
        deletionListeners.add(listener);
    }
    
    private void removeDeletionListener(FileBufferView view) {
        ArrayList<DeletionListener> deleteListeners = getDeletionListeners();
        for (DeletionListener listener : deleteListeners) {
            if (listener.getView() == view) {
                deletionListeners.remove(listener);
            }
        }
    }

    void subscribeView(FileBufferView view) {
        addInsertionListener(new InsertionListener(view));
        addDeletionListener(new DeletionListener(view));
    }

    void unSubscribeView(FileBufferView view) {
        removeInsertionListener(view);
        removeDeletionListener(view);
    }

    void fireNewChar(Point insert) {
        for (InsertionListener listener : insertionListeners) {
            listener.update('x', insert);
        }
    }
    
    void fireNewLineBreak(Point insert) {
        for (InsertionListener listener : insertionListeners) {
            listener.update((char) 13, insert);
        }
    }
    
    void fireDelChar(Point insert) {
        for (DeletionListener listener : deletionListeners) {
            listener.update('x', insert);
        }
    }
    
    void fireDelLineBreak(Point insert) {
        for (DeletionListener listener : deletionListeners) {
            listener.update((char) 13, insert);
        }
    }
}
