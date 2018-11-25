package team.anoml.node.util;

import org.apache.commons.codec.digest.DigestUtils;
import team.anoml.node.core.FileTable;
import team.anoml.node.core.FileTableEntry;
import team.anoml.node.core.RoutingTable;
import team.anoml.node.core.RoutingTableEntry;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

public class NodeUtils {

    public static void printRoutingTable(RoutingTable routingTable) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("%40s %20s %10s", "Ip", "Port", "Validated");
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------");
        for(RoutingTableEntry entry: routingTable.getAllEntries()){
            System.out.format("%40s %20s %10s",
                    entry.getIP(), entry.getPort(), entry.isValidated());
            System.out.println();
        }
        System.out.println("-----------------------------------------------------------------------------");
    }

    public static void printFileTable(FileTable fileTable) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("%30s %40s", "File Name", "md5");
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------");
        for(FileTableEntry entry: fileTable.getAllEntries()){
            System.out.format("%30s %40s",
                    entry.getFileName(), entry.getMd5());
            System.out.println();
        }
        System.out.println("-----------------------------------------------------------------------------");
    }

    public static String getMD5Hex(File file) throws IOException {
        return DigestUtils.md5Hex(new FileInputStream(file)).toUpperCase();
    }

    public static File createFile(String fileName) throws IOException {
        String filePath = SystemSettings.getFilePath();
        Random random = new Random();

        int length = (random.nextInt(10) + 1) * 1024 * 1024;
        byte[] array = new byte[length];
        random.nextBytes(array);

        Path file = Paths.get(filePath, fileName);
        Files.createDirectories(file.getParent());
        Files.write(file, array, StandardOpenOption.CREATE);

        return file.toFile();
    }
}
