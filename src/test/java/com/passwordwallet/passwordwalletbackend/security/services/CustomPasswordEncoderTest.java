package com.passwordwallet.passwordwalletbackend.security.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

@ContextConfiguration(classes = CustomPasswordEncoder.class)
public class CustomPasswordEncoderTest extends AbstractTestNGSpringContextTests {

    @Autowired
    CustomPasswordEncoder customPasswordEncoder;

    @Test
    public void testCalculationSHA512_succeed_ifArgumentIsString() {
        //given
        String rawPassword = RandomStringUtils.randomAlphanumeric(8);
        String salt = RandomStringUtils.randomAlphanumeric(20);

        //when
        String hashedPassword = customPasswordEncoder.calculateSHA512(salt + rawPassword);

        //then
        Assert.assertEquals(hashedPassword.length(),128);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCalculationSHA512_failed_ifArgumentIsNull() {

        //when
        String hashedPassword = customPasswordEncoder.calculateSHA512(null);

        //then
        Assert.assertEquals(hashedPassword.length(),128);
    }

    @Test
    public void testCalculationHMAC_succeed_ifArgumentIsString() {
        //given
//        customPasswordEncoder = new CustomPasswordEncoder("hmacSecret", "");
        String rawPassword = RandomStringUtils.randomAlphanumeric(8);

        //when
        String hashedPassword = customPasswordEncoder.calculateHMAC(rawPassword);

        //then
        Assert.assertEquals(hashedPassword.length(),88);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCalculationHMAC_failed_ifArgumentIsNull() {

        //when
        String hashedPassword = customPasswordEncoder.calculateHMAC(null);

        //then
        Assert.assertEquals(hashedPassword.length(),128);
    }
}
