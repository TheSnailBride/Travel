package com.multi.travel.mapper;

import com.multi.travel.model.Travel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TravelMapper {
    int countAll(@Param("keyword") String keyword);

    List<Travel> findAllPaged(@Param("offset") int offset, @Param("limit") int limit, @Param("keyword") String keyword);

    Travel findById(int no);
}