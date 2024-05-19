import java.util.ArrayList;
public abstract class Path {

    private String absolutePath;

    private final String delimiter;

    public Path(String absolutePath, String delimiter) {
        this.absolutePath = absolutePath;
        this.delimiter = delimiter;

    }

    public String getPath() {
        return absolutePath;
    }

    void setPath(String newPath) {
        this.absolutePath = newPath;
    }

    private String getDelimiter() {
        return delimiter;
    }

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

    public String getName() {
        String[] filepath = getPath().split(String.valueOf(getDelimiter()));
        filepath = filepath[filepath.length - 1].split("\\\\");
        return filepath[filepath.length - 1];
    }

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

class FilePath extends Path{

    public FilePath(String path) {
        super(path, "/");
        setPath(Path.normalize("/", new java.io.File(path).getAbsolutePath()));
    }
}

class JsonPath extends Path {

    public JsonPath(String absolutePath) {
        super(absolutePath, "/");
    }
}