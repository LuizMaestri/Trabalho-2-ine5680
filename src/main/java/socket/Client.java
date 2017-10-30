package socket;

import cript.DH;
import exception.NoImplementedException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.security.*;
import java.util.Scanner;

public class Client extends ISocket {

    public Client() throws NoSuchProviderException, NoSuchAlgorithmException {
        super("Client.jks");
    }

    public void run(String host, int port) throws NoImplementedException {
        try {
            Socket server = new Socket(host, port);
            System.out.println("O cliente se conectou ao servidor!");
            keyExchange(server);
            secretKey = DH.generateKey();
            sendSecretKey(server);
            Scanner input = new Scanner(System.in);
            PrintStream output = new PrintStream(server.getOutputStream());
            while (input.hasNextLine()) {
                String msg = input.nextLine();
                String cipherText = DH.encrypt(msg, secretKey);
                output.println(cipherText);
            }
            output.close();
            input.close();

        } catch (IOException | InvalidAlgorithmParameterException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | NoSuchProviderException | IllegalBlockSizeException e) {
            System.out.println("Not possible encrypt message");
        }
    }

    public void run(int port) throws NoImplementedException {
        throw new NoImplementedException("This method is not implemented on client");
    }




}
