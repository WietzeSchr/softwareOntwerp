import java.util.ArrayList;

public class FileBufferListenerService {
    
    private ArrayList<InsertionListener> insertionListeners = new ArrayList<>();
    
    private ArrayList<DeletionListener> deletionListeners = new ArrayList<>();

    private ArrayList<SaveListener> saveListeners = new ArrayList<>();

    public ArrayList<InsertionListener> getInsertionListeners() {
        return new ArrayList<>(insertionListeners);
    }

    public void setInsertionListeners(ArrayList<InsertionListener> newInsertionListeners) {
        this.insertionListeners = newInsertionListeners;
    }

    public ArrayList<DeletionListener> getDeletionListeners() {
        return new ArrayList<>(deletionListeners);
    }

    public void setDeletionListeners(ArrayList<DeletionListener> newDeletionListeners) {
        this.deletionListeners = newDeletionListeners;
    }

    public ArrayList<SaveListener> getSaveListeners() {
        return new ArrayList<>(saveListeners);
    }

    public void setSaveListeners(ArrayList<SaveListener> newSaveListeners) {
        this.saveListeners = newSaveListeners;
    }
    
    public void addInsertionListener(InsertionListener listener) {
        insertionListeners.add(listener);
    }
    
    public void removeInsertionListener(FileBufferView view) {
        ArrayList<InsertionListener> insertListeners = getInsertionListeners();
        for (InsertionListener listener : insertListeners) {
            if (listener.getView() == view) {
                insertionListeners.remove(listener);
            }
        }
    }
    
    public void addDeletionListener(DeletionListener listener) {
        deletionListeners.add(listener);
    }
    
    public void removeDeletionListener(FileBufferView view) {
        ArrayList<DeletionListener> deleteListeners = getDeletionListeners();
        for (DeletionListener listener : deleteListeners) {
            if (listener.getView() == view) {
                deletionListeners.remove(listener);
            }
        }
    }

    public void addSaveListener(SaveListener listener) {
        saveListeners.add(listener);
    }

    public void removeSaveListener(FileBufferView view) {
        ArrayList<SaveListener> sListeners = getSaveListeners();
        for (SaveListener listener : sListeners) {
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
