package com.palominolabs.bradybunch.core;

import com.google.common.base.Throwables;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

@Entity
@Table(name = "people")
@NamedQueries({
    @NamedQuery(
        name = "com.palominolabs.bradybunch.core.Person.findByEmail",
        query = "SELECT p FROM Person p WHERE p.email = :email"
    ),
    @NamedQuery(
        name = "com.palominolabs.bradybunch.core.Person.findAll",
        query = "SELECT p FROM Person p"
    )
})
public class Person {
    private static final int iterations = 10*1024;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCorrectPassword(String password) {
        return checkPassword(password, this.password);
    }

    public void setPassword(String password) {
        this.password = getSaltedHash(password);
    }


    // http://stackoverflow.com/a/11038230/17339
    private static boolean checkPassword(String password, String stored) throws IllegalArgumentException {
        String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length != 2)
            return false;
        String hashOfInput = hash(password, Base64.decodeBase64(saltAndPass[0]));
        return hashOfInput.equals(saltAndPass[1]);
    }

    private static String getSaltedHash(String password) {
        byte[] salt = new byte[0];
        try {
            salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        } catch (NoSuchAlgorithmException e) {
            Throwables.propagate(e);
        }

        return Base64.encodeBase64String(salt) + "$" + hash(password, salt);
    }

    private static String hash(String password, byte[] salt) throws IllegalArgumentException {
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Empty passwords are not supported.");

        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey key = f.generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterations, desiredKeyLen));
            return Base64.encodeBase64String(key.getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Throwables.propagate(e);
        }

        return null;
    }
}
