package team.anoml.node.util;

import org.apache.commons.codec.digest.DigestUtils;
import team.anoml.node.core.FileTable;
import team.anoml.node.core.RoutingTable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.logging.Level;

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

        File file = File.createTempFile(fileName, ".data");

        int length = (random.nextInt(10) + 1) * 1024 * 1024;

        byte[] array = new byte[length];
        random.nextBytes(array);
        String s = new String(array, StandardCharsets.UTF_8);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(s);
        }

        return file;
    }
}
