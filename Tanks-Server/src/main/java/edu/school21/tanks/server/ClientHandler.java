package edu.school21.tanks.server;

import edu.school21.tanks.app.Main;
import edu.school21.tanks.models.Player;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private final Server            server;
    private final Socket            socket;
    private final InputStream       in;
    private final OutputStream      out;
    private final boolean           first;
    private int                     hp;

    public ClientHandler(Socket socket, boolean first, Server server) throws IOException {
        this.socket = socket;
        this.first = first;
        this.server = server;
        hp = 100;
        in = socket.getInputStream();
        out = socket.getOutputStream();
        new ReadMsg().start();
        sendMap();
    }

    private void close() throws IOException {
        socket.close();
        in.close();
        out.close();
    }

    public void sendResults(Player player) throws IOException {
        out.write(player.getShots() >> 8);
        out.write(player.getShots());
        out.write(player.getHits());
        out.write(this.hp == 0 ? 0 : 1);
        close();
    }

    protected void sendMap() throws IOException {
        if (first) {
            synchronized (server) {
                for (int i = 0; i < Server.MAP_HEIGHT; i++) {
                    out.write(Server.map[i]);
                }
            }
        } else {
            synchronized (server) {
                for (int i = Server.MAP_HEIGHT - 1; i >= 0; i--) {
                    out.write(inverseArray(Server.map[i]));
                }
            }
        }

    }

    private byte[] inverseArray(byte[] arr) {
        int len = arr.length;
        byte[] res = new byte[len];
        for (int i = 0; i < len; i++) {
            if (arr[i] == 0) {
                res[len - i - 1] = 0;
            } else if (arr[i] == 'E') {
                res[len - i - 1] = 'U';
            } else if (arr[i] == 'U') {
                res[len - i - 1] = 'E';
            } else if (arr[i] == 'u') {
                res[len - i - 1] = 'e';
            } else if (arr[i] == 'e') {
                res[len - i - 1] = 'u';
            } else if (arr[i] == 'T') {
                res[len - i - 1] = 'B';
            } else if (arr[i] == 'B') {
                res[len - i - 1] = 'T';
            } else if (arr[i] == 'L') {
                res[len - i - 1] = 'L';
            }
        }
        return res;
    }

    public boolean checkHit() {
        if (first) {
            int ux = findUX();
            for (int i = ux - 40 - 2; i < ux + 40 + 2 && i < Server.MAP_WIDTH; i++) {
                for (int j = Server.U_HEIGHT + 40 + 5; j > Server.U_HEIGHT - 40 - 5; j--) {
                    if (i > 0 && Server.map[j][i] == 'T') {
                        synchronized (server) {
                            this.hp -= 5;
                            Main.playersRepository.updateHits(Main.playersRepository.findById(1L));
                            Server.map[j][i] = 0;
                            if (this.hp > 0) {
                                Server.map[Server.U_HEIGHT][ux] = 'u';
                            } else {
                                Server.map[Server.U_HEIGHT][ux] = 'L';
                                Server.gameOver = true;
                            }
                            return true;
                        }
                    }
                }
            }
        } else {
            int ex = findEX();
            for (int i = ex - 40 - 2; i < ex + 40 + 2 && i < Server.MAP_WIDTH; i++) {
                for (int j = Server.ENEMY_HEIGHT + 40 + 5; j > Server.ENEMY_HEIGHT - 40 - 5; j--) {
                    if (i > 0 && Server.map[j][i] == 'B') {
                        synchronized (server) {
                            this.hp -= 5;
                            Main.playersRepository.updateHits(Main.playersRepository.findById(0L));
                            Server.map[j][i] = 0;
                            if (this.hp > 0) {
                                Server.map[Server.ENEMY_HEIGHT][ex] = 'e';
                            } else {
                                Server.map[Server.ENEMY_HEIGHT][ex] = 'L';
                                Server.gameOver = true;
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    protected int findUX() {
        for (int i = 0; i < Server.MAP_WIDTH; i++) {
            if (Server.map[Server.U_HEIGHT][i] == 'U') {
                return i;
            }
        }
        return 0;
    }

    protected int findEX() {
        for (int i = 0; i < Server.MAP_WIDTH; i++) {
            if (Server.map[Server.ENEMY_HEIGHT][i] == 'E') {
                return i;
            }
        }
        return 0;
    }

    private class ReadMsg extends Thread {
        @Override
        public void run() {
            int msg;
            try {
                while (!Server.gameOver) {
                    msg = in.read();
                    int y = first ? Server.U_HEIGHT : Server.ENEMY_HEIGHT;
                    synchronized (server) {
                        if (first) {
                            if (msg == 'L') {
                                for (int i = 50; i < Server.MAP_WIDTH - 40; i++) {
                                    if (Server.map[y][i] == 'U') {
                                        Server.map[y][i - 10] = 'U';
                                        Server.map[y][i] = 0;
                                        break;
                                    }
                                }
                            } else if (msg == 'R') {
                                for (int i = 40; i < Server.MAP_WIDTH - 50; i++) {
                                    if (Server.map[y][i] == 'U') {
                                        Server.map[y][i + 10] = 'U';
                                        Server.map[y][i] = 0;
                                        break;
                                    }
                                }
                            } else {
                                for (int i = 0; i < Server.MAP_WIDTH; i++) {
                                    if (Server.map[y][i] == 'U') {
                                        Server.map[y - 45][i] = 'B';
                                        Main.playersRepository.updateShots(Main.playersRepository.findById(0L));
                                        break;
                                    }
                                }
                            }
                        } else {
                            if (msg == 'L') {
                                for (int i = 40; i < Server.MAP_WIDTH - 50; i++) {
                                    if (Server.map[y][i] == 'E') {
                                        Server.map[y][i + 10] = 'E';
                                        Server.map[y][i] = 0;
                                        break;
                                    }
                                }
                            } else if (msg == 'R') {
                                for (int i = 50; i < Server.MAP_WIDTH - 40; i++) {
                                    if (Server.map[y][i] == 'E') {
                                        Server.map[y][i - 10] = 'E';
                                        Server.map[y][i] = 0;
                                        break;
                                    }
                                }
                            } else {
                                for (int i = 0; i < Server.MAP_WIDTH; i++) {
                                    if (Server.map[y][i] == 'E') {
                                        Server.map[y + 45][i] = 'T';
                                        Main.playersRepository.updateShots(Main.playersRepository.findById(1L));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    Server.sendMap();
                }
            } catch (IOException e) {
            }
        }
    }
}