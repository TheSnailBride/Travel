package com.multi.travel.service;

import com.multi.travel.mapper.TravelMapper;
import com.multi.travel.model.PagedResult;
import com.multi.travel.model.Travel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TravelService {
    private final TravelMapper travelMapper;

    public TravelService(TravelMapper travelMapper) {
        this.travelMapper = travelMapper;
    }

    public PagedResult<Travel> getAllTravels(int page, int size, String keyword) {
        int offset = page * size;
        List<Travel> items = travelMapper.findAllPaged(offset, size, keyword);
        int totalCount = travelMapper.countAll(keyword);
        return new PagedResult<>(items, totalCount);
    }

    public Travel getTravelById(int no) {
        return travelMapper.findById(no);
    }
}