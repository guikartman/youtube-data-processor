package com.processor.youtubedataprocess.service;

import com.google.api.services.youtube.model.SearchListResponse;

import java.io.IOException;

public interface YoutubeDataHandle {
    SearchListResponse retrieveDataFromAChannel(String channelId) throws IOException;

    String VIDEO_LINK_BASE_URL = "https://www.youtube.com/watch?v=";

    static String generateLink(String videoId) {
        return VIDEO_LINK_BASE_URL + videoId;
    }
}
