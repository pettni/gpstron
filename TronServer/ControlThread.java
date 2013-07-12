//Class controlling the player objects
//Handles all outgoing messages

import java.util.*;

class ControlThread extends Thread {

    public boolean gameOn, gameEnd, bdset, sendErrmsg;
    public double north, east, south, west;
    public int hostid;
    public Map map;

    public Vector<PlayerThread> vec;
    private int nextid;
    private int deadPlayers, totalPlayers;   
    
    ControlThread() {
        gameOn = false;
        sendErrmsg = true;
        vec = new Vector<PlayerThread>();
        nextid = 0;
        start();
        map = new Map();
        bdset = false;
        deadPlayers = 0;
        totalPlayers = 0;
    }

    //New player
    public void newPlayer(PlayerThread p) {
        vec.add(p);
        hostid = vec.get(0).id;
        System.out.println("Number of players: " + vec.size());
    }

    public void saveBoundaries(double n, double e, double s, double w) {
        north = n;
        east = e;
        south = s;
        west = w;
        System.out.println("Boundaries saved");
        bdset = true;
        map.setEdges(n, e, s, w);
        sendToAll("Server: Received boundaries from host");
        sendToAll("Server: Play area: north=" + north + ", south=" + south + ", west=" + west + " and east=" + east);
    }

    //Remove player
    public void delPlayer(PlayerThread p) {
        int id = p.id;
        sendToOthers(p.id, "/DP " + p.id);
        System.out.println(p.name + " disconnected");
        vec.remove(p);
        System.out.println("Number of players: " + vec.size());
        if (id == hostid & vec.size() > 0) {
            hostid = vec.get(0).id;
            sendToAll("/HST " + hostid);
            sendToAll("Server: Player " + vec.get(0).name + " is now the host");
        }
    }

    //Return next available id
    public int getNumber() {
        int id = nextid;
        nextid++;
        return id;
    }

    //Send message s to all players but player with id n
    public void sendToOthers(int n, String s) {
        for (PlayerThread p : vec) {
            if (p.id != n) {
                p.sendMsg(s);
            }
        }
    }

    //Send message s to all players
    public void sendToAll(String s) {
        for (PlayerThread p : vec) {
            p.sendMsg(s);
        }
    }

    //Restart the server
    private void restart() {
        System.out.println("Server restarted, waiting for players");
        gameOn = false;
        nextid = 0;
        vec = new Vector<PlayerThread>();
        map.clear();
    }

    //Actions to perform when a player dies
    public void reportDeath(PlayerThread p) {
        deadPlayers++;
        p.isAlive = false;
        int winnerid = 0;
        if (deadPlayers == totalPlayers - 1) {

            for (PlayerThread pl : vec) {
                pl.isReady = false;
                pl.flush();
                if (pl.isAlive) {
                    winnerid = pl.id;
                }
            }
            sendToAll("/WIN " + winnerid);
            gameOn = false;
            System.out.println("Game ended, restarting..");
            map.clear();
        }
    }

    public void run() {
        while (true) {
            if (!gameOn) {
                //Check if all players are ready
                if (vec.size() > 0) {
                    int n = 0;
                    for (PlayerThread p : vec) {
                        if (p.isReady == true) {
                            n++;
                        }
                    }
                    if (n == vec.size()) {
                        if (bdset) {
                            gameOn = true;
                            deadPlayers = 0;
                            totalPlayers = vec.size();
                            System.out.println("Game starting..");
                            sendToAll("Server: The game will start in three seconds");
                            try {
                                Thread.currentThread().sleep(3000);
                            } catch (Exception e) {
                            }
                            sendToAll("/START");
                            //Flush all coordinates
                            for (PlayerThread p : vec) {
                                p.flush();
                                p.isAlive = true;
                            }
                        } else {
                            if (sendErrmsg) {
                                sendToAll("Server: The host player has to set play area boundaries. This is done automatically by setting the coordinates of the client. The game will start once the coordinates are set");
                                sendErrmsg = false;
                            }
                        }

                    }
                }

                //Distribute coordinates
                for (PlayerThread p : vec) {
                    try {
                        String xc = Double.toString(p.vec.lastElement().x);
                        String yc = Double.toString(p.vec.lastElement().y);
                        sendToOthers(p.id, "/C " + p.id + " " + xc + " " + yc);
                    } catch (Exception e) {
                    }
                }

                try {
                    Thread.currentThread().sleep(1000);
                } catch (Exception e) {
                }
            }

            if (gameOn) {
                //Distribute coordinates
                for (PlayerThread p : vec) {
                    try {
                        String xc = Double.toString(p.vec.lastElement().x);
                        String yc = Double.toString(p.vec.lastElement().y);
                        sendToOthers(p.id, "/C " + p.id + " " + xc + " " + yc);
                    } catch (Exception e) {
                    }
                }

                try {
                    Thread.currentThread().sleep(200);
                } catch (Exception e) {
                }

                if (vec.size() == 0) {
                    restart();
                }
            }
        }
    }
}
