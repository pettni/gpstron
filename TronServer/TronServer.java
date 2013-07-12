//Opens the server and sends connected clients sockets
//to a ControlThread object

import java.io.*;
import java.net.*;

public class TronServer {

    private static ServerSocket serverSocket;

    public static void main(String[] x) {
        int port = 4554;
        //Loop for checking command line arguments
        for (int i = 0; i < x.length; i++) {
            if (x[i].equals("-P")) {
                port = Integer.valueOf(x[i + 1]).intValue();
            }
            if (x[i].equals("-h") | x[i].equals("--help")) {
                System.out.println("Arguments: \n" +
                        "-P             Port \n" +
                        "--help/-h      Help (this)");
                System.exit(0);
            }
        }

        ControlThread ct = new ControlThread();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Now listening on port " + port);
        } catch (IOException ie) {
            System.out.println("Could not bind to port " + port);
            System.exit(0);
        }

        while (true) {
            try {
                Socket s = serverSocket.accept();
                if (!ct.gameOn) {
                    new PlayerThread(s, ct);
                } else {
                    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                    out.println("/GST");
                    s.close();
                }
            } catch (IOException ie) {
                System.out.println("Player connect failed");
            }
        }
    }
}
