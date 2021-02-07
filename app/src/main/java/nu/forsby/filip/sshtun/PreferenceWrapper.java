package nu.forsby.filip.sshtun;

import android.content.SharedPreferences;

class PreferenceWrapper {

    private SharedPreferences fSharedPref;
    private byte[] fByteArray;

    private static PreferenceWrapper fInstance;

    public PreferenceWrapper() {
        // Empty constructor
    }

    public static synchronized PreferenceWrapper getInstance() {
        if (fInstance == null) {
            fInstance = new PreferenceWrapper();
        }
        return fInstance;
    }

    public void init(SharedPreferences prefs) {
        fSharedPref = prefs;
    }

    public String getString(String key) {
        return fSharedPref.getString(key, null);
    }

    public int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    public byte[] getPrivateKeyBytes() {
        return fByteArray;
    }

    public void set(String key, String value) {
        fSharedPref.edit().putString(key, value).apply();
    }

    public void set(String key, int value) {
        fSharedPref.edit().putInt(key, value).apply();
    }

    public void setPrivateKeyBytes(byte[] privateKeyBytes) {
        fByteArray = privateKeyBytes;
    }

}
