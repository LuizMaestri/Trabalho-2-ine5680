package cript;

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import store.KeyStrAdap;
import utils.Utils;

import javax.crypto.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import static utils.Config.*;

public class RSA {

    private KeyPair pair;

    public RSA() throws NoSuchProviderException, NoSuchAlgorithmException {
        Security.addProvider(new BouncyCastleFipsProvider());
        KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm, provider);
        pair = generator.generateKeyPair();
    }

    public PublicKey getPublicKey(String fileName)
            throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException,
            URISyntaxException, NoSuchProviderException, IOException, InvalidKeySpecException {
        PublicKey key = KeyStrAdap.getPublicKey(fileName);
        if (key == null){
            key = pair.getPublic();
        }
        return key;
    }

    public PrivateKey getPrivateKey(String fileName)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, URISyntaxException,
            NoSuchProviderException, IOException, UnrecoverableKeyException {
        PrivateKey key = KeyStrAdap.getPrivateKey(fileName);
        if (key == null){
            key = pair.getPrivate();
            KeyStrAdap.storeKey(fileName, "_private", key);
        }
        return key;
    }

    public String encrypt(String text, Key key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(mode, provider);
        SecureRandom random = CriptUtils.createFixedRandom();
        cipher.init(Cipher.ENCRYPT_MODE, key, random);
        return Utils.toHex(cipher.doFinal(text.getBytes()));
    }

    public String decrypt(String cipherText, Key key)
            throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(mode, provider);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] text = Utils.toByteArray(cipherText);
        byte[] plainText = cipher.doFinal(text);
        return Utils.toText(plainText);
    }
}
