import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Directory extends DirEntry {

    private final Directory[] subDirectories;

    private final File[] files;

    public Directory(String absPath) {
        super(absPath);
        this.subDirectories = readSubDir();
        this.files = readFiles();
    }

    public Directory(String absPath, Directory parent) {
        super(absPath, parent);
        this.subDirectories = readSubDir(parent);
        this.files = readFiles();
    }

    private Directory[] getSubDirectories() {
        return subDirectories;
    }

    private File[] getFiles() {
        return files;
    }

    public DirEntry[] getEntries() {
        ArrayList<DirEntry> result = new ArrayList<>();
        Collections.addAll(result, getSubDirectories());
        Collections.addAll(result, getFiles());
        return result.toArray(new DirEntry[0]);
    }

    public File[] readFiles() {
        String absDirPath = getPathString();
        List<File> resultList = new ArrayList<>();
        java.io.File[] flist = new java.io.File(absDirPath).listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isFile()) {
                resultList.add(new File(flist[i].getAbsolutePath(), this));
            }
        }
        return resultList.toArray(new File[0]);
    }

    public Directory[] readSubDir() {
        String absDirPath = getPathString();
        List<Directory> resultList = new ArrayList<>();
        java.io.File[] files = new java.io.File(absDirPath).listFiles();
        if (getParentPath() != null) {
            resultList.add(null);
        }
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            if (files[i].isDirectory()) {
                resultList.add(new Directory(files[i].getAbsolutePath() + "/", this));
            }
        }
        return resultList.toArray(new Directory[0]);
    }

    public Directory[] readSubDir(Directory parent) {
        String absDirPath = getPathString();
        List<Directory> resultList = new ArrayList<>();
        java.io.File[] files = new java.io.File(absDirPath).listFiles();
        if (getParentPath() != null) {
            resultList.add(parent);
        }
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            if (files[i].isDirectory()) {
                resultList.add(new Directory(files[i].getAbsolutePath() + "/", this));
            }
        }
        return resultList.toArray(new Directory[0]);
    }

    public View openEntry(LayoutManager manager, int line, FileBuffer buffer, String newLine) throws FileNotFoundException {
        DirEntry entry = getEntry(line);
        if (entry == null) {
            return new DirectoryView(5,5, new Point(1,1), getParentPath(), manager);
        }
        return entry.open(manager, buffer, newLine);
    }

    public DirEntry getEntry(int line) {
        return getEntries()[line - 1];
    }

    @Override
    public View open(LayoutManager manager, FileBuffer buffer, String newLine) {
        return new DirectoryView(5,5, new Point(1,1), getPathString(), manager);
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
