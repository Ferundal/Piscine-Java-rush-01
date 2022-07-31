package edu.school21.tanks.client.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Connection {
    private final int MAP_HEIGHT = 480;
    private final int MAP_WIDTH = 640;
    private Socket connectionSocket;
    private InputStream in;
    private OutputStream out;

    public int [] getGameResults() {
        int [] results = new int [3];
        try {
            results[0] = (in.read() << 8);
            results[0] += in.read();
            results[1] = in.read();
            results[2] = in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }


    public Connection() { }

    public boolean setConnection(String address) {
        String[] strs = address.split(":");
        try {
            connectionSocket = new Socket(strs[0], Integer.parseInt(strs[1]));
            in = connectionSocket.getInputStream();
            out = connectionSocket.getOutputStream();
        } catch (IOException e) {
            try {
                close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public void close() throws IOException {
        connectionSocket.close();
        in.close();
        out.close();
    }

    public byte[][] getMap() throws IOException {
        byte[][] res = new byte[MAP_HEIGHT][MAP_WIDTH];
        for (int i = 0; i < MAP_HEIGHT; i++) {
            if (in.read(res[i]) != MAP_WIDTH) {
                System.err.println("ayayay");
            }
        }
        return res;
    }

    public void sendMoveLeft() throws IOException {
        out.write('L');
    }

    public void sendMoveRight() throws IOException {
        out.write('R');
    }

    public void sendShoot() throws IOException {
        out.write('S');
    }
}
