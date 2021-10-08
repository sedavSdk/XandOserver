package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
    static Server server;
    public static void main(String[] args) throws IOException, InterruptedException {
        server = new Server();
        server.start();
    }
}
