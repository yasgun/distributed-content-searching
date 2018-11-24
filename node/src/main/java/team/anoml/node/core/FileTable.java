package team.anoml.node.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public Collection<FileTableEntry> getEntriesByFileNameRegex(String fileNameRegex) {
        Collection<FileTableEntry> fileTableEntries = new ArrayList<>();
        Pattern pattern = Pattern.compile(fileNameRegex);
        for (Iterator<FileTableEntry> i = getAllEntries().iterator(); i.hasNext(); ) {
            Matcher matcher = pattern.matcher(i.next().getFileName());
            if (matcher.find())
                fileTableEntries.add(i.next());
        }
        return fileTableEntries;
    }
}
