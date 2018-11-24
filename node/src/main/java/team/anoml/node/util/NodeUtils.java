package team.anoml.node.util;

import org.apache.commons.codec.digest.DigestUtils;
import team.anoml.node.core.FileTable;
import team.anoml.node.core.RoutingTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class NodeUtils {

    public static void printRoutingTable(RoutingTable routingTable) {

    }

    public static void printFileTable(FileTable fileTable) {

    }

    public static String getMD5Hex(File file) throws IOException {
        return DigestUtils.md5Hex(new FileInputStream(file)).toUpperCase();
    }
}
