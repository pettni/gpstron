//Class for information associated with a remote player in the game

import java.util.*;
import java.awt.*;

class Player {

    public String name;
    public int id;
    public Color col;
    public Vector<Point> pointvector;
    public Point currentpos;
    public Point deathpos;
    public boolean ready;
    public boolean isAlive;
    public boolean disconnect;

    Player(int i, String s, String c) {
        id = i;
        name = s;
        col = new Color(Integer.parseInt(c, 16));
        currentpos = new Point();
        deathpos = new Point();
        pointvector = new Vector<Point>();
        ready = false;
        disconnect = false;
    }

    public void changeName(String n) {
        name = n;
    }

    public void die(Double x, Double y) {
        isAlive = false;
        deathpos.dx = x;
        deathpos.dy = y;
    }

    public void changeCol(String c) {
        col = new Color(Integer.parseInt(c, 16));
    }

    public void currPos(double x, double y) {
        currentpos.dx = x;
        currentpos.dy = y;
    }
}
