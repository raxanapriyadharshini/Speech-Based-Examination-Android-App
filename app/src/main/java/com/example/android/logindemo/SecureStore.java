package com.example.android.logindemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Base64;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Locale;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Fully local, encrypted user store — replaces Firebase Auth / Database / Storage.
 *
 * Security design:
 *  - All values are persisted in EncryptedSharedPreferences (AES-256-GCM),
 *    with the master key held in the Android Keystore (hardware-backed where available).
 *  - Passwords are never stored. Only a PBKDF2WithHmacSHA256 hash (120k iterations)
 *    over a per-user 16-byte random salt is kept, and it is verified in constant time.
 *  - Profile images are copied into app-private internal storage; nothing leaves the device.
 */
public final class SecureStore {

    private static final String PREFS_FILE = "secure_user_store";
    private static final String KEY_CURRENT_USER = "current_user_email";
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int SALT_BYTES = 16;

    private final SharedPreferences prefs;
    private final Context appContext;

    private SecureStore(Context context) {
        this.appContext = context.getApplicationContext();
        this.prefs = createEncryptedPrefs(this.appContext);
    }

    public static SecureStore get(Context context) {
        return new SecureStore(context);
    }

    private static SharedPreferences createEncryptedPrefs(Context ctx) {
        try {
            MasterKey masterKey = new MasterKey.Builder(ctx)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    ctx,
                    PREFS_FILE,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to open secure storage", e);
        }
    }

    // ---- Account lifecycle -------------------------------------------------

    public boolean userExists(String email) {
        return prefs.contains(pw(normalize(email)));
    }

    /** Registers a new user. Returns false if the email is already taken. */
    public boolean register(String email, String password, String name, String age, Uri imageUri) {
        String key = normalize(email);
        if (userExists(key)) {
            return false;
        }
        byte[] salt = randomSalt();
        byte[] hash = pbkdf2(password.toCharArray(), salt);

        SharedPreferences.Editor e = prefs.edit();
        e.putString(pw(key), encode(hash));
        e.putString(salt(key), encode(salt));
        e.putString(name(key), name);
        e.putString(age(key), age);
        e.putString(email(key), email);
        String imgPath = (imageUri != null) ? copyImageToInternal(key, imageUri) : null;
        if (imgPath != null) {
            e.putString(img(key), imgPath);
        }
        e.commit();
        return true;
    }

    /** Verifies credentials in constant time. */
    public boolean authenticate(String email, String password) {
        String key = normalize(email);
        String storedHash = prefs.getString(pw(key), null);
        String storedSalt = prefs.getString(salt(key), null);
        if (storedHash == null || storedSalt == null) {
            return false;
        }
        byte[] candidate = pbkdf2(password.toCharArray(), decode(storedSalt));
        return MessageDigest.isEqual(candidate, decode(storedHash));
    }

    /** Sets a new password for an existing user (re-salts). */
    public boolean updatePassword(String email, String newPassword) {
        String key = normalize(email);
        if (!userExists(key)) {
            return false;
        }
        byte[] salt = randomSalt();
        byte[] hash = pbkdf2(newPassword.toCharArray(), salt);
        prefs.edit()
                .putString(pw(key), encode(hash))
                .putString(salt(key), encode(salt))
                .commit();
        return true;
    }

    // ---- Session -----------------------------------------------------------

    public void setCurrentUser(String email) {
        prefs.edit().putString(KEY_CURRENT_USER, normalize(email)).commit();
    }

    public String getCurrentUser() {
        return prefs.getString(KEY_CURRENT_USER, null);
    }

    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    public void logout() {
        prefs.edit().remove(KEY_CURRENT_USER).commit();
    }

    /** Removes all stored users/session. Primarily for tests and factory-reset. */
    public void clearAll() {
        prefs.edit().clear().commit();
    }

    // ---- Profile -----------------------------------------------------------

    public UserProfile getProfile(String email) {
        String key = normalize(email);
        return new UserProfile(
                prefs.getString(age(key), ""),
                prefs.getString(email(key), email),
                prefs.getString(name(key), ""));
    }

    public String getImagePath(String email) {
        return prefs.getString(img(normalize(email)), null);
    }

    /** Updates profile fields (and image if provided). Handles email change by re-keying. */
    public void updateProfile(String currentEmail, String newName, String newAge, String newEmail, Uri newImage) {
        String oldKey = normalize(currentEmail);
        String newKey = normalize(newEmail);

        // Preserve credentials
        String hash = prefs.getString(pw(oldKey), null);
        String salt = prefs.getString(salt(oldKey), null);
        String existingImg = prefs.getString(img(oldKey), null);

        SharedPreferences.Editor e = prefs.edit();
        if (!oldKey.equals(newKey)) {
            // remove old keys
            e.remove(pw(oldKey)).remove(salt(oldKey)).remove(name(oldKey))
                    .remove(age(oldKey)).remove(email(oldKey)).remove(img(oldKey));
            if (hash != null) e.putString(pw(newKey), hash);
            if (salt != null) e.putString(salt(newKey), salt);
        }
        e.putString(name(newKey), newName);
        e.putString(age(newKey), newAge);
        e.putString(email(newKey), newEmail);

        String imgPath = (newImage != null) ? copyImageToInternal(newKey, newImage) : existingImg;
        if (imgPath != null) {
            e.putString(img(newKey), imgPath);
        }
        e.commit();

        if (oldKey.equals(getCurrentUser()) || newKey.equals(oldKey)) {
            setCurrentUser(newKey);
        }
    }

    // ---- Internals ---------------------------------------------------------

    private String copyImageToInternal(String key, Uri source) {
        try {
            File dir = new File(appContext.getFilesDir(), "profile_images");
            if (!dir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                dir.mkdirs();
            }
            File dest = new File(dir, "profile_" + safeName(key) + ".jpg");
            try (InputStream in = appContext.getContentResolver().openInputStream(source);
                 OutputStream out = new FileOutputStream(dest)) {
                if (in == null) return null;
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) {
                    out.write(buf, 0, n);
                }
            }
            return dest.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH_BITS);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return f.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("Password hashing failed", e);
        }
    }

    private static byte[] randomSalt() {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static String encode(byte[] b) {
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }

    private static byte[] decode(String s) {
        return Base64.decode(s, Base64.NO_WRAP);
    }

    private static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private static String safeName(String key) {
        return key.replaceAll("[^a-zA-Z0-9]", "_");
    }

    // Namespaced preference keys per user
    private static String pw(String k)    { return "u:" + k + ":pwhash"; }
    private static String salt(String k)  { return "u:" + k + ":salt"; }
    private static String name(String k)  { return "u:" + k + ":name"; }
    private static String age(String k)   { return "u:" + k + ":age"; }
    private static String email(String k) { return "u:" + k + ":email"; }
    private static String img(String k)   { return "u:" + k + ":img"; }
}
