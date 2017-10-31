package store;

import utils.Utils;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import static utils.Config.algorithm;

public class KeyStrAdap {

    private static char[] pass = "1qaz2wsx".toCharArray();

    public static void storeKey(String fileName, String alias, Key key) throws IOException {
        FileOutputStream output = null;
        try {
            File storeFile = Utils.getStoreFile(fileName);
            KeyStore store = load(storeFile, pass);
            output = new FileOutputStream(storeFile);
            Certificate[] cert = new Certificate[1];
            cert[0] = store.getCertificate("cert");
            store.setKeyEntry(alias, key, pass, cert);
            store.store(output, pass);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | NoSuchProviderException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                output.close();
            }
        }

    }

    public static PrivateKey getPrivateKey(String fileName)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException,
            IOException, UnrecoverableKeyException, URISyntaxException {
        return (PrivateKey) getKey(fileName, "_private");
    }

    public static PublicKey getPublicKey(String fileName)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException,
            IOException, UnrecoverableKeyException, URISyntaxException, InvalidKeySpecException {
        // pega chave privada do key store
        RSAPrivateCrtKey privk = (RSAPrivateCrtKey) getPrivateKey(fileName);
        // com base na chave privada gera-se a publica
        RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(privk.getModulus(), privk.getPublicExponent());
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(publicKeySpec);
    }

//    public static SecretKey getSecretKey(String fileName) {
//            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException,
//            IOException, UnrecoverableKeyException, URISyntaxException {
//        return (SecretKey) getKey(fileName, "_secret");
//    }

    private static Key getKey(String fileName, String alias)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException,
            IOException, UnrecoverableKeyException, URISyntaxException {
        File storeFile = Utils.getStoreFile(fileName);
        KeyStore store = load(storeFile, pass);
        return store.getKey(alias, pass);
    }

    private static KeyStore load(File storeFile, char[] pass)
            throws CertificateException, NoSuchAlgorithmException, IOException, NoSuchProviderException, KeyStoreException {
        KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream input = new FileInputStream(storeFile);
        store.load(input, pass);
        return store;
    }
}
