
import java.awt.*;

class DBCanvas extends Canvas {

    Image imbuff;
    Graphics gbuff;
    boolean initiated = false;

    DBCanvas() {
    }

    public void dbPaint(Graphics g) {
    }

    public void paint(Graphics g) {
        if (initiated) {
            dbPaint(gbuff);
            g.drawImage(imbuff, 0, 0, this);
        } else {
            dbPaint(g);
        }
    }

    public void update(Graphics g) {
        if (!initiated) {
            imbuff = createImage(getSize().width, getSize().height);
            gbuff = imbuff.getGraphics();
            initiated = true;
        }
        gbuff.setColor(getBackground());
        gbuff.fillRect(0, 0, getSize().width, getSize().height);
        gbuff.setColor(getForeground());
        paint(g);
    }
}
