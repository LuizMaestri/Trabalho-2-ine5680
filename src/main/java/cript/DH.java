package cript;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import utils.Utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;

public class DH {

    private static int ivLength = 16;

    public static SecretKey generateKey()
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException {
        Security.addProvider(new BouncyCastleProvider());
        //gerando chaves DH para gerar secret key no client
        ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec("brainpoolp256r1");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH", "BC");
        keyPairGenerator.initialize(parameterSpec);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // gerando secret key para AES
        KeyAgreement keyAgreement =  KeyAgreement.getInstance("ECDH", "BC");
        keyAgreement.init(keyPair.getPrivate());
        keyAgreement.doPhase(keyPair.getPublic(), true);
        return keyAgreement.generateSecret("AES");
    }

    public static String encrypt(String text, Key key)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        SecureRandom r = SecureRandom.getInstance("SHA1PRNG");
        // Generate 128 bit IV for Encryption
        byte[] iv = new byte[ivLength];
        r.nextBytes(iv);

        //TODO gambi derivar chave depois
        int length = 16;
        byte[] bytes = key.getEncoded();
        byte[] derived = new byte[length];
        System.arraycopy(bytes, 0, derived, 0, length);

        //Gerando secret key aparti da derivação
        SecretKeySpec spec = new SecretKeySpec(derived, "AES");
        // cifar texto com cbc
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, spec, new IvParameterSpec(iv));
        byte[] cipherText = c.doFinal(text.getBytes(StandardCharsets.UTF_8));
        // concatenar ic com texto cifrado
        byte[] ivCipher = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, ivCipher, 0, ivLength);
        System.arraycopy(cipherText, 0, ivCipher, ivLength, length);
        return Utils.toHex(ivCipher);
    }

    public static String decrypt(String cipherText, Key key)
            throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException,
            NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException {
        byte[] cipherBytes = Utils.toByteArray(cipherText);

        //TODO gambi derivar chave depois
        int length = 16;
        byte[] bytes = key.getEncoded();
        byte[] derived = new byte[length];
        System.arraycopy(bytes, 0, derived, 0, length);

        // separando IV e texto cifhado
        byte[] iv = new byte[ivLength];
        System.arraycopy(cipherBytes, 0, iv, 0, ivLength);
        byte[] copy = Arrays.copyOfRange(cipherBytes, ivLength, cipherBytes.length);

        //decifrando texto
        SecretKeySpec spec = new SecretKeySpec(derived, "AES");
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, spec, new IvParameterSpec(iv));
        byte[] text = c.doFinal(copy);
        return Utils.toText(text);
    }

}
