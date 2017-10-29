package socket;

import cript.DH;
import cript.RSA;
import exception.NoImplementedException;
import store.KeyStrAdap;
import utils.Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;

public abstract class ISocket {

    RSA rsa;
    PublicKey pubKey;
    SecretKey secretKey;

    public abstract void run(String host, int port) throws NoImplementedException;
    abstract void run(int port) throws NoImplementedException;

    private PublicKey getPublicKey(String from)
            throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, IOException, URISyntaxException {
        return KeyStrAdap.getPublicKey("public.keys", "public".toCharArray(), from + "_public");
    }

    private void sendPublicKey(Socket socket, String from){
        try {
//            PublicKey publicKey = getPublicKey(from);
//            if (publicKey == null){
//                System.out.println("Not in store");
//                publicKey = rsa.getPublicKey();
//            }
            PublicKey publicKey = rsa.getPublicKey();
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(publicKey);
        } catch (IOException e) {
            e.printStackTrace();
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (UnrecoverableKeyException e) {
//            e.printStackTrace();
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
        }
    }

    private void recivePublicKey(Socket socket, String receiver) throws IOException, ClassNotFoundException {
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        pubKey = (PublicKey) input.readObject();
//        try {
//            KeyStrAdap.storeKey("public.keys", "public".toCharArray(), receiver + "_public", pubKey);
//        } catch (NoSuchProviderException e) {
//            e.printStackTrace();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        } catch (CertificateException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
    }

    void keyExchnge(Socket socket, String sender, String receiver) throws IOException, ClassNotFoundException {
        sendPublicKey(socket, sender);
        recivePublicKey(socket, receiver);
    }

    void sendSecretKey(Socket socket)
            throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException,
            NoSuchProviderException, InvalidKeyException, IOException {
        String text = Utils.toHex(secretKey.getEncoded());
        //assinando chave simetrica com a chave privada
        String cipherText = rsa.encrypt(text, pubKey);
        byte[] bytes = Utils.toByteArray(cipherText);
        //enviando chave secreta cifrada
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.writeInt(bytes.length);
        output.write(bytes);
        output.flush();
    }

    void reciveSecretKey(Socket socket)
            throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException,
            NoSuchProviderException, InvalidKeyException, IOException {
        DataInputStream input = new DataInputStream(socket.getInputStream());
        int length = input.readInt();
        if(length>0) {
            byte[] bytes = new byte[length];
            input.readFully(bytes, 0, length);
            String cipherKey =  Utils.toHex(bytes);
            //decifrando chave secreta com a chave publica
            PrivateKey privateKey = rsa.getPrivateKey();
            String key = rsa.decrypt(cipherKey, privateKey);
            byte[] keyBytes = Utils.toByteArray(key);
            secretKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
        }
    }
}
