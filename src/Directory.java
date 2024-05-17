import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Directory extends FileSystemNode {

    public Directory(String absPath) {
        super(absPath, '/');
    }

    public Directory(String absPath, Directory parent) {
        super(absPath, '/', parent);
    }

    @Override
    FileSystemNode[] readSubNodes() {
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

    @Override
    FileSystemNode[] readSubNodes(FileSystemNode parent) {
        String absDirPath = getPathString();
        List<FileSystemNode> resultList = new ArrayList<>();
        java.io.File[] files = new java.io.File(absDirPath).listFiles();
        if (getParentPath() != null) {
            resultList.add(parent);
        }
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            if (files[i].isDirectory()) {
                resultList.add(new Directory(files[i].getAbsolutePath() + "/", this));
            }
        }
        return resultList.toArray(new FileSystemNode[0]);
    }

    @Override
    FileSystemLeaf[] readLeaves() {
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

    @Override
    public View openEntry(LayoutManager manager, int line, FileBuffer buffer, String newLine) throws FileNotFoundException {
        FileSystemEntry entry = getEntry(line);
        if (entry == null) {
            return new DirectoryView(5,5, new Point(1,1), getParentPath(), manager);
        }
        return entry.open(manager, buffer, newLine);
    }

    @Override
    public View open(LayoutManager manager, FileBuffer buffer, String newLine) {
        return new DirectoryView(5,5, new Point(1,1), getPathString(), manager);
    }

    @Override
    public String[] makeContent() {
        ArrayList<String> result = new ArrayList<>();
        FileSystemEntry[] entries = getEntries();
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

    @Override
    public String toString() {
        return getName() + "/";
    }
}
