//Visible object in main window handling all user input
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

class Manipulation extends JPanel implements ActionListener {

    public String name;
    public JTextArea textarea, playerfield, readyfield;
    private JButton ready, color;
    private View view;
    private Protocol prot;
    private JButton choosefile, setcoords, connect, chname;
    private JFileChooser fc;
    private JTextField serverfield, portfield, chatfield;

    Manipulation(Protocol p, View v) {
        view = v;
        prot = p;
        name = prot.getConf("name");

        choosefile = new JButton("Load new map");
        choosefile.setActionCommand("enable");
        choosefile.addActionListener(this);
        fc = new JFileChooser();

        setcoords = new JButton("Set coordinates");
        setcoords.addActionListener(this);

        JLabel serverlabel = new JLabel("Server:");
        JLabel portlabel = new JLabel("Port:");

        serverfield = new JTextField("localhost", 10);

        portfield = new JTextField("4554", 6);

        connect = new JButton("Connect");
        connect.addActionListener(this);

        chname = new JButton("Change name");
        chname.addActionListener(this);

        chatfield = new JTextField(30);
        chatfield.addActionListener(this);

        textarea = new JTextArea(15, 30);
        textarea.setEditable(false);
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textarea);
        scrollPane.getViewport().add(textarea);

        JLabel player = new JLabel("Player list");

        playerfield = new JTextArea();
        playerfield.setEditable(false);

        readyfield = new JTextArea();
        readyfield.setEditable(false);

        ready = new JButton("I'm ready");
        ready.addActionListener(this);

        color = new JButton("Change color");
        color.addActionListener(this);

        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.ipadx = 2;
        c.ipady = 5;
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        add(serverlabel, c);

        c.gridx = 3;
        add(serverfield, c);

        c.gridx = 6;
        add(portlabel, c);

        c.gridx = 9;
        add(portfield, c);

        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 6;
        add(connect, c);

        c.gridx = 6;
        add(chname, c);

        c.gridy = 2;
        c.gridx = 0;
        add(choosefile, c);

        c.gridx = 6;
        add(setcoords, c);

        c.anchor = GridBagConstraints.LINE_START;
        c.gridy = 3;
        c.gridx = 0;
        c.gridwidth = 12;
        c.gridheight = 10;
        add(scrollPane, c);

        c.gridy = 14;
        c.gridheight = 1;
        add(chatfield, c);

        c.gridy = 15;
        add(player, c);

        c.gridy = 16;
        c.gridwidth = 8;
        c.gridheight = 6;
        add(playerfield, c);

        c.gridx = 8;
        c.gridwidth = 4;
        add(readyfield, c);

        c.gridx = 0;
        c.gridy = 23;
        c.gridheight = 1;
        c.gridwidth = 6;
        c.anchor = GridBagConstraints.CENTER;
        add(ready, c);

        c.gridx = 6;
        add(color, c);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == choosefile) {
            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                prot.saveConf("mapfile=" + file);
                view.newFile(file);
            }
        }
        if (e.getSource() == chname) {
            if (!prot.gameOn) {
                String nm = JOptionPane.showInputDialog("Enter name", name);
                if (nm != null) {
                    name = nm;
                    prot.sendMsg("/NM " + name);
                    prot.saveConf("name=" + name);
                }
            } else {
                textarea.append("Name change not allowed during play \n");
            }
        }
        if (e.getSource() == setcoords) {
            new SetCoords(view, prot);
        }
        if (e.getSource() == connect) {
            if (!prot.connected) {
                String serv = serverfield.getText();
                int port = Integer.parseInt(portfield.getText());
                prot.setSocket(serv, port, name, prot.getConf("color"));
            } else {
                prot.disconnect();
                prot.chatvec.add("Disconnected. \n");
            }
        }
        if (e.getSource() == ready) {
            if (prot.isReady) {
                prot.isReady = false;
                prot.sendMsg("/NOK");
            } else {
                prot.isReady = true;
                prot.sendMsg("/OK");
            }
        }
        if (e.getSource() == color) {
            String[] choices = {"Blue", "Green", "Red", "Yellow", "Black",
                "Pink", "Orange", "White"};
            int cc = JOptionPane.showOptionDialog(null, "Choose your color",
                    "Color", JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, choices, null);
            String col = "";
            switch (cc) {
                case 0:
                    col = "0000FF";
                    break;
                case 1:
                    col = "00FF00";
                    break;
                case 2:
                    col = "FF0000";
                    break;
                case 3:
                    col = "FFF000";
                    break;
                case 4:
                    col = "000000";
                    break;
                case 5:
                    col = "FF00FF";
                    break;
                case 6:
                    col = "FF9C00";
                    break;
                case 7:
                    col = "FFFFFF";
                    break;
                default:
            }
            try {
                Color c = new Color(Integer.parseInt(col, 16));
                view.color=c;
                prot.saveConf("color=" + col);
                prot.sendMsg("/OLO " + col);
            } catch (Exception e2) {
            }
        }
        if (e.getSource() == chatfield) {
            String text = chatfield.getText();
            prot.sendMsg(text);
            textarea.append(text + "\n");
            chatfield.setText("");
        }
    }

    //Updates text of Connect and ready buttons
    public void updatebuttons() {
        if (prot.connected) {
            connect.setText("Disconnect");
        } else {
            connect.setText("Connect");
        }
        if (prot.isReady) {
            ready.setText("I'm not ready");
        } else {
            ready.setText("I'm ready!");
        }
    }
}
