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
            // ❗️❗️❗️ 이 부분이 수정되었습니다. ❗️❗️❗️
            // 기존: tourImageService.getMainImage(item.getTitle());
            // 변경: title과 district를 모두 사용하여 최적의 이미지를 찾습니다.
            String imageUrl = tourImageService.findBestImageWithFallbacks(item.getTitle(), item.getDistrict());
            item.setMainImage(imageUrl);
        }

        return new PagedResult<>(items, totalCount);
    }

    public Travel getTravelById(int no) {
        Travel item = travelMapper.findById(no);
        if (item != null) {
            // ❗️❗️❗️ 이 부분도 함께 수정합니다. ❗️❗️❗️
            String imageUrl = tourImageService.findBestImageWithFallbacks(item.getTitle(), item.getDistrict());
            item.setMainImage(imageUrl);
        }
        return item;
    }
}