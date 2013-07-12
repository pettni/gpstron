//Makes the game playable with the keyboard for testing purposes
//Interface is equal to the GPSReader class
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Keyboard extends JFrame implements ActionListener {

    public double latitude;
    public double longitude;

    private JButton north, east, south, west;

    Keyboard(Protocol p) {
        latitude = 59.34933;
        longitude = 18.07276;

        Action up = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                latitude += 0.0001;
            }
        };
        Action down = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                latitude -= 0.0001;
            }
        };
        Action left = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                longitude -= 0.0001;
            }
        };
        Action right = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                longitude += 0.0001;
            }
        };

        setTitle("Navigation window");
        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(200, 200));

        JPanel panel = new JPanel();
        add(panel);

        north = new JButton("North");
        south = new JButton("South");
        east = new JButton("East");
        west = new JButton("West");
        north.addActionListener(this);
        south.addActionListener(this);
        east.addActionListener(this);
        west.addActionListener(this);

        panel.add(north);
        panel.add(south);
        panel.add(west);
        panel.add(east);

        setVisible(true);

        panel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,
                0), "up");
        panel.getActionMap().put("up", up);

        panel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
                0), "down");
        panel.getActionMap().put("down", down);

        panel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
                0), "left");
        panel.getActionMap().put("left", left);

        panel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
                0), "right");
        panel.getActionMap().put("right", right);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == north) {
            latitude += 0.0001;
        }
        if (e.getSource() == south) {
            latitude -= 0.0001;
        }
        if (e.getSource() == west) {
            longitude -= 0.0001;
        }
        if (e.getSource() == east) {
            longitude += 0.0001;
        }
    }
}
