package com.sixbbq.gamept.api.dnf.dto.auction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuctionResponseDTO {
    private List<AuctionItem> rows;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuctionItem {
        private Long auctionNo;
        private String regDate;
        private String expireDate;
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
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Seal {
        private Integer count;
        private Integer limit;
    }
}