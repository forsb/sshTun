package nu.forsby.filip.sshtun;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Host {

    String host;
    String user;
    int port;
    String passwd;

    public Host(String host, String user, String passwd, int port) {
        this.host = host;
        this.user = user;
        this.passwd = passwd;
        this.port = port;
    }

    public String toString() {
        return this.user + "@" + this.host + ":" + this.port;
    }

}
