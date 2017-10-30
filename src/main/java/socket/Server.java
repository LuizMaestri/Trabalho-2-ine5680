package socket;

import cript.DH;
import exception.NoImplementedException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Scanner;

public class Server extends ISocket {

    public Server() throws NoSuchProviderException, NoSuchAlgorithmException {
        super("Server.jks");
    }

    public void run(String host, int port) throws NoImplementedException {
        run(port);
    }

    public void run(int port) throws NoImplementedException {
        ServerSocket server;
        try {
            server = new ServerSocket(port);
            System.out.println("Porta 12345 aberta!");
            Socket client = server.accept();
            System.out.println("Nova conexão com o cliente " + client.getInetAddress().getHostAddress());
            keyExchange(client);
            reciveSecretKey(client);
            Scanner scanner = new Scanner(client.getInputStream());
            while (scanner.hasNextLine()) {
                String cipherText = scanner.nextLine();
                String msg = DH.decrypt(cipherText, secretKey);
                System.out.println(msg);
            }
            scanner.close();
            server.close();
        } catch (IOException e) {
            System.out.println("Disconnected from server");
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | NoSuchProviderException | IllegalBlockSizeException e) {
            System.out.println("Not possible decrypt message");
        } catch (ClassNotFoundException | InvalidAlgorithmParameterException e) {
            System.out.println("Error on Key Exchange");
        } catch (CertificateException | UnrecoverableKeyException | KeyStoreException | URISyntaxException e) {
            System.out.println("Error on client Secret key");
        }
    }

}
