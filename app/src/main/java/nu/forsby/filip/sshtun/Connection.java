package nu.forsby.filip.sshtun;

import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.*;

public class Connection {

    private JSch jsch;

    String host;
    String user;
    int port;

    String password;
    byte[] privateKey;
    String keyFileName;

    int lport;
    String rhost;
    int rport;


    // Constructor
    public Connection() {
        this.jsch = new JSch();
    }

    public Connection(String host,
                      String user,
                      int port,
                      int lport,
                      String rhost,
                      int rport) {
        this.host = host;
        this.user = user;
        this.port = port;

        this.lport = lport;
        this.rhost = rhost;
        this.rport = rport;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Open port forward
    public void PortForward () {
        new PortForwardTask().execute(0);
    }

    public void onPortForwardSuccess(int assinged_port) { }

    public void onPortForwardFail(Exception e) { }

    private int portForward() throws Exception {
        Session session = jsch.getSession(user, host, port);

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        if (password != null) {
            session.setPassword(password);
        }

        session.connect();
        return session.setPortForwardingL(lport, rhost, rport);
    }

    /*
     * Async task to open port
     */
    private class PortForwardTask extends AsyncTask<Integer, Void, Integer> {

        private Exception except = null;

        @Override
        protected Integer doInBackground(Integer... a) {
            try {
                return portForward();
            } catch (Exception e) {
                except = e;
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer assinged_port) {
            if (except == null) {
                Log.i("SSHTUN", "Local forward localhost:" + assinged_port);
                onPortForwardSuccess(assinged_port);
            } else {
                Log.i("SSHTUN", "Local forward failed.");
                onPortForwardFail(except);
            }
        }
    }
}
