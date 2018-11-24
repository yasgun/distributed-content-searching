package team.anoml.node.util;

import org.apache.commons.codec.digest.DigestUtils;
import team.anoml.node.core.FileTable;
import team.anoml.node.core.RoutingTable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class NodeUtils {

    public static void printRoutingTable(RoutingTable routingTable) {

    }

    public static void printFileTable(FileTable fileTable) {

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
        Files.write(file, array);

        return file.toFile();
    }
}
