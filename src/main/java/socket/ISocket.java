package socket;

import cript.RSA;
import exception.NoImplementedException;
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
import java.security.spec.InvalidKeySpecException;

public abstract class ISocket {

    private RSA rsa;
    private PublicKey pubKey;
    private String store;
    SecretKey secretKey;

    ISocket(String store) throws NoSuchProviderException, NoSuchAlgorithmException {
        this.store = store;
        this.rsa = new RSA();
        try {
            rsa.getPrivateKey(store);
        } catch (CertificateException | KeyStoreException | URISyntaxException | IOException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
    }

    public abstract void run(String host, int port) throws NoImplementedException;
    abstract void run(int port) throws NoImplementedException;

    private void sendPublicKey(Socket socket){
        try {
            PublicKey publicKey = rsa.getPublicKey(store);
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(publicKey);
        } catch (IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | NoSuchProviderException | KeyStoreException | URISyntaxException | InvalidKeySpecException e) {
            System.out.println("Not Possible send key from " + store.replace(".jks", ""));
        }
    }

    private void recivePublicKey(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        pubKey = (PublicKey) input.readObject();
    }

    void keyExchange(Socket socket) throws IOException, ClassNotFoundException {
        sendPublicKey(socket);
        recivePublicKey(socket);
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
            NoSuchProviderException, InvalidKeyException, IOException, UnrecoverableKeyException, CertificateException,
            KeyStoreException, URISyntaxException {
        DataInputStream input = new DataInputStream(socket.getInputStream());
        int length = input.readInt();
        if(length>0) {
            byte[] bytes = new byte[length];
            input.readFully(bytes, 0, length);
            String cipherKey =  Utils.toHex(bytes);
            //decifrando chave secreta com a chave publica
            PrivateKey privateKey = rsa.getPrivateKey(store);
            String key = rsa.decrypt(cipherKey, privateKey);
            byte[] keyBytes = Utils.toByteArray(key);
            secretKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
        }
    }
}
