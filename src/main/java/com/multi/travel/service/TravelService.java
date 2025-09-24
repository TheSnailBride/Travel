package com.multi.travel.service;

import com.multi.travel.mapper.TravelMapper;
import com.multi.travel.model.PagedResult;
import com.multi.travel.model.Travel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TravelService {
    private final TravelMapper travelMapper;
    private final TourImageService tourImageService;

    public TravelService(TravelMapper travelMapper, TourImageService tourImageService) {
        this.travelMapper = travelMapper;
        this.tourImageService = tourImageService;
    }

    public PagedResult<Travel> getAllTravels(int page, int size, String keyword) {
        int offset = page * size;
        List<Travel> items = travelMapper.findAllPaged(offset, size, keyword);
        int totalCount = travelMapper.countAll(keyword);

        for (Travel item : items) {
            String imageUrl = tourImageService.findBestImageWithFallbacks(item.getTitle(), item.getDistrict());
            item.setMainImage(imageUrl);
        }

        return new PagedResult<>(items, totalCount);
    }

    public Travel getTravelById(int no) {
        Travel item = travelMapper.findById(no);
        if (item != null) {
            String imageUrl = tourImageService.findBestImageWithFallbacks(item.getTitle(), item.getDistrict());
            item.setMainImage(imageUrl);
        }
        return item;
    }

}