package com.artgallery.onlineartgallery.dto;

public record GalleryStatsResponse(
        long totalArtworks,
        long availableArtworks,
        long soldArtworks,
        long featuredArtworks
) {
}
