package com.space.controller;

import com.space.service.ShipService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/")
public class ShipController {

    private final ShipService shipService;

    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping
    public String list(Map<String, Object> model){

        model.put("ships", shipService.findAll());
        model.put("shipLimits", shipService.getShipLimits());

        return "index";
    }

}
