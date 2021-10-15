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
    Vector<Lobby> lobbies;
    ServerSocket serverSocket;
    String list;
    String[] list_mass;
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
        String name = "unnamed";
        Socket socket;
        BufferedReader in;
        PrintWriter out;
        long last_time_answer;
        Lobby wait_in_lobby;
    }

    class Lobby{
        Player player1, player2;

        void add1(Player player){
            player1 = player;
        }
        void add2(Player player){
            player2 = player;
            players.remove(player);
            player1.out.println("start\nX");
            player1.out.flush();
            player2.out.println("start\nO");
            player2.out.flush();
        }
    }

    void add(Socket socket) throws IOException {
        Player player = new Player();
        player.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        player.out = new PrintWriter(socket.getOutputStream());
        player.name = player.in.readLine();
        player.name = player.name.replaceAll(" ", "");
        player.socket = socket;
        player.last_time_answer = System.currentTimeMillis();
        players.add(player);
    }

    void start() throws IOException, InterruptedException {
        lobbies = new Vector<>();
        for(int i = 0; i < 5; ++i){
            lobbies.add(new Lobby());
        }
        new ServerCommand(this).start();

        serverSocket = new ServerSocket(1321);
        wait_for_player.start();
        while (true){
            StringBuilder s = new StringBuilder();
            for(int i = 0; i < players.size(); ++i){
                if(testConnect(players.get(i))) {
                    if(i < players.size() && players.get(i)!=null) s.append(players.get(i).name).append(' ');
                }else players.remove(i);
            }
            for(int i = 0; i < lobbies.size(); ++i){
                lobbyTestConnect(lobbies.get(i));
            }
            list = s.toString();
        }
    }

    boolean testConnect(Player player) throws IOException {
        if(player != null && player.in.ready()){
            String s = player.in.readLine();
            if(s.equals("list")) {
                player.out.println(list);
                player.out.flush();
            }else if(s.equals("test")){

            }else if(s.startsWith("inv")){
                s = s.substring(3);
                for(int j = 0; j < 5; ++j) {
                    if(lobbies.get(j).player1 == null) {
                        lobbies.get(j).add1(player);
                        for (int i = 0; i < players.size(); ++i) {
                            if (s.equals(players.get(i).name)) {
                                invite(players.get(i), lobbies.get(j));
                                break;
                            }
                        }
                        break;
                    }
                }
                players.remove(player);
            }else if(s.equals("yes")){
                    player.wait_in_lobby.add2(player);
                    players.remove(player);
            } else if(s.equals("no")){
                    players.add(player.wait_in_lobby.player1);
                    player.wait_in_lobby.player1 = null;
                    player.wait_in_lobby = null;
            }
                    player.last_time_answer = System.currentTimeMillis();
            return true;
        }
        if(player != null && player.last_time_answer - System.currentTimeMillis() < -3000) return false;
        return true;
    }

    void lobbyTestConnect(Lobby lobby) throws IOException {
        if(lobby.player1 != null && lobby.player1.in.ready()) {
            String s = lobby.player1.in.readLine();
            if(s.charAt(0) < '9' && s.charAt(0) > '0' || s.charAt(0) == '-') {
                    int y = Integer.parseInt(s);
                    if (y > -1) {
                        lobby.player2.out.println(y);
                        lobby.player2.out.flush();

                }
            }
            lobby.player1.last_time_answer = System.currentTimeMillis();
        }
        if(lobby.player2 != null && lobby.player2.in.ready()) {
            String s = lobby.player2.in.readLine();
            if(s.charAt(0) < '9' && s.charAt(0) > '0' || s.charAt(0) == '-') {
                    int y = Integer.parseInt(s);
                    if (y > -1) {
                        lobby.player1.out.println(y);
                        lobby.player1.out.flush();
                    }
            }
            lobby.player2.last_time_answer = System.currentTimeMillis();
        }
        if(lobby.player1 != null && lobby.player1.last_time_answer - System.currentTimeMillis() < -3000) {
            lobby.player1 = null;
            players.add(lobby.player2);
            lobby.player2 = null;
        }
        if(lobby.player2 != null && lobby.player2.last_time_answer - System.currentTimeMillis() < -3000) {
            lobby.player2 = null;
            players.add(lobby.player1);
            lobby.player1 = null;
        }
    }

    void invite(Player player, Lobby lobby){
        player.out.println("invite");
        player.out.flush();
        player.wait_in_lobby = lobby;
    }

}
