package team.anoml.node.core;

public class FileTableEntry {

    private String fileName;
    private String md5;

    public FileTableEntry(String fileName, String md5) {
        this.fileName = fileName;
        this.md5 = md5;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMd5() {
        return md5;
    }
}
