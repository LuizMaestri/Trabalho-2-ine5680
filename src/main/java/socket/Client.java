package socket;

import cript.DH;
import cript.RSA;
import exception.NoImplementedException;
import utils.Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Scanner;

public class Client extends ISocket {

    public Client() throws NoSuchProviderException, NoSuchAlgorithmException {
        rsa = new RSA();
    }

    public void run(String host, int port) throws NoImplementedException {
        try {
            Socket server = new Socket(host, port);
            System.out.println("O cliente se conectou ao servidor!");
            keyExchnge(server, "SERVER", "");
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

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | NoSuchProviderException | IllegalBlockSizeException e) {
            System.out.println("Not possible encrypt message");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public void run(int port) throws NoImplementedException {
        throw new NoImplementedException("This method is not implemented on client");
    }




}
