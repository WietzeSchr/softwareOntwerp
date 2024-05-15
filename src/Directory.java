import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Directory extends DirEntry {

    private final Directory[] subDirectories;

    private final File[] files;

    public Directory(String absPath) {
        super(absPath);
        this.subDirectories = readSubDirs();
        this.files = readFiles();
    }

    public Directory(String absPath, Directory parent) {
        super(absPath, parent);
        this.subDirectories = readSubDirs(parent);
        this.files = readFiles();
    }

    public File[] getFiles() {
        return files;
    }

    public Directory[] getSubDirectories() {
        return subDirectories;
    }

    public DirEntry[] getEntries() {
        ArrayList<DirEntry> result = new ArrayList<>();
        result.addAll(Arrays.asList(getSubDirectories()));
        result.addAll(Arrays.asList(getFiles()));
        return result.toArray(new DirEntry[0]);
    }

    public Directory[] readSubDirs() {
        String[] entryNames = listFiles();
        ArrayList<Directory> subDirs = new ArrayList<>();
        if (getParentPath() != null) {
            subDirs.add(null);              // this way it is know that a parent exists but not initialized
        }
        for (int i = 0; i < entryNames.length; i++) {
            if (entryNames[i].charAt(entryNames[i].length() - 1)  == '/') {
                subDirs.add(new Directory(entryNames[i], this));
            }
        }
        return subDirs.toArray(new Directory[0]);
    }

    public File[] readFiles() {
        String[] entryNames = listFiles();
        ArrayList<File> files = new ArrayList<>();
        for (int i = 0; i < entryNames.length; i++) {
            if (entryNames[i].charAt(entryNames[i].length() - 1)  != '/') {
                files.add(new File(entryNames[i]));
            }
        }
        return files.toArray(new File[0]);
    }

    public Directory[] readSubDirs(Directory parent) {
        String[] entryNames = listFiles();
        ArrayList<Directory> subDirs = new ArrayList<>();
        subDirs.add(parent);              // this way it is know that a parent exists but not initialized
        for (int i = 0; i < entryNames.length; i++) {
            if (entryNames[i].charAt(entryNames[i].length() - 1)  == '/') {
                subDirs.add(new Directory(entryNames[i], this));
            }
        }
        return subDirs.toArray(new Directory[0]);
    }

    public String[] listFiles() {
        String absDirPath = getPathString();
        List<String> resultList = new ArrayList<>();
        java.io.File[] files = new java.io.File(absDirPath).listFiles();
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            if (files[i].isFile()) {
                resultList.add(files[i].getAbsolutePath());
            }
            else if (files[i].isDirectory()) {
                resultList.add(files[i].getAbsolutePath() + "/");
            }
        }
        return resultList.toArray(new String[0]);
    }

    Directory openDir(int line) {
        String str = makeContent()[line];
        if (str.equals("..")) {
            if (getEntries()[0] == null) {
                return new Directory(getParentPath());
            }
            else {
                return getParent();
            }
        }
        return getSubDir(str);
    }

    File getFile(String name) {
        for (int i = 0; i < getFiles().length; i++) {
            if (getFiles()[i].getName().equals(name)) {
                return getFiles()[i];
            }
        }
        return null;
    }

    private Directory getSubDir(String dirName) {
        for (int i = 0; i < getSubDirectories().length; i++) {
            if (getSubDirectories()[i] != null && getSubDirectories()[i].toString().equals(dirName)) {
                return getSubDirectories()[i];
            }
        }
        return null;
    }

    public String[] makeContent() {
        ArrayList<String> result = new ArrayList<>();
        DirEntry[] entries = getEntries();
        for (int i = 0; i < entries.length; i++) {
            if (i == 0 && entries[i] == null || i == 0 && getParent() != null) {
                result.add("..");
            }
            else {
                result.add(entries[i].toString());
            }
        }
        return result.toArray(new String[0]);
    }

    public String toString() {
        return getName() + "/";
    }
}
