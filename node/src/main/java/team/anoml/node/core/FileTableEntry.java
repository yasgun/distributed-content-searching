package team.anoml.node.core;

public class FileTableEntry {

    private String fileName;
    private String filePath;

    public FileTableEntry(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

}
