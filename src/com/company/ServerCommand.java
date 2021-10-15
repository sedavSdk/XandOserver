package com.company;

import java.util.Scanner;

public class ServerCommand extends Thread{

    Scanner scanner = new Scanner(System.in);
    Server server;
    String[] list_mass;

    public ServerCommand(Server server) {
        this.server = server;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        while (true) {
            String s = scanner.nextLine();
                if (s.equals("list")) {
                    System.out.print("\033[H\033[2J");
                    list_mass = server.list.split(" ");
                    System.out.println("________________________________________________");
                    for (int i = 0; i < list_mass.length; ++i) System.out.println(list_mass[i]);
                    for (int i = 0; i < 5; ++i) {
                        if (server.lobbies.get(i).player1 != null)
                            System.out.print('<' + server.lobbies.get(i).player1.name);
                        if (server.lobbies.get(i).player2 != null)
                            System.out.print("  " + server.lobbies.get(i).player2.name + '>');
                    }
                    System.out.println();
                }
        }
    }
}
