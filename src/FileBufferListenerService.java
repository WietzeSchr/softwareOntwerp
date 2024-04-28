import java.util.ArrayList;

public class FileBufferListenerService {
    
    private final ArrayList<InsertionListener> insertionListeners = new ArrayList<>();
    
    private final ArrayList<DeletionListener> deletionListeners = new ArrayList<>();

    private final ArrayList<SaveListener> saveListeners = new ArrayList<>();
    
    public void addInsertionListener(InsertionListener listener) {
        insertionListeners.add(listener);
    }
    
    public void removeInsertionListener(FileBufferView view) {
        for (InsertionListener listener : insertionListeners) {
            if (listener.getView() == view) {
                insertionListeners.remove(listener);
            }
        }
    }
    
    public void addDeletionListener(DeletionListener listener) {
        deletionListeners.add(listener);
    }
    
    public void removeDeletionListener(FileBufferView view) {
        for (DeletionListener listener : deletionListeners) {
            if (listener.getView() == view) {
                deletionListeners.remove(listener);
            }
        }
    }

    public void addSaveListener(SaveListener listener) {
        saveListeners.add(listener);
    }

    public void removeSaveListener(FileBufferView view) {
        for (SaveListener listener : saveListeners) {
            if (listener.getView() == view) {
                saveListeners.remove(listener);
            }
        }
    }
    
    public void fireNewChar(Point insert) {
        for (InsertionListener listener : insertionListeners) {
            listener.update('x', insert);
        }
    }
    
    public void fireNewLineBreak(Point insert) {
        for (InsertionListener listener : insertionListeners) {
            listener.update((char) 13, insert);
        }
    }
    
    public void fireDelChar(Point insert) {
        for (DeletionListener listener : deletionListeners) {
            listener.update('x', insert);
        }
    }
    
    public void fireDelLineBreak(Point insert) {
        for (DeletionListener listener : deletionListeners) {
            listener.update((char) 13, insert);
        }
    }

    public void fireSaved() {
        for (SaveListener listener : saveListeners) {
            listener.update((char) 17, null);
        }
    }
}
