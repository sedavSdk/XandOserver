package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

    Vector<Player> players;
    ServerSocket serverSocket;
    String list;
    Thread wait_for_player = new Thread(){
        @Override
        public void run() {
            while (true){
                try {
                    Socket socket = serverSocket.accept();
                    add(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    public Server() {
        players = new Vector<>();
    }

    class Player{
        String waiting_answer, name = "unnamed";
        Socket socket;
        BufferedReader in;
        PrintWriter out;
        long last_time_answer;
    }

    void add(Socket socket) throws IOException {
        Player player = new Player();
        player.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        player.out = new PrintWriter(socket.getOutputStream());
        player.name = player.in.readLine();
        player.socket = socket;
        player.waiting_answer = "list";
        player.last_time_answer = System.currentTimeMillis();
        players.add(player);
    }

    void start() throws IOException, InterruptedException {
        serverSocket = new ServerSocket(1321);
        wait_for_player.start();
        while (true){
            StringBuilder s = new StringBuilder();
            for(int i = 0; i < players.size(); ++i){
                if(testConnect(players.get(i))) {
                    s.append(players.get(i).name).append('\n');
                    answer(players.get(i));
                }else players.remove(i);
            }
            Thread.sleep(1000);
            System.out.print("\033[H\033[2J");
            list = s.toString();
            System.out.println("________________________________________________");
            System.out.println(list);
        }
    }

    boolean testConnect(Player player) throws IOException {
        if(player.in.ready()){
            String s = player.in.readLine();
            switch (s){
                case "list":
                    player.waiting_answer = "list";
                case "test":
                default:
                    player.last_time_answer = System.currentTimeMillis();
            }
            return true;
        }
        if(player.last_time_answer - System.currentTimeMillis() < -5000) return false;
        return true;
    }

    void answer(Player player){
        switch (player.waiting_answer){
            case "list":
                player.out.println(list);
                player.out.flush();
                break;
        }
    }

}
