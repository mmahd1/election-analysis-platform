package com.team7.hboict.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Team7ApplicationTests {

    private final HomeService homeService = new HomeService();

    @Test
    @DisplayName("Service test")
    void demoTestServiceMethod() {
        String expectedString = "Dit is voor home later.";
        String actualString = homeService.printService();

        assertEquals(expectedString, actualString);
    }
}