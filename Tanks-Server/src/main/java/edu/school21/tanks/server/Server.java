package edu.school21.tanks.server;

import edu.school21.tanks.app.Main;
import edu.school21.tanks.models.Player;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    protected static final int MAP_HEIGHT = 480;
    protected static final int MAP_WIDTH = 640;
    protected static final int U_HEIGHT = 419;
    protected static final int ENEMY_HEIGHT = 60;
    private ServerSocket server;
    protected static ClientHandler[] connections;
    protected static byte[][] map;
    protected static boolean gameOver = false;

    public Server(int port, ApplicationContext context) throws IOException {
        server = new ServerSocket(port);
        connections = new ClientHandler[2];
        map = initMap();
    }

    private static byte[][] initMap() {
        byte[][] map = new byte[MAP_HEIGHT][MAP_WIDTH];
        for (int i = 0; i < MAP_HEIGHT; i++) {
            for (int j = 0; j < MAP_WIDTH; j++) {
                map[i][j] = 0;
            }
        }
        map[ENEMY_HEIGHT][MAP_WIDTH / 2] = 'E';
        map[U_HEIGHT][MAP_WIDTH / 2] = 'U';
        return map;
    }

    public void start() throws IOException, InterruptedException {
        Socket player1 = server.accept();
        connections[0] = new ClientHandler(player1, true, this);
        Socket player2 = server.accept();
        connections[1] = new ClientHandler(player2, false, this);
        Main.playersRepository.save(new Player(0L, 0, 0));
        Main.playersRepository.save(new Player(1L, 0, 0));
        BulletMover bulletMover = new BulletMover();
        bulletMover.start();
        bulletMover.join();
        sendResults();
    }

    protected static synchronized void sendMap() throws IOException {
        connections[0].sendMap();
        connections[1].sendMap();
    }

    private void sendResults() throws IOException {
        Player player1 = Main.playersRepository.findById(0L);
        connections[0].sendResults(player1);
        Player player2 = Main.playersRepository.findById(1L);
        connections[1].sendResults(player2);
    }

    private class BulletMover extends Thread {
        @Override
        public void run() {
            while (!Server.gameOver) {
                synchronized (this) {
                    for (int i = ENEMY_HEIGHT - 40; i < U_HEIGHT - 40; i++) {
                        for (int j = 40; j < Server.MAP_WIDTH - 40; j++) {
                            if (Server.map[i][j] == 'B') {
                                    if (Server.map[i - 1][j] == 0) {
                                        Server.map[i - 1][j] = 'B';
                                        Server.map[i][j] = 0;
                                    } else if (Server.map[i - 1][j] == 'T') {
                                        Server.map[i - 1][j] = 'D';
                                        Server.map[i][j] = 0;
                                    }
                            } else if (Server.map[i][j] == 'D') {
                                Server.map[i - 1][j] = 'B';
                                Server.map[i][j] = 'T';
                            }
                        }
                    }
                    for (int j = 40; j < Server.MAP_WIDTH - 40; j++) {
                        if (Server.map[ENEMY_HEIGHT - 41][j] == 'B') {
                            Server.map[ENEMY_HEIGHT - 41][j] = 0;
                        }
                    }
                    for (int i = U_HEIGHT + 40; i >=  ENEMY_HEIGHT + 40; i--) {
                        for (int j = 40; j < Server.MAP_WIDTH - 40; j++) {
                            if (Server.map[i][j] == 'T') {
                                if (Server.map[i + 1][j] == 0) {
                                    Server.map[i + 1][j] = 'T';
                                    Server.map[i][j] = 0;
                                } else if (Server.map[i + 1][j] == 'B') {
                                    Server.map[i + 1][j] = 'D';
                                    Server.map[i][j] = 0;
                                }
                            } else if (Server.map[i][j] == 'D') {
                                Server.map[i + 1][j] = 'T';
                                Server.map[i][j] = 'B';
                            }
                        }
                    }
                    for (int j = 40; j < Server.MAP_WIDTH - 40; j++) {
                        if (Server.map[U_HEIGHT + 41][j] == 'T') {
                            Server.map[U_HEIGHT + 41][j] = 0;
                        }
                    }
                }
                try {
                    boolean hit = connections[0].checkHit();
                    hit |= connections[1].checkHit();
                    Server.sendMap();
                    if (hit) {
                        synchronized (this) {
                            for (int j = 40; j < MAP_WIDTH - 40; j++) {
                                if (map[U_HEIGHT][j] == 'u') {
                                    map[U_HEIGHT][j] = 'U';
                                }
                                if (map[ENEMY_HEIGHT][j] == 'e') {
                                    map[ENEMY_HEIGHT][j] = 'E';
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
