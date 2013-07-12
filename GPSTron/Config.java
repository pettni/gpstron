//This class controls the access to the config file saved in $HOME/.gpstron
import java.io.*;

class Config {

    private File f;
    private String name, north, east, south, west, mapfile, color;

    Config() {
        boolean dir = new File(System.getProperty("user.home") +
                "/.gpstron").mkdirs();
        f = new File(System.getProperty("user.home") + "/.gpstron/config.txt");
        boolean newconf = false;
        try {
            newconf = f.createNewFile();
        } catch (Exception e) {
        }

        //Standard values
        name = "NoName";
        north = "1";
        east = "1";
        south = "0";
        west = "0";
        mapfile = "";
        color = "0000FF";

        if (!newconf) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String st = br.readLine();
                while (st != null) {
                    saveData(st);
                    st = br.readLine();
                }
                br.close();
            } catch (Exception e) {
            }
        }
    }

    //Get information from the config file
    public String getData(String s) {
        if (s.equals("north")) {
            return north;
        } else if (s.equals("east")) {
            return east;
        } else if (s.equals("south")) {
            return south;
        } else if (s.equals("west")) {
            return west;
        } else if (s.equals("name")) {
            return name;
        } else if (s.equals("color")) {
            return color;
        } else if (s.equals("mapfile")) {
            return mapfile;
        } else {
            return "";
        }
    }

    //Help method that interprets a string of the form
    //"variable=value" and stores the value
    private void saveData(String s) {
        String[] spl = s.split("=");
        if (spl[0].equals("north")) {
            north = spl[1];
        }
        if (spl[0].equals("east")) {
            east = spl[1];
        }
        if (spl[0].equals("south")) {
            south = spl[1];
        }
        if (spl[0].equals("west")) {
            west = spl[1];
        }
        if (spl[0].equals("name")) {
            name = spl[1];
        }
        if (spl[0].equals("mapfile")) {
            mapfile = spl[1];
        }
        if (spl[0].equals("color")) {
            color = spl[1];
        }
    }

    //Save information to the config file
    public void save(String s) {
        saveData(s);
        f.delete();
        try {
            f.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write("north=" + north);
            bw.newLine();
            bw.write("east=" + east);
            bw.newLine();
            bw.write("south=" + south);
            bw.newLine();
            bw.write("west=" + west);
            bw.newLine();
            bw.write("name=" + name);
            bw.newLine();
            bw.write("mapfile=" + mapfile);
            bw.newLine();
            bw.write("color=" + color);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (Exception e) {
        }
    }
}
