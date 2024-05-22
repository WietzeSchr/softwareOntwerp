public class JsonEditLock {

    private int jsonViewCount;

    public JsonEditLock() {
        this.jsonViewCount = 0;
    }

    private int getJsonViewCount() {
        return jsonViewCount;
    }

    private void setJsonViewCount(int newJsonViewCount) {
        this.jsonViewCount = newJsonViewCount;
    }

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
