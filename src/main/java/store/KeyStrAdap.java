package store;

import utils.Utils;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;

import static cript.Config.provider;
import static cript.Config.storeType;

public class KeyStrAdap {

    private static char[] pass = "test".toCharArray();

    public static void storeKey(String fileName, char[] password, String alias, Key key)
            throws NoSuchProviderException, KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, URISyntaxException {
        File storeFile = Utils.getStoreFile(fileName);
        KeyStore store = load(storeFile, password);
        assert storeFile != null;
        FileOutputStream output = new FileOutputStream(storeFile);
        store.setKeyEntry(alias, key, pass,null);
        store.store(output, password);
    }

    public static PrivateKey getPrivateKey(String fileName, char[] password, String alias)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException,
            IOException, UnrecoverableKeyException, URISyntaxException {
        return (PrivateKey) getKey(fileName, password, alias);
    }

    public static PublicKey getPublicKey(String fileName, char[] password, String alias)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException,
            IOException, UnrecoverableKeyException, URISyntaxException {
        return (PublicKey) getKey(fileName, password, alias);
    }

    public static SecretKey getSecretKey(String fileName, char[] password, String alias)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException,
            IOException, UnrecoverableKeyException, URISyntaxException {
        return (SecretKey) getKey(fileName, password, alias);
    }

    private static Key getKey(String fileName, char[] password, String alias)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException,
            IOException, UnrecoverableKeyException, URISyntaxException {
        File storeFile = Utils.getStoreFile(fileName);
        KeyStore store = load(storeFile, password);
        return store.getKey(alias, pass);
    }

    private static KeyStore load(File storeFile, char[] password)
            throws CertificateException, NoSuchAlgorithmException, IOException, NoSuchProviderException, KeyStoreException {
        KeyStore store = KeyStore.getInstance(storeType, provider);
        FileInputStream input = new FileInputStream(storeFile);
        store.load(input, password);
        return store;
    }
}
