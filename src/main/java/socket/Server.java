package socket;

import cript.DH;
import cript.RSA;
import exception.NoImplementedException;
import utils.Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Scanner;

public class Server extends ISocket {

    public Server() throws NoSuchProviderException, NoSuchAlgorithmException {
        rsa = new RSA();
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
            keyExchnge(client, "CLIENT", "");
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
            e.printStackTrace();
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | NoSuchProviderException | IllegalBlockSizeException e) {
//            System.out.println("Not possible decrypt message");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

}
