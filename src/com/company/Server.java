package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Server {
    Socket socket1, socket2;
    BufferedReader input1, input2;
    PrintWriter writer1, writer2;

    public Server() throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(1321)){
            socket1 = serverSocket.accept();
            System.out.println(1);
            writer1 = new PrintWriter(socket1.getOutputStream());
            writer1.println("X");
            writer1.flush();
            socket2 = serverSocket.accept();
            writer2 = new PrintWriter(socket2.getOutputStream());
            writer2.println("O");
            writer2.flush();
            System.out.println(2);
            input1 = new BufferedReader(new InputStreamReader(socket1.getInputStream(), StandardCharsets.UTF_8));
            input2 = new BufferedReader(new InputStreamReader(socket2.getInputStream(), StandardCharsets.UTF_8));
        }catch (IOException e){
            e.printStackTrace();
        }
        while (true){
            String s = "";
            try {
                    if(input1.ready()) {
                        s = input1.readLine();
                        System.out.println(s);
                        writer2.println(s);
                        writer2.flush();
                    }
                if(input2.ready()) {
                    s = input2.readLine();
                    System.out.println(s);
                    writer1.println(s);
                    writer1.flush();
                }
            }catch (SocketException e){
                new Server();
            }
        }
    }
}
