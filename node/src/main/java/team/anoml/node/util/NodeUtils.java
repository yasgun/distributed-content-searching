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
        System.out.printf("%40s %20s", "IP", "Port");
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------");
        for (RoutingTableEntry entry : routingTable.getAllEntries()) {
            System.out.format("%40s %20s", entry.getIPAddress(), entry.getPort());
            System.out.println();
        }
        System.out.println("-----------------------------------------------------------------------------");
    }

    public static void printFileTable(FileTable fileTable) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("%30s %40s", "File Name", "SHA");
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------");
        for (FileTableEntry entry : fileTable.getAllEntries()) {
            System.out.format("%30s %40s", entry.getFileName(), entry.getSHA());
            System.out.println();
        }
        System.out.println("-----------------------------------------------------------------------------");
    }

    public static String getSHAHex(File file) throws IOException {
        return DigestUtils.sha1Hex(new FileInputStream(file)).toUpperCase();
    }

    public synchronized static File createFile(String fileName) throws IOException {
        String filePath = SystemSettings.getFilePath();
        Random random = new Random();

        int length = (random.nextInt(9) + 2) * 1024 * 1024;
        byte[] array = new byte[length];
        random.nextBytes(array);

        Path file = Paths.get(filePath, fileName);
        Files.createDirectories(file.getParent());
        Files.write(file, array, StandardOpenOption.CREATE);

        return file.toFile();
    }

    public synchronized static int[] getDistinctOrderedTwoRandomNumbers(int bound) {
        int[] numbers = new int[2];
        Random random = new Random();

        int randInt1 = random.nextInt(bound);
        int randInt2 = random.nextInt(bound);

        while (randInt1 == randInt2) {
            randInt2 = random.nextInt(bound);
        }

        if (randInt1 < randInt2) {
            numbers[0] = randInt1;
            numbers[1] = randInt2;
        } else {
            numbers[0] = randInt2;
            numbers[1] = randInt1;
        }
        return numbers;
    }
}
