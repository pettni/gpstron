//Class for the objects in the Map matrix

import java.util.*;

class Spot {

    public int id;
    public Date timecreated;
    boolean passed;

    Spot() {
        passed = false;
    }

    void activate(int i) {
        id = i;
        timecreated = new Date();
        passed = true;
    }
}
