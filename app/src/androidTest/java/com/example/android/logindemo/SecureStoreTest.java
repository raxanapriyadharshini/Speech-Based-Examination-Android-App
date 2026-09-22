package com.example.android.logindemo;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Deterministic tests for the fully-local, encrypted auth store.
 * Verifies authentication logic AND that no secret is written in plaintext.
 */
@RunWith(AndroidJUnit4.class)
public class SecureStoreTest {

    private static final String EMAIL = "teacher@example.com";
    private static final String PASSWORD = "SecurePass123";

    private Context ctx;
    private SecureStore store;

    @Before
    public void setUp() {
        ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        store = SecureStore.get(ctx);
        // Start each test from a clean slate (synchronous).
        store.clearAll();
    }

    private File prefsFile() {
        return new File(ctx.getFilesDir().getParentFile(), "shared_prefs/secure_user_store.xml");
    }

    @Test
    public void register_then_authenticate() {
        assertTrue("first registration succeeds",
                store.register(EMAIL, PASSWORD, "Teacher", "35", null));
        assertFalse("duplicate registration is rejected",
                store.register(EMAIL, PASSWORD, "Other", "40", null));
        assertTrue("correct credentials authenticate",
                store.authenticate(EMAIL, PASSWORD));
        assertTrue("email is case-insensitive",
                store.authenticate("TEACHER@example.com", PASSWORD));
        assertFalse("wrong password is rejected",
                store.authenticate(EMAIL, "wrongpassword"));
        assertFalse("unknown user is rejected",
                store.authenticate("nobody@example.com", PASSWORD));
    }

    @Test
    public void update_password_invalidates_old() {
        store.register(EMAIL, PASSWORD, "Teacher", "35", null);
        assertTrue(store.updatePassword(EMAIL, "BrandNewSecret999"));
        assertFalse("old password no longer works", store.authenticate(EMAIL, PASSWORD));
        assertTrue("new password works", store.authenticate(EMAIL, "BrandNewSecret999"));
    }

    @Test
    public void secrets_are_never_stored_in_plaintext() throws Exception {
        store.register(EMAIL, PASSWORD, "Teacher", "35", null);
        File f = prefsFile();
        assertTrue("encrypted store file exists", f.exists());
        String onDisk = new String(Files.readAllBytes(f.toPath()));
        assertFalse("plaintext password must not appear on disk", onDisk.contains(PASSWORD));
        assertFalse("plaintext email must not appear on disk", onDisk.contains(EMAIL));
        assertFalse("plaintext name must not appear on disk", onDisk.contains("Teacher"));
    }
}
