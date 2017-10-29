package cript;

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import utils.Utils;

import javax.crypto.*;
import java.security.*;

import static cript.Config.*;

public class RSA {

    private KeyPair pair;

    public RSA() throws NoSuchProviderException, NoSuchAlgorithmException {
        Security.addProvider(new BouncyCastleFipsProvider());
        KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm, provider);
        pair = generator.generateKeyPair();
    }

    public PublicKey getPublicKey(){
        return pair.getPublic();
    }

    public PrivateKey getPrivateKey(){
        return pair.getPrivate();
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
