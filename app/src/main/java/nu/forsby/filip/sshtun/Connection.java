package nu.forsby.filip.sshtun;

import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.*;

public class Connection {

    private JSch jsch;
    Host host;

    // Constructor
    public Connection(Host host) {
        this.jsch = new JSch();
        this.host = host;
    }

    // Open port forward
    public void PortForward (int lport, String rhost, int rport) {
        LP lp = new LP(lport, rhost, rport);
        new PortForwardTask().execute(lp);
    }

    public void onPortForwardSuccess(int assinged_port) { }

    public void onPortForwardFail(Exception e) { }

    private int portForward(int lport, String rhost, int rport) throws Exception {
        Session session = jsch.getSession(host.user, host.host, host.port);

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(host.passwd);

        session.connect();
        return session.setPortForwardingL(lport, rhost, rport);
    }

    /*
     * Async task to open port
     */
    private class PortForwardTask extends AsyncTask<LP, Void, Integer> {

        private Exception except = null;
        private LP lp;

        @Override
        protected Integer doInBackground(LP... lps) {
            lp = lps[0];
            try {
                return portForward(lp.lport, lp.rhost, lp.rport);
            } catch (Exception e) {
                except = e;
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer assinged_port) {
            if (except == null) {
                Log.i("SSHTUN", "Local forward localhost:" + assinged_port + " -> " + lp.rhost + ":" + lp.rport);
                onPortForwardSuccess(assinged_port);
            } else {
                Log.i("SSHTUN", "Local forward failed.");
                onPortForwardFail(except);
            }
        }
    }

    private class LP {
        int lport;
        String rhost;
        int rport;

        public LP (int lport, String rhost, int rport) {
            this.lport = lport;
            this.rhost = rhost;
            this.rport = rport;
        }
    }
}
