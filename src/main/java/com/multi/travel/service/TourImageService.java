package com.multi.travel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class TourImageService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String DEFAULT_IMAGE_URL = "/images/default-travel.png"; // 기본 이미지 경로

    @Value("${tour.api.service-key}")
    private String serviceKey;

    public TourImageService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    public String findBestImageWithFallbacks(String title, String district) {
        String imageUrl = searchImageByKeyword(title);
        if (imageUrl != null) {
            return imageUrl;
        }


        String refinedKeyword = refineKeyword(title);
        if (refinedKeyword != null && !refinedKeyword.equals(title)) {
            imageUrl = searchImageByKeyword(refinedKeyword);
            if (imageUrl != null) {
                return imageUrl;
            }
        }

        if (district != null && !district.trim().isEmpty()) {
            imageUrl = searchImageByKeyword(district + " 가볼만한 곳");
            if (imageUrl != null) {
                return imageUrl;
            }
        }

        // 4단계: 모든 검색 실패 시, 기본 이미지 반환
        return DEFAULT_IMAGE_URL;
    }


    private String refineKeyword(String originalKeyword) {
        if (originalKeyword == null) return null;
        String[] parts = originalKeyword.split("\\s+");
        return parts.length > 0 ? parts[0] : originalKeyword;
    }



    private String searchImageByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        try {
            String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

            String urlStr = String.format(
                    "http://apis.data.go.kr/B551011/KorService2/searchKeyword2?serviceKey=%s&keyword=%s&MobileOS=ETC&MobileApp=AppTest&_type=json&numOfRows=1&pageNo=1&arrange=A",
                    encodedServiceKey,
                    encodedKeyword
            );

            String jsonResponse = restTemplate.getForObject(new URI(urlStr), String.class);
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode itemsNode = root.path("response").path("body").path("items").path("item");

            if (itemsNode.isArray() && !itemsNode.isEmpty()) {
                JsonNode firstItem = itemsNode.get(0);
                if (firstItem.has("firstimage") && !firstItem.get("firstimage").asText().isEmpty()) {
                    return firstItem.get("firstimage").asText();
                }
            }
        } catch (Exception e) {
            System.err.printf("TourImageService API 호출 오류 (키워드: %s): %s%n", keyword, e.getMessage());
        }
        return null;
    }
}