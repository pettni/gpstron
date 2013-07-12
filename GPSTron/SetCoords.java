//Simple object that creates the window where new map coordinates 
//can be specified

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class SetCoords extends JFrame implements ActionListener {

    private JButton confirm, cancel;
    private View view;
    private Protocol prot;
    private JTextField north, east, south, west;

    SetCoords(View v, Protocol p) {
        view = v;
        prot = p;
        setTitle("Set coordinates");
        setSize(new Dimension(400, 300));
        setDefaultLookAndFeelDecorated(true);
        setLayout(new GridLayout(3, 1));

        JPanel numbers = new JPanel();
        numbers.setLayout(new GridLayout(4, 2));

        JLabel nl = new JLabel("North (long N): ");
        north = new JTextField(prot.getConf("north"));
        numbers.add(nl);
        numbers.add(north);

        JLabel sl = new JLabel("South: (long N)");
        south = new JTextField(prot.getConf("south"));
        numbers.add(sl);
        numbers.add(south);

        JLabel wl = new JLabel("West: (lat E)");
        west = new JTextField(prot.getConf("west"));
        numbers.add(wl);
        numbers.add(west);

        JLabel el = new JLabel("East: (lat E)");
        east = new JTextField(prot.getConf("east"));
        numbers.add(el);
        numbers.add(east);

        JPanel p5 = new JPanel();
        confirm = new JButton("Ok");
        confirm.addActionListener(this);

        cancel = new JButton("Cancel");
        cancel.addActionListener(this);

        p5.add(confirm);
        p5.add(cancel);

        JLabel help = new JLabel("Please specify " +
		"coordinates for the map borders.");
        add(help);

        add(numbers);
        add(p5);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirm) {
            double no = Double.parseDouble(north.getText());
            double ea = Double.parseDouble(east.getText());
            double so = Double.parseDouble(south.getText());
            double we = Double.parseDouble(west.getText());

            view.newCoords(no, ea, so, we);

            prot.saveConf("north=" + Double.toString(no));
            prot.saveConf("east=" + Double.toString(ea));
            prot.saveConf("south=" + Double.toString(so));
            prot.saveConf("west=" + Double.toString(we));

            if (prot.myid == prot.hostid) {
                prot.sendMsg("/BD " + Double.toString(no) + 
		" " + Double.toString(ea) + " " + 
		Double.toString(so) + " " + Double.toString(we));
            }

            dispose();
        }
        if (e.getSource() == cancel) {
            dispose();
        }

    }
}
