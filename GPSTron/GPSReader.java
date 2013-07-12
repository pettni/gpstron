//Class that reads gpsd output data through the localhost gpsd socket
//at port 2947
import java.io.*;
import java.net.*;

class GPSReader extends Thread {

    public double latitude;
    public double longitude;
    private Socket gpsSocket;
    private BufferedReader br;
    private BufferedWriter bw;
    private boolean connected;
    private Protocol prot;

    GPSReader(Protocol p) {
        connected = false;
        prot = p;
        start();
    }

    private void connect() {

        try {
            gpsSocket = new Socket(InetAddress.getLocalHost(), 2947);
            br = new BufferedReader(new InputStreamReader(gpsSocket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(gpsSocket.getOutputStream()));
            bw.write("w+r-\n");
            bw.flush();
            connected = true;
            prot.chatvec.add("Connected to GPSd \n");
        } catch (Exception e) {
            connected = false;
        }

    }

    public void run() {
        while (true) {
            if (connected) {
                try {
                    while (connected) {
                        String raw = br.readLine();
                        String[] rawparts = raw.split(" ");
                        if (rawparts[0].compareTo("GPSD,O=GND") == 0 |
                                rawparts[0].compareTo("GPSD,0=MID2") == 0) {
                            latitude = Double.parseDouble(rawparts[3]);
                            longitude = Double.parseDouble(rawparts[4]);
                        }
                    }
                } catch (Exception e) {
                    prot.chatvec.add("GPSd error. Trying to reconnect.. \n");
                    connected = false;
                }
            } else {
                try {
                    Thread.currentThread().sleep(3000);
                } catch (Exception e) {
                }
                connect();
            }
        }
    }
}
