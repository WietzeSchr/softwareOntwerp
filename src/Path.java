import java.util.ArrayList;

/* ******************
 *    PATH CLASS    *
 * ******************/
public abstract class Path {

    private String absolutePath;

    private final String delimiter;

    /**
     * This constructor creates a new path
     * @param absolutePath  The absolute path
     * @param delimiter     The path delimiter
     */
    public Path(String absolutePath, String delimiter) {
        this.absolutePath = absolutePath;
        this.delimiter = delimiter;

    }

    /**
     * This method returns the absolute path
     * @return  String  Absolute path
     */
    public String getPath() {
        return absolutePath;
    }

    /**
     * This method sets the path to the newPath
     * @param newPath   The new path
     */
    void setPath(String newPath) {
        this.absolutePath = newPath;
    }

    /**
     * This method returns the delimiter
     * @return
     */
    private String getDelimiter() {
        return delimiter;
    }

    /**
     * This method normalizes the path
     *      e.g.    /home/Documents/../Downloads    ->      /home/Downloads
     * @param delimiter The delimiter used
     * @param path      The absolute path
     * @return          String      normalized absolute path
     */
    protected static String normalize(String delimiter, String path) {
        String[] splittedPath = path.split(delimiter);
        ArrayList<String> copy = new ArrayList<>();
        for (String s : splittedPath) {
            if (copy.size() > 0 && s.equals("..")) {
                copy.remove(copy.size() - 1);
            } else {
                copy.add(String.copyValueOf(s.toCharArray()));
            }
        }
        StringBuilder result = new StringBuilder();
        String[] splittedNormalized = copy.toArray(new String[0]);
        for (int i = 0; i < splittedNormalized.length; i++) {
            if (i != 0) {
                result.append(delimiter);
            }
            result.append(splittedNormalized[i]);
        }
        return result.toString();
    }

    /**
     * This method returns the name
     * @return  String
     */
    public String getName() {
        String[] filepath = getPath().split(String.valueOf(getDelimiter()));
        filepath = filepath[filepath.length - 1].split("\\\\");
        return filepath[filepath.length - 1];
    }

    /**
     * This method returns the parent's path
     * @return
     */
    public String getParentPath() {
        String absPath = getPath();
        String name = getName();
        String parentPath = absPath.substring(0, absPath.length() - name.length());
        if (parentPath.equals(getDelimiter())) {
            return null;
        }
        return parentPath;
    }

    public String toString() {
        return String.copyValueOf(absolutePath.toCharArray());
    }
}

/* **************
 *   FILEPATH   *
 * **************/
class FilePath extends Path{

    /**
     * This constructor creates a new FilePath
     * @param path  The absolute (not normalized) path
     */
    public FilePath(String path) {
        super(path, "/");
        setPath(Path.normalize("/", new java.io.File(path).getAbsolutePath()));
    }
}

/* **************
 *   FILEPATH   *
 * **************/
class JsonPath extends Path {

    /**
     * This constructor creates a new JsonPath
     * @param absolutePath  The absolute path
     */
    public JsonPath(String absolutePath) {
        super(absolutePath, "/");
    }
}