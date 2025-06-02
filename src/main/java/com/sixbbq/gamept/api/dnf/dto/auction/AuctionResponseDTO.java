package com.sixbbq.gamept.api.dnf.dto.auction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class AuctionResponseDTO {
    private List<AuctionItem> rows;

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuctionItem {
        private Long auctionNo;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime regDate;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime expireDate;
        private String itemId;
        private String itemName;
        private Integer itemAvailableLevel;
        private String itemRarity;
        private String itemTypeId;
        private String itemType;
        private String itemTypeDetailId;
        private String itemTypeDetail;
        private Integer refine;
        private Integer reinforce;
        private String amplificationName;
        private Integer fame;
        private Seal seal;
        private Integer count;
        private Long price;
        private Long currentPrice;
        private Long unitPrice;
        private Long averagePrice;
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Seal {
        private Integer count;
        private Integer limit;
    }
}