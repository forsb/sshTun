package nu.forsby.filip.sshtun;

import android.content.SharedPreferences;

import java.util.Base64;

class PreferenceWrapper {

    private static final String PRIVATE_KEY_STORAGE_KEY = "private_key_bytes";
    private static final String PUBLIC_KEY_STORAGE_KEY = "public_key_bytes";

    private SharedPreferences fSharedPref;
    private byte[] fPrivateKey = null;
    private byte[] fPublicKey = null;

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
        return fSharedPref.getString(key, "");
    }

    public int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    public byte[] getPrivateKey() {
        if (fPrivateKey == null) {
            fPrivateKey = Base64.getDecoder().decode(getString(PRIVATE_KEY_STORAGE_KEY));
        }
        return fPrivateKey;
    }

    public byte[] getPublicKey() {
        if (fPublicKey == null) {
            fPublicKey = Base64.getDecoder().decode(getString(PUBLIC_KEY_STORAGE_KEY));
        }
        return fPublicKey;
    }

    public void set(String key, String value) {
        fSharedPref.edit().putString(key, value).apply();
    }

    public void setPrivateKey(byte[] privateKey) {
        fPrivateKey = privateKey;
        set(PRIVATE_KEY_STORAGE_KEY, Base64.getEncoder().encodeToString(privateKey));
    }

    public void setPublicKey(byte[] publicKey) {
        fPublicKey = publicKey;
        set(PUBLIC_KEY_STORAGE_KEY, Base64.getEncoder().encodeToString(publicKey));
    }
}
