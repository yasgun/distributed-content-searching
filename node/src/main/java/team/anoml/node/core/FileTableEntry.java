package team.anoml.node.core;

public class FileTableEntry {

    private String fileName;
    private String sha;

    public FileTableEntry(String fileName, String sha) {
        this.fileName = fileName;
        this.sha = sha;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSHA() {
        return sha;
    }
}
