//Object that controls all messages received from the server through its socket.
//Also contains several important methods
import java.io.*;
import java.util.*;
import java.net.*;

class Protocol extends Thread {

    public boolean gameOn;
    public boolean isAlive;
    public boolean connected;
    public boolean isReady;
    public int myid;
    public int hostid;
    public Point currentpos, deathpos;
    public Vector<Player> vec;
    public Vector<String> chatvec;
    public Vector<Point> pointvector;
    //--Switch between keyboard and GPS navigation here---
    //private GPSReader gpsReader;
    private Keyboard gpsReader;
    //----------------------------------------------------
    private Socket sock;
    private PrintWriter out;
    private BufferedReader in;
    private Config conf;

    Protocol() {
        //--Switch between keyboard and GPS navigation here---
        //gpsReader = new GPSReader(this);
        gpsReader = new Keyboard(this);
        //----------------------------------------------------
        conf = new Config();
        gameOn = false;
        currentpos = new Point();
        deathpos = new Point();
        start();
        vec = new Vector<Player>();
        chatvec = new Vector<String>();
        pointvector = new Vector<Point>();
        hostid = -1;
        myid = -2;
    }

    //Communication with config object
    public void saveConf(String s) {
        conf.save(s);
    }

    public String getConf(String s) {
        return conf.getData(s);
    }

    //Ask the gpsReader for an updated position
    public void getNewPos() {
        currentpos.dx = gpsReader.longitude;
        currentpos.dy = gpsReader.latitude;
    }

    //The connect method that opens a new socket to address s at port i.
    //n is the name that should be sent to the server
    public void setSocket(String s, int i, String n, String c) {
        try {
            sock = new Socket(s, i);
            out = new PrintWriter(sock.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out.println("/NNM " + n + " " + c);
            connected = true;
            chatvec.add("Connected to server " + s + "\n");
        } catch (Exception e) {
            chatvec.add("Connect failed \n");
            disconnect();
        }
    }

    //Disconnect method
    public void disconnect() {
        try {
            out.println("/QUIT");
            in.close();
            out.close();
            sock.close();
            vec.clear();
            pointvector.clear();
            gameOn = false;
            isReady = false;
            connected = false;
        } catch (Exception e) {
        }
    }

    //Send a message to the server
    public void sendMsg(String s) {
        try {
            out.println(s);
        } catch (Exception e) {
        }
    }

    //The run method
    public void run() {
        while (true) {
            while (connected) {
                //Get message from server
                String st;
                try {
                    st = in.readLine();
                } catch (Exception e) {
                    disconnect();
                    break;
                }
                if (st == null) {
                    break;
                }

                //Handle received chat messages
                if (!st.startsWith("/")) {
                    chatvec.add(st + "\n");
                }

                //-----All commands below-----------
                //----------------------------------
                if (st.equals("/GST")) {
                    chatvec.add("Game at server already " +
                            "started. Disconnecting... \n");
                    disconnect();
                    break;
                }

                if (!gameOn) {
                    //Start the game!
                    if (st.startsWith("/START")) {
                        gameOn = true;
                        isAlive = true;
                        chatvec.add("GAME STARTED! \n");
                        for (Player p : vec) {
                            p.isAlive = true;
                        }
                    }
                    //Server reporting which player is the host
                    if (st.startsWith("/HST")) {
                        String[] spl = st.split(" ");
                        hostid = Integer.parseInt(spl[1]);
                    }
                    //Server reporting players own id
                    if (st.startsWith("/ID")) {
                        String[] spl = st.split(" ");
                        myid = Integer.parseInt(spl[1]);
                    }
                    //Add a player
                    if (st.startsWith("/NP")) {
                        String[] spl = st.split(" ");
                        int id = Integer.parseInt(spl[1]);
                        vec.add(new Player(id, spl[2], spl[3]));
                        chatvec.add(spl[2] + " connected! \n");
                    }
                    //Change name of player
                    if (st.startsWith("/BNM")) {
                        String[] spl = st.split(" ");
                        int id = Integer.parseInt(spl[1]);
                        for (Player p : vec) {
                            if (p.id == id) {
                                chatvec.add("Player " + p.name + " " +
                                        "changed name to " + spl[2] + "\n");
                                p.changeName(spl[2]);
                            }
                        }
                    }
                    //Remove a player
                    if (st.startsWith("/DP")) {
                        String[] spl = st.split(" ");
                        int id = Integer.parseInt(spl[1]);
                        int removeid = -1;
                        String rmname = "";
                        for (Player p : vec) {
                            if (p.id == id) {
                                removeid = vec.indexOf(p);
                                rmname = p.name;
                            }
                        }
                        try {
                            vec.remove(removeid);
                            chatvec.add("Player " + rmname +
                                    " disconnected \n");
                        } catch (Exception e) {
                        }
                    }
                    //Change position for other players
                    if (st.startsWith("/C")) {
                        String[] spl = st.split(" ");
                        int id = Integer.parseInt(spl[1]);
                        for (Player p : vec) {
                            if (p.id == id) {
                                double xc = Double.parseDouble(spl[2]);
                                double yc = Double.parseDouble(spl[3]);
                                p.currPos(xc, yc);
                            }
                        }
                    }
                    //Player is ready
                    if (st.startsWith("/OK")) {
                        String[] spl = st.split(" ");
                        int id = Integer.parseInt(spl[1]);
                        for (Player p : vec) {
                            if (p.id == id) {
                                p.ready = true;
                            }
                        }
                    }
                    //Player is not ready
                    if (st.startsWith("/NOK")) {
                        String[] spl = st.split(" ");
                        int id = Integer.parseInt(spl[1]);
                        for (Player p : vec) {
                            if (p.id == id) {
                                p.ready = false;
                            }
                        }
                    }
                    //Player changed color
                    if (st.startsWith("/OLO")) {
                        String[] spl = st.split(" ");
                        int id = Integer.parseInt(spl[1]);
                        for (Player p : vec) {
                            if (p.id == id) {
                                p.changeCol(spl[2]);
                            }
                        }
                    }
                }
                if (gameOn) {
                    //Send coordinates to player objects
                    if (st.startsWith("/C")) {
                        String[] spl = st.split(" ");
                        int id = Integer.parseInt(spl[1]);
                        double xc = Double.parseDouble(spl[2]);
                        double yc = Double.parseDouble(spl[3]);
                        for (Player p : vec) {
                            if (p.id == id) {
                                p.currPos(xc, yc);
                            }
                        }
                    }
                    //Player died
                    if (st.startsWith("/DD") & isAlive) {
                        isAlive = false;
                        String[] spl = st.split(" ");
                        deathpos.dx = Double.parseDouble(spl[1]);
                        deathpos.dy = Double.parseDouble(spl[2]);
                        chatvec.add("You died \n");
                    }
                    //Someone else died
                    if (st.startsWith("/DIE")) {
                        String[] spl = st.split(" ");
                        int id = Integer.parseInt(spl[1]);
                        for (Player p : vec) {
                            if (p.id == id) {
                                p.die(Double.parseDouble(spl[2]),
                                        Double.parseDouble(spl[3]));
                                chatvec.add("Player " + p.name + " died.  \n");
                            }
                        }
                    }
                    //Someone won
                    if (st.startsWith("/WIN")) {
                        String[] spl = st.split(" ");
                        int id = Integer.parseInt(spl[1]);
                        if (id == myid) {
                            chatvec.add("Congratulations! You won! \n");
                        }
                        for (Player p : vec) {
                            if (p.id == id) {
                                chatvec.add("The winner is " + p.name + "\n");
                            }
                        }
                        try {
                            Thread.currentThread().sleep(10000);
                        } catch (Exception e) {
                        }
                        gameOn = false;
                        isReady = false;
                        pointvector.clear();
                        for (Player p : vec) {
                            p.ready = false;
                            p.pointvector.clear();
                            if (p.disconnect) {
                                vec.remove(p);
                            }
                        }
                    }
                    //Delete player
                    if (st.startsWith("/DP")) {
                        String[] spl = st.split(" ");
                        int id = Integer.parseInt(spl[1]);
                        int removeid = -1;
                        String delname = "";
                        for (Player p : vec) {
                            if (p.id == id) {
                                delname = p.name;
                                p.disconnect = true;
                            }
                        }
                        chatvec.add("Player " + delname +
                                " disconnected and considered dead \n");
                    }
                }
            }
        }
    }
}
