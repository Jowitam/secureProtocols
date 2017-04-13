package com.github.jowitam.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;


public class Application {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InterruptedException {

       if (args.length >= 2 && args[0].equals("client")) {
            System.out.println("uruchamiam w trybie klient");
            client(args[1]);
        } else if (args.length==1 && args[0].equals("server")) {
            System.out.println("uruchamiam w trybie serwer");
            server();
        } else {
            System.out.println("brak wymaganego parametru, uruchom podając parametr: 'client [host]' lub 'server'");
        }
    }

    //metoda haszujaca
    private static byte[] sha1(byte message, byte[] r1, byte[] r2) throws NoSuchAlgorithmException {
        MessageDigest crypto = MessageDigest.getInstance("SHA1");
        crypto.reset();
        crypto.update(r1);
        crypto.update(r2);
        crypto.update(message);
        return crypto.digest();
    }

    // metoda serwera
    private static void server() {
        try {
            ServerSocket serverSocket = new ServerSocket(2000);
            System.out.println("Server start.");

            Socket clientSocket = serverSocket.accept();
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

            Message firstMessage = (Message) inputStream.readObject();

            System.out.println("r1:" + Arrays.toString(firstMessage.getR1()));
            System.out.println("hashMessage:" + Arrays.toString(firstMessage.getHashMessage()));

            Message secondMessage = (Message) inputStream.readObject();

            System.out.println("r1:" + Arrays.toString(secondMessage.getR1()));
            System.out.println("r2:" + Arrays.toString(secondMessage.getR2()));
            System.out.println("com.github.jowitam.security.Message:" + secondMessage.getMessage());

            //porownanie
            if (Arrays.equals(firstMessage.getR1(), secondMessage.getR1())) {
                System.out.println("r1 zgodne");
                byte[] hashMessageSecond = sha1(secondMessage.getMessage(), secondMessage.getR1(), secondMessage.getR2());
                if (Arrays.equals(firstMessage.getHashMessage(), hashMessageSecond)) {
                    System.out.println("Haszująca zgodna");
                    System.out.println("BRAWO!!!");
                }
            } else {
                System.out.println("Niezgodne wiadomości");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //metoda klienta
    private static void client(String host) throws NoSuchAlgorithmException, IOException, InterruptedException {
        //losowa r1
        SecureRandom random = new SecureRandom();
        byte r1[] = new byte[128];
        random.nextBytes(r1);
        // losowa r2
        byte[] r2 = new byte[128];
        random.nextBytes(r2);

        //wiadomosc
        byte message = 22;

        //shaszowana wiadomosc z r1, r2, i message
        byte[] hashMessage = sha1(message, r1, r2);

        System.out.println("tworzenie elementów");
        System.out.println("r1: " + Arrays.toString(r1));
        System.out.println("r2: " + Arrays.toString(r2));
        System.out.println("message: " + message);
        System.out.println("hashMessage: " + Arrays.toString(hashMessage));

        //przeslanie pierwszego pakietu r1 i hassMessage
        Message firstMessage = new Message();
        firstMessage.setR1(r1);
        firstMessage.setHashMessage(hashMessage);

        //druga wiadomosc
        Message secondMessage = new Message();
        secondMessage.setR1(r1);
        secondMessage.setR2(r2);
        secondMessage.setMessage(message);

        //otwarcie socketu i przesłanie 1 wiadomosci
        Socket clientSocket = new Socket(host, 2000);
        ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        System.out.println("przesyłam pierwszą wiadomość");
        outputStream.writeObject(firstMessage);
        Thread.sleep(3000);
        System.out.println("przesyłam drugą wiadomość");
        outputStream.writeObject(secondMessage);
        clientSocket.close();
    }
}
