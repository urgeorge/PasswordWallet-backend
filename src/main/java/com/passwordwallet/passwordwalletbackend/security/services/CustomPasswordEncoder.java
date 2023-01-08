package com.passwordwallet.passwordwalletbackend.security.services;

import com.passwordwallet.passwordwalletbackend.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@RequiredArgsConstructor
@AllArgsConstructor
public class CustomPasswordEncoder implements PasswordEncoder {

    @Value("${passwordwallet.app.hmacSecret}")
    private String hmacSecret;

    @Value("${passwordwallet.app.pepperSecret}")
    private String pepperSecret;

    public String calculateSHA512(String text) {
        if(text == null){
            throw new IllegalArgumentException("Calculated text cannot be null");
        }

        text = text+this.pepperSecret;

        try {
            //get an instance of SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            //calculate message digest of the input string - returns byte array
            byte[] messageDigest = md.digest(text.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // If wrong message digest algorithm was specified
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String calculateHMAC(String text){
        if(text == null){
            throw new IllegalArgumentException("Calculated text cannot be null");
        }

        final String HMAC_SHA512 = "HmacSHA512";
        Mac sha512Hmac;
        String result="";
        try {
            final byte[] byteKey = this.hmacSecret.getBytes(StandardCharsets.UTF_8);
            sha512Hmac = Mac.getInstance(HMAC_SHA512);
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
            sha512Hmac.init(keySpec);
            byte[] macData = sha512Hmac.doFinal(text.getBytes(StandardCharsets.UTF_8));
            result = Base64.getEncoder().encodeToString(macData);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return this.calculateSHA512(rawPassword.toString()).equals(encodedPassword)
                || this.calculateHMAC(rawPassword.toString()).equals(encodedPassword);
    }
}
