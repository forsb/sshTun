package nu.forsby.filip.sshtun;

import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Connection {

    private JSch jsch;
    Host host;

    // Constructor
    public Connection(Host host) {
        this.jsch = new JSch();
        this.host = host;
    }

    // Execute remote command
    public void Execute(String command) {
        new RemoteExecuteTask().execute(command);
    }

    // Open port forward
    public void PortForward (int lport, String rhost, int rport) {
        LP lp = new LP(lport, rhost, rport);
        new PortForwardTask().execute(lp);
    }

    // onExecuteSuccess and onExecuteFail should be implemented by user
    public void onExecuteSuccess(Command command) { }

    public void onExecuteFail(Exception e) { }

    public void onPortForwardSuccess(int assinged_port) { }

    public void onPortForwardFail(Exception e) { }


    private Command executeCommand(String command) throws Exception {
        Command c = new Command(command);

        Session session = jsch.getSession(host.user, host.host, host.port);

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(host.passwd);

        session.connect();

        Channel channel = session.openChannel("exec");
        ((ChannelExec)channel).setCommand(c.command);
        channel.setInputStream(null);

        InputStream in = channel.getInputStream();

        c.timestamp = System.currentTimeMillis();

        channel.connect();

        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) {
                    break;
                }
                c.output.add(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                if(in.available() > 0) {
                    continue;
                }
                Log.i("JSCH", "exit-status: " + channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(1000);
            } catch(Exception e){ }
        }

        channel.disconnect();
        session.disconnect();

        return c;
    }

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
     * Async task to execute a command on the remote host
     */
    private class RemoteExecuteTask extends AsyncTask<String, Void, Command> {

        private Exception except = null;

        @Override
        protected Command doInBackground(String... commands) {
            try {
                return executeCommand(commands[0]);
            } catch (Exception e) {
                except = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Command result) {
            if (except == null) {
                onExecuteSuccess(result);
            } else {
                onExecuteFail(except);
            }
        }
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
