package store;

import org.apache.commons.io.IOUtils;
import utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            InputStream storeFile = Utils.getStoreFile(fileName);
            KeyStore store = load(storeFile, pass);
            IOUtils.copy(storeFile, output);
            Certificate[] cert = new Certificate[1];
            cert[0] = store.getCertificate("cert");
            store.setKeyEntry(alias, key, pass, cert);
            store.store(output, pass);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | NoSuchProviderException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
            output.close();
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
        InputStream storeFile = Utils.getStoreFile(fileName);
        KeyStore store = load(storeFile, pass);
        return store.getKey(alias, pass);
    }

    private static KeyStore load(InputStream storeFile, char[] pass)
            throws CertificateException, NoSuchAlgorithmException, IOException, NoSuchProviderException, KeyStoreException {
        KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
//        FileInputStream input = new FileInputStream(storeFile);
        store.load(storeFile, pass);
        return store;
    }
}
