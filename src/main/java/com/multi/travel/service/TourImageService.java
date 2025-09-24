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

    /**
     * 여러 단계의 폴백을 통해 최적의 이미지를 검색하는 메인 메소드
     * @param title 관광지 제목 (예: "경복궁 야간개장")
     * @param district 지역구 이름 (예: "종로구")
     * @return 찾은 이미지 URL 또는 기본 이미지 URL
     */
    public String findBestImageWithFallbacks(String title, String district) {
        // 1단계: 원본 제목 그대로 검색
        String imageUrl = searchImageByKeyword(title);
        if (imageUrl != null) {
            return imageUrl;
        }

        // 2단계: 제목에서 핵심 키워드를 추출하여 검색
        // 예: "경복궁 야간개장" -> "경복궁"
        String refinedKeyword = refineKeyword(title);
        if (refinedKeyword != null && !refinedKeyword.equals(title)) {
            imageUrl = searchImageByKeyword(refinedKeyword);
            if (imageUrl != null) {
                return imageUrl;
            }
        }

        // 3단계: 지역명으로 검색 (예: "종로구 가볼만한 곳")
        if (district != null && !district.trim().isEmpty()) {
            imageUrl = searchImageByKeyword(district + " 가볼만한 곳");
            if (imageUrl != null) {
                return imageUrl;
            }
        }

        // 4단계: 모든 검색 실패 시, 기본 이미지 반환
        return DEFAULT_IMAGE_URL;
    }

    /**
     * 키워드를 정제하여 핵심 단어를 추출하는 헬퍼 메소드
     * (간단한 예시이며, 실제로는 더 정교한 로직이 필요할 수 있습니다)
     */
    private String refineKeyword(String originalKeyword) {
        if (originalKeyword == null) return null;
        // 공백을 기준으로 첫 번째 단어를 핵심 키워드로 간주하는 간단한 로직
        String[] parts = originalKeyword.split("\\s+");
        return parts.length > 0 ? parts[0] : originalKeyword;
    }


    /**
     * 실제 Tour API를 호출하여 이미지를 검색하는 private 메소드 (기존 getMainImage 로직)
     * Optional을 사용하여 null 처리를 더 명확하게 할 수 있지만, 여기서는 간단하게 null을 반환합니다.
     */
    private String searchImageByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        try {
            // 서비스 키는 한 번만 인코딩하는 것이 효율적입니다. (생성자에서 처리 가능)
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
            // 실제 운영 환경에서는 로거(Logger)를 사용하는 것이 좋습니다.
            System.err.printf("TourImageService API 호출 오류 (키워드: %s): %s%n", keyword, e.getMessage());
        }
        return null;
    }
}