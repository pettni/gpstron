//Object responsible for painting the map, and the players positions and traces
//onto it.

import java.awt.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;

class View extends DBCanvas {

    private Protocol prot;
    private Image imscaled;
    private double north, east, south, west;
    private int xsize, ysize;
    public Color color;

    View(Protocol p) {
        prot = p;
        xsize = 570;
        ysize = 570;

        north = Double.parseDouble(prot.getConf("north"));
        east = Double.parseDouble(prot.getConf("east"));
        south = Double.parseDouble(prot.getConf("south"));
        west = Double.parseDouble(prot.getConf("west"));
        File filen = new File(prot.getConf("mapfile"));
        color = new Color(Integer.parseInt(prot.getConf("color"), 16));
        newFile(filen);
    }

    //Method to specify new map image
    public void newFile(File infile) {
        try {
            BufferedImage im = ImageIO.read(infile);
            imscaled = im.getScaledInstance(xsize, ysize, Image.SCALE_AREA_AVERAGING);
            repaint();
        } catch (IOException ie) {
        }
    }

    //Method to change map coordinates
    public void newCoords(double n, double e, double s, double w) {
        north = n;
        east = e;
        south = s;
        west = w;
    }

    //Method to convert coordinates to pixel integers
    private Point coordToInt(Point p) {
        Point crd = new Point();

        double xstep = xsize / (east - west);
        double ystep = ysize / (north - south);

        crd.x = (int) ((p.dx - west) * xstep);
        crd.y = (int) (ysize - (p.dy - south) * ystep);

        return crd;
    }

    //Method to draw a line with thickness t from p1 to p2
    private void thickLine(Point p1, Point p2, int t, Graphics g) {
        int dX = p1.x - p2.x;
        int dY = p1.y - p2.y;

        double lineLength = Math.sqrt(dX * dX + dY * dY);

        double scale = (double) (t) / (2 * lineLength);

        double ddx = -scale * (double) dY;
        double ddy = scale * (double) dX;
        ddx += (ddx > 0) ? 0.5 : -0.5;
        ddy += (ddy > 0) ? 0.5 : -0.5;
        int dx = (int) ddx;
        int dy = (int) ddy;

        int xx[] = new int[4];
        int yy[] = new int[4];

        xx[0] = p1.x + dx;
        yy[0] = p1.y + dy;
        xx[1] = p1.x - dx;
        yy[1] = p1.y - dy;
        xx[2] = p2.x - dx;
        yy[2] = p2.y - dy;
        xx[3] = p2.x + dx;
        yy[3] = p2.y + dy;

        g.fillPolygon(xx, yy, 4);
    }

    //Draw a cross at point p
    private void drawCross(Point p, Graphics g) {
        Point p1 = new Point(p.x - 10, p.y - 10);
        Point p2 = new Point(p.x + 10, p.y + 10);
        Point p3 = new Point(p.x - 10, p.y + 10);
        Point p4 = new Point(p.x + 10, p.y - 10);
        thickLine(p1, p2, 4, g);
        thickLine(p3, p4, 4, g);
    }

    //Paint method
    public void dbPaint(Graphics g) {
        //Paint the map first
        if (imscaled != null) {
            g.drawImage(imscaled, 0, 0, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, xsize, ysize);
        }

        Point coord = new Point();
        //Then the players
        if (!prot.gameOn) {
            g.setColor(color);
            coord = coordToInt(prot.currentpos);
            g.fillOval(coord.x - 7, coord.y - 7, 14, 14);
            for (Player p : prot.vec) {
                g.setColor(p.col);
                coord = coordToInt(p.currentpos);
                g.fillOval(coord.x - 7, coord.y - 7, 14, 14);
            }
        } else {
            //Add current coordinates if still alive
            if (prot.isAlive) {
                coord = coordToInt(prot.currentpos);
                prot.pointvector.add(coord);
            } else {
                g.setColor(color);
                Point death = coordToInt(prot.deathpos);
                thickLine(prot.pointvector.lastElement(), death, 4, g);
                drawCross(death, g);
            }

            //Paint trace
            g.setColor(color);
            for (int i = 0; i < prot.pointvector.size() - 1; i++) {
                thickLine(prot.pointvector.get(i + 1), prot.pointvector.get(i), 4, g);
            }

            //Paint own position
            coord = coordToInt(prot.currentpos);
            g.fillOval(coord.x - 7, coord.y - 7, 14, 14);

            //Paint other players traces and position
            for (Player p : prot.vec) {
                g.setColor(p.col);
                if (p.isAlive) {
                    p.pointvector.add(coordToInt(p.currentpos));
                    coord = coordToInt(p.currentpos);
                    g.fillOval(coord.x - 7, coord.y - 7, 14, 14);
                }
                for (int i = 0; i < p.pointvector.size() - 1; i++) {
                    thickLine(p.pointvector.get(i + 1), p.pointvector.get(i), 4, g);
                }
                if (!p.isAlive) {
                    Point death = coordToInt(p.deathpos);
                    thickLine(p.pointvector.lastElement(), death, 4, g);
                    drawCross(death, g);
                }
            }
        }
    }
}
