package team.anoml.node.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileTable {

    private static FileTable fileTable = new FileTable();
    private ConcurrentHashMap<String, FileTableEntry> entries = new ConcurrentHashMap<>();

    private FileTable() {
        //nothing is required here
    }

    public static FileTable getFileTable() {
        return fileTable;
    }

    public void addEntry(FileTableEntry fileTableEntry) {
        entries.put(fileTableEntry.getFileName(), fileTableEntry);
    }

    public void removeEntry(String fileName) {
        entries.remove(fileName);
    }

    public Collection<FileTableEntry> getAllEntries() {
        return entries.values();
    }

    public FileTableEntry getEntryByFileName(String fileName) {
        return entries.get(fileName);
    }

    public Collection<FileTableEntry> getEntriesByFileName(String keyWord) {
        Collection<FileTableEntry> fileTableEntries = new ArrayList<>();
        Set<String> keyWords = new HashSet<>(Arrays.asList(keyWord.toUpperCase().split(" ")));

        for (FileTableEntry entry : getAllEntries()) {
            Set<String> fileNameWords = new HashSet<>(Arrays.asList(entry.getFileName().toUpperCase().split(" ")));
            if (fileNameWords.containsAll(keyWords)) {
                fileTableEntries.add(entry);
            }
        }
        return fileTableEntries;
    }
}
