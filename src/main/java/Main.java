import exception.NoImplementedException;
import socket.Client;
import socket.ISocket;
import socket.Server;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Main {

    public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException {
        ISocket socket = new Server();
        int port = 12345;
        String host = "127.0.0.1";
        for (String arg : args) {
            if (arg.equals("help")){
                help();
            }
            if (arg.equals("CLIENT")){
                socket = new Client();
            }
            if (arg.contains("PORT=")){
                String[] split = arg.split("=");
                port = Integer.parseInt(split[1]);
            }

            if (arg.contains("HOST=")){
                host = arg.split("=")[1];
            }
        }
        try {
            socket.run(host, port);
        } catch (NoImplementedException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void help() {
        System.out.println("PARAMETERS");
        System.out.println("CLIENT\tRun as client");
        System.out.println("PORT=port\tdefine run port(default: 12345)");
        System.out.println("HOST=host\tdefine server host to connect (default: 127.0.0.1)");
    }
}
