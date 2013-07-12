//Object responsible for repainting the View object and also to tell the 
//Protocol object to check for current position and send it to the server.

class Updater extends Thread {

    private Protocol prot;
    private View view;
    private Manipulation man;
    private int n;

    Updater(Protocol p, View v, Manipulation mp) {
        prot = p;
        view = v;
        man = mp;
        n = 0;
        start();
    }

    public void run() {
        while (true) {
            prot.getNewPos();
            view.repaint();

            //Send own coordinates
            if (n > 0) {
                try {
                    prot.sendMsg("/C " + prot.currentpos.dx + " " + prot.currentpos.dy);
                    n = 0;
                } catch (Exception e) {
                }
            }

            //Update chat field
            for (int i = 0; i < prot.chatvec.size(); i++) {
                man.textarea.append(prot.chatvec.get(i));
            }
            man.textarea.setCaretPosition(man.textarea.getText().length());
            prot.chatvec.clear();

            //Player list with ready status
            String players = "";
            String ready = "";
            int id = prot.myid;

            if (prot.connected) {
                for (int i = 0; i < prot.vec.size(); i++) {

                    if (id < prot.vec.get(i).id) {
                        players += man.name + "\n";
                        ready += (prot.isReady) ? "Ready \n" : "Not ready \n";
                        id = 2147483647;
                    }

                    players += prot.vec.get(i).name + "\n";

                    if (prot.vec.get(i).ready) {
                        ready += "Ready \n";
                    } else {
                        ready += "Not ready \n";
                    }
                }

                if (prot.vec.size() > 0) {
                    if (id > prot.vec.lastElement().id & id != 2147483647) {
                        players += man.name + "\n";
                        ready += (prot.isReady) ? "Ready \n" : "Not ready \n";
                        id = 2147483647;
                    }
                }

                if (prot.vec.size() < 1) {
                    players = man.name + "\n";
                    ready = (prot.isReady) ? "Ready \n" : "Not ready \n";
                }
            } else {
                players = "Not connected to server";
                ready = "";
            }

            man.playerfield.setText(players);
            man.readyfield.setText(ready);

            man.updatebuttons();
            n++;
            try {
                Thread.currentThread().sleep(200);
            } catch (InterruptedException e) {
            }
        }
    }
}
