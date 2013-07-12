//A point specified either in int or double coordinates

class Point {

    public int x;
    public int y;
    public double dx;
    public double dy;

    Point(int a, int b) {
        x = a;
        y = b;
    }

    Point(double a, double b) {
        dx = a;
        dy = b;
    }

    Point() {
    }
}
