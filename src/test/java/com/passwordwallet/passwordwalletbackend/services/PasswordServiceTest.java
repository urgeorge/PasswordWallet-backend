package com.passwordwallet.passwordwalletbackend.services;

import com.passwordwallet.passwordwalletbackend.models.PasswordDTO;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PasswordServiceTest {

    static ArrayList<PasswordDTO> passwordsList = new ArrayList<>();
    static int listSizeCounter = 0;

    @DataProvider(name = "testAddingPasswordsDataProvider")
    public static Object[][] createdPasswords() {

        Object[] passwordsArray = new Object[5];
        passwordsArray[0] = PasswordDTO.builder().id(1L).websiteName("Facebook").password("PasswordFacebook").description("Description1").createdAt(LocalDateTime.now().toString()).updatedAt(LocalDateTime.now().toString()).build();
        passwordsArray[1] = PasswordDTO.builder().id(2L).websiteName("Instagram").password("PasswordInstagram").description("Description2").createdAt(LocalDateTime.now().toString()).updatedAt(LocalDateTime.now().toString()).build();
        passwordsArray[2] = PasswordDTO.builder().id(3L).websiteName("Twitter").password("PasswordTwitter").description("Description3").createdAt(LocalDateTime.now().toString()).updatedAt(LocalDateTime.now().toString()).build();
        passwordsArray[3] = PasswordDTO.builder().id(4L).websiteName("Google").password("PasswordGoogle").description("Description4").createdAt(LocalDateTime.now().toString()).updatedAt(LocalDateTime.now().toString()).build();
        passwordsArray[4] = PasswordDTO.builder().id(5L).websiteName("Microsoft").password("PasswordMicrosoft").description("Description5").createdAt(LocalDateTime.now().toString()).updatedAt(LocalDateTime.now().toString()).build();

        return new Object[][] {{passwordsArray[0]}, {passwordsArray[1]}, {passwordsArray[2]}, {passwordsArray[3]}, {passwordsArray[4]}};
    }

    @Test(dataProvider = "testAddingPasswordsDataProvider")
    public void testAddPassword(PasswordDTO passwordDTO) {
        //given
        listSizeCounter++;

        //when
        passwordsList.add(passwordDTO);

        //then
        Assert.assertEquals(passwordsList.size(), listSizeCounter);
    }

}
