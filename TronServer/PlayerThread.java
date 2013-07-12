//The servers representation of a player, every connected 
//player is an object of this class
//Handles all incoming messages from the players

import java.net.*;
import java.io.*;
import java.util.*;

class PlayerThread extends Thread {

    public boolean isReady;
    public boolean isAlive;
    public Vector<Point> vec;
    public String name;
    public String color;
    public int id;
    private ControlThread ct;
    private Socket sock;
    private BufferedReader in;
    private PrintWriter out;
    private Map map;

    PlayerThread(Socket s, ControlThread c) {
        isReady = false;
        sock = s;
        ct = c;
        map = ct.map;
        id = ct.getNumber();

        try {
            in = new BufferedReader(new 
	    	InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);
        } catch (Exception ie) {
        }

        ct.newPlayer(this);

        out.println("/ID " + id);
        out.println("/HST " + ct.hostid);

        vec = new Vector<Point>();

        start();

        //Send info to the new player about existing players
        for (PlayerThread p : ct.vec) {
            if (p != this) {
                out.println("/NP " + p.id + " " + p.name + " " + p.color);
            }
        }
        try {
            Thread.currentThread().sleep(1000);
        } catch (Exception e) {
        }

        //Send info to other players about the existing player
        ct.sendToOthers(id, "/NP " + id + " " + name + " " + color);
    }

    public void sendMsg(String s) {
        out.println(s);
    }

    public void flush() {
        vec.clear();
    }

    private void remove() {
        if (ct.gameOn) {
            ct.reportDeath(this);
        }
        ct.delPlayer(this);
        try {
            in.close();
            out.close();
            sock.close();
        } catch (Exception e) {
        }
    }

    public void run() {
        while (true) {
            String st;
            //Wait for socket message
            try {
                st = in.readLine();
            } catch (Exception e) {
                remove();
                break;
            }
            if (st == null) {
                remove();
                break;
            }

            //Send chat messages
            if (!st.startsWith("/")) {
                ct.sendToOthers(id, name + ": " + st);
            }

            // Close nicely when QUIT
            if (st.equals("/QUIT")) {
                remove();
                break;
            }
            if (!ct.gameOn) {
                //Manage ready and name change
                //
                //Ready command
                if (st.equals("/OK")) {
                    isReady = true;
                    System.out.println(name + " ready");
                    ct.sendToAll("/OK " + id);
                }
                //Not ready command
                if (st.equals("/NOK")) {
                    isReady = false;
                    System.out.println(name + " not ready");
                    ct.sendToAll("/NOK " + id);
                }
                //Change name command
                if (st.startsWith("/NM")) {
                    String[] spl = st.split(" ");
                    System.out.println(name + " changed name to " + spl[1]);
                    name = spl[1];
                    ct.sendToOthers(id, "/BNM " + id + " " + name);
                }
                //Name and color of new player
                if (st.startsWith("/NNM")) {
                    String[] spl = st.split(" ");
                    name = spl[1];
                    color = spl[2];
                }
                //Play boundaries from host player
                if (st.startsWith("/BD")) {
                    if (id == ct.hostid) {
                        String[] spl = st.split(" ");
                        double no = Double.parseDouble(spl[1]);
                        double ea = Double.parseDouble(spl[2]);
                        double so = Double.parseDouble(spl[3]);
                        double we = Double.parseDouble(spl[4]);
                        ct.saveBoundaries(no, ea, so, we);
                    }
                }
                //Player color
                if (st.startsWith("/OLO")) {
                    String[] spl = st.split(" ");
                    color = spl[1];
                    ct.sendToOthers(id, "/OLO " + id + " " + color);
                }
                //Coordinates
                if (st.startsWith("/C")) {
                    String[] spl = st.split(" ");
                    Point d = new Point();
                    d.x = Double.parseDouble(spl[1]);
                    d.y = Double.parseDouble(spl[2]);
                    vec.add(d);
                }

            }

            if (ct.gameOn & isAlive) {
                //Coordinates
                if (st.startsWith("/C")) {
                    String[] spl = st.split(" ");
                    Point d = new Point();
                    d.x = Double.parseDouble(spl[1]);
                    d.y = Double.parseDouble(spl[2]);
                    vec.add(d);

                    int index = vec.size();
                    if (index > 1) {
                        Point p1 = vec.get(index - 2);
                        Point p2 = vec.get(index - 1);

                        //Check if outside play area
                        if (p2.x < ct.west | p2.x > ct.east | 
				p2.y > ct.north | p2.y < ct.south) {
                            out.println("/DD " + p2.x + " " + p2.y);
                            ct.sendToOthers(id, "/DIE " + id + " " +
			    	p2.x + " " + p2.y);
                            ct.reportDeath(this);
                        } else {
                            //Check for intersection
                            Point isect = map.check(p1, p2, id);
                            if (isect.exists) {
                                out.println("/DD " + isect.x + " " + 
					isect.y);
                                ct.sendToOthers(id, "/DIE " + id + " " + 
					isect.x + " " + isect.y);
                                ct.reportDeath(this);
                            }
                        }
                    }
                }
            }
        }
    }
}
