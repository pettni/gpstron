//Main object that sets up the window
import java.awt.*;
import javax.swing.*;

class GPSTron extends JFrame {

    GPSTron() {
        Protocol prot = new Protocol();

        View view = new View(prot);
        view.setSize(new Dimension(570, 570));

        Manipulation man = new Manipulation(prot, view);

        new Updater(prot, view, man);

        setTitle("GPS Tron");
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.PAGE_START;

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 62;
        add(view, c);
        c.gridx = 62;
        c.gridwidth = 38;
        add(man, c);

        setSize(new Dimension(950, 620));
        setVisible(true);
    }

    public static void main(String[] x) {
        new GPSTron();
    }
}
