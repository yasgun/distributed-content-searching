package team.anoml.node.core.service;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileCreator {

    private static final Logger logger = Logger.getLogger(FileCreator.class.getName());
    private static final Random random = new Random();

    public File createFile(String fileName) throws IOException {

        File file = File.createTempFile(fileName, ".data");

        int length = (random.nextInt(10) + 1) * 1024 * 1024;

        byte[] array = new byte[length];
        random.nextBytes(array);
        String s = new String(array, StandardCharsets.UTF_8);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(s);
        }

        logger.log(Level.INFO, "Temporary file generated with random content");

        String md5Hash = DigestUtils.md5Hex(new FileInputStream(file)).toUpperCase();
        String sha1Hash = DigestUtils.sha1Hex(new FileInputStream(file)).toUpperCase();

        logger.log(Level.INFO, "checksum value (md5): \"{}\"", md5Hash);
        logger.log(Level.INFO, "checksum value (sha1): \"{}\"", sha1Hash);

        return file;
    }
}
