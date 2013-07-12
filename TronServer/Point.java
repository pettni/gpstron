//A point specified either in int or double coordinates

class Point {

    public int ix;
    public int iy;
    public double x;
    public double y;
    public boolean exists;

    Point(int a, int b) {
        ix = a;
        iy = b;
    }

    Point(double a, double b) {
        x = a;
        y = b;
    }

    Point() {
    }
}
