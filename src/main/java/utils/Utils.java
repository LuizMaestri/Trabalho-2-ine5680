package utils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import static java.io.File.*;

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

    public static File getStoreFile(String fileName) throws URISyntaxException {
        String dir = "storeFiles";
        ClassLoader classLoader = Utils.class.getClassLoader();
        URL resource = classLoader.getResource(dir + "/" + fileName);
        assert resource != null;
        String file = resource.getFile();
        file = file.replaceAll("%20", " ");
        return new File(file);
    }
}
