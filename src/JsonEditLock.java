/* *************************
 *   JSON EDIT LOCK CLASS  *
 * *************************/

public class JsonEditLock {

    private int jsonViewCount;

    /* ***************
     *  CONSTRUCTORS *
     *****************/

    public JsonEditLock() {
        this.jsonViewCount = 0;
    }

    /* **********************
     *  GETTERS AND SETTERS *
     * **********************/

    private int getJsonViewCount() {
        return jsonViewCount;
    }

    private void setJsonViewCount(int newJsonViewCount) {
        this.jsonViewCount = newJsonViewCount;
    }

    /* *******************
     *   LOCKING LOGIC   *
     * *******************/

    protected void acquireNewLock() {
        setJsonViewCount(getJsonViewCount() + 1);
    }

    protected void releaseLock() {
        setJsonViewCount(getJsonViewCount() - 1);
    }

    public boolean isLocked() {
        if (getJsonViewCount() > 0) {
            return true;
        }
        return false;
    }
}
