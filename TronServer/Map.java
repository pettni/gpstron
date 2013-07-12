
import java.util.*;
import java.lang.Math.*;

class Map {

    private Spot[][] matrix;
    private double north, east, south, west;

    Map() {
        matrix = new Spot[2000][2000];
        clear();
    }

//Set the boundaries for the play area
    void setEdges(double n, double e, double s, double w) {
        north = n;
        east = e;
        south = s;
        west = w;
    }

//Converts GPS coordinates to a matrix element
    private Point convert(Point p) {
        Point ret = new Point();
        double ix = 2000 * (p.x - west) / (east - west);
        double iy = 2000 * (p.y - south) / (north - south);
        ret.x = ix;
        ret.y = iy;
        ret.ix = (int) ix;
        ret.iy = (int) iy;
        return ret;
    }

//Converts a matrix element to GPS Coordinates
    private Point invconvert(Point p) {
        Point ret = new Point();
        ret.x = west + p.ix * (east - west) / 2000;
        ret.y = south + p.iy * (north - south) / 2000;
        return ret;
    }

//Erases all traces in the matrix
    void clear() {
        for (int i = 0; i < 2000; i++) {
            for (int j = 0; j < 2000; j++) {
                matrix[i][j] = new Spot();
            }
        }
    }

//Checks if the line between p1 and p2 contains a collision. If so, the
//point of intersection is returned
    Point check(Point p1, Point p2, int id) {
        Point ret = new Point();
        ret.exists = false;

        Point ip1 = convert(p1);
        Point ip2 = convert(p2);

        Point vector = new Point((ip2.x - ip1.x), (ip2.y - ip1.y));

        double diffx = ip2.x - ip1.x;
        double diffy = ip2.y - ip1.y;

        double length = Math.sqrt(diffx * diffx + diffy * diffy);

        if (length > 0) {
            vector.x = vector.x / length;
            vector.y = vector.y / length;
            //Length of direction vector is now 1
        }

        Date date = new Date();

        for (int i = 0; i <= 2 * length; i++) {

            double xp = ip1.x + i * vector.x / 2;
            double yp = ip1.y + i * vector.y / 2;

            int x = (int) xp;
            int y = (int) yp;

            if (matrix[x][y].passed) {
                //You can not commit suicide at points passed
                //later than up to 10 seconds ago,
                //to compensate for the GPS margin of error
                if (date.getTime() - matrix[x][y].timecreated.getTime() > 10000 |
			matrix[x][y].id != id) {
                    ret = invconvert(new Point(x, y));
                    ret.exists = true;
                }
            } else {
                matrix[x][y].activate(id);
            }
        }
        return ret;
    }
}


