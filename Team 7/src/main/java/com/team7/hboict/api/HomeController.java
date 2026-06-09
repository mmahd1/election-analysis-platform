package com.team7.hboict.api;

import com.team7.hboict.service.HomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home-api")
public class HomeController {
    @GetMapping("/home")
    public String index() {
        HomeService homeService = new HomeService();
        homeService.printService();
        return homeService.printService();
    }
}
