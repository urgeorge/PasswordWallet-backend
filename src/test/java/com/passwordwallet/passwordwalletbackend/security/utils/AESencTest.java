package com.passwordwallet.passwordwalletbackend.security.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

import static org.testng.Assert.*;

public class AESencTest {

    @Test
    public void testEncryptionAndDecryption_succeed_ifPasswordAndKeyAreCorrect() throws Exception {
        //given
        String password = RandomStringUtils.randomAlphanumeric(8);
        String passwordKey = RandomStringUtils.randomAlphanumeric(128);
        String ALGORITHM = "AES";

        //when
        String encryptedPassword = AESenc.encrypt(password, new SecretKeySpec(Arrays.copyOfRange(
                passwordKey.getBytes(),
                passwordKey.getBytes().length - 32,
                passwordKey.getBytes().length),
                ALGORITHM));


        //then
        String decryptedPassword = AESenc.decrypt(encryptedPassword, new SecretKeySpec(Arrays.copyOfRange(
                passwordKey.getBytes(),
                passwordKey.getBytes().length - 32,
                passwordKey.getBytes().length),
                ALGORITHM));

        Assert.assertEquals(decryptedPassword, password);
    }

    @Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
    public void testEncryption_failed_ifPasswordKeyIsTooShort() throws Exception {
        //given
        String password = RandomStringUtils.randomAlphanumeric(8);
        String passwordKey = RandomStringUtils.randomAlphanumeric(8);
        String ALGORITHM = "AES";

        //when
        String encryptedPassword = AESenc.encrypt(password, new SecretKeySpec(Arrays.copyOfRange(
                passwordKey.getBytes(),
                passwordKey.getBytes().length - 32,
                passwordKey.getBytes().length),
                ALGORITHM));

        //then
        Assert.assertEquals(encryptedPassword.length(), 128);

    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testEncryption_failed_ifPasswordIsNull() throws Exception {
        //given
        String password = null;
        String passwordKey = RandomStringUtils.randomAlphanumeric(128);
        String ALGORITHM = "AES";

        //when
        String encryptedPassword = AESenc.encrypt(password, new SecretKeySpec(Arrays.copyOfRange(
                passwordKey.getBytes(),
                passwordKey.getBytes().length - 32,
                passwordKey.getBytes().length),
                ALGORITHM));

        //then
        Assert.assertEquals(encryptedPassword.length(), 128);

    }

}
