package utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static java.io.File.separator;

public class Utils {

    public static String toHex(byte[] data) {
        return Arrays.toString(data);
    }

    public static byte[] toByteArray (String text){
        text = text.replace("[", "");
        text = text.replace("]", "");
        String[] split = text.split(", ");
        int length = split.length;
        byte[] bytes = new byte[length];
        for (int index = 0; index < length; index++) {
            bytes[index] = Byte.parseByte(split[index]);
        }
        return bytes;
    }

    public static String toText(byte[] data) {
        return new String(data);
    }

    public static File getStoreFile(String fileName) throws IOException {
        String dir = "storeFiles";
        String workiingDir = System.getProperty("user.dir");
        return new File(workiingDir + separator + dir + separator + fileName);
//        return new FileInputStream(keyStore);
    }
}
