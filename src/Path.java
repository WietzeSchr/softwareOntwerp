import java.util.ArrayList;
public class Path {

    private final String absolutePath;

    private final char delimiter;

    public Path(String path, char delimiter) {
        String absPath = new java.io.File(path).getAbsolutePath();
        this.absolutePath = normalize(absPath);
        this.delimiter = delimiter;
    }

    public String getPath() {
        return absolutePath;
    }

    private char getDelimiter() {
        return delimiter;
    }

    private String normalize(String path) {
        String[] splittedPath = path.split(String.valueOf(getDelimiter()));
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
                result.append(String.valueOf(getDelimiter()));
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
        if (parentPath.equals(String.valueOf(getDelimiter()))) {
            return null;
        }
        return parentPath;
    }

    public String toString() {
        return String.copyValueOf(absolutePath.toCharArray());
    }
}
