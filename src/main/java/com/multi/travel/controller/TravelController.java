package com.multi.travel.controller;

import com.multi.travel.model.PagedResult;
import com.multi.travel.model.Travel;
import com.multi.travel.service.TravelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TravelController {
    private final TravelService travelService;

    public TravelController(TravelService travelService) {
        this.travelService = travelService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @ResponseBody
    @GetMapping("/api/travels")
    public PagedResult<Travel> getAllTravels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return travelService.getAllTravels(page, size, keyword);
    }

    @ResponseBody
    @GetMapping("/api/travel/{no}")
    public Travel getTravel(@PathVariable int no) {
        return travelService.getTravelById(no);
    }

}