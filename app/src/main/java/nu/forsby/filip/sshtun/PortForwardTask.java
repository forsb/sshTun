package nu.forsby.filip.sshtun;

import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.*;

/**
 * Async task to open port
 */
public class PortForwardTask extends AsyncTask<Void, Void, Integer> {

    public interface PortForwardResultListener {
        void onPortForwardSuccess(int assignedPort);
        void onPortForwardFail(Exception e);
    }

    private PreferenceWrapper fPrefs;
    private Exception fException;
    private PortForwardResultListener fListener;

    public PortForwardTask(PortForwardResultListener listener) {
        super();
        fListener = listener;
        fPrefs = PreferenceWrapper.getInstance();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            return portForward();
        } catch (Exception e) {
            fException = e;
            return -1;
        }
    }

    @Override
    protected void onPostExecute(Integer assignedPort) {
        if (fException == null) {
            Log.i("SSHTUN", "Local forward localhost:" + assignedPort);
            fListener.onPortForwardSuccess(assignedPort);
        } else {
            Log.i("SSHTUN", "Local forward failed.");
            fListener.onPortForwardFail(fException);
        }
    }

    private int portForward() throws JSchException {
        Session session =  new JSch().getSession(
                fPrefs.getString("User"),
                fPrefs.getString("Host"),
                fPrefs.getInt("Port"));

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        String password = fPrefs.getString("Password");
        if (!password.isEmpty()) {
            session.setPassword(password);
        }

        session.connect();
        return session.setPortForwardingL(
                fPrefs.getInt("Local Port"),
                fPrefs.getString("Remote Host"),
                fPrefs.getInt("Remote Port"));
    }

}
