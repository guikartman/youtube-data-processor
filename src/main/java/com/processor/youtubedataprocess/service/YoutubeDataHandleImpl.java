package com.processor.youtubedataprocess.service;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class YoutubeDataHandleImpl implements YoutubeDataHandle{

    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private final JsonFactory JSON_FACTORY = new JacksonFactory();
    private YouTube youtube;

    private final String API_KEY;
    private final Long maxResults;

    public YoutubeDataHandleImpl(@Value("${google.api.youtube.key}") String API_KEY, @Value("${google.api.youtube.max-results}") Long maxResults) {
        this.API_KEY = API_KEY;
        youtube = createYoutubeInstance();
        this.maxResults = maxResults;
    }

    @Override
    public SearchListResponse retrieveDataFromAChannel(String channelId) throws IOException {
        var videoSearch = youtube.search().list("id,snippet");
        videoSearch.setKey(API_KEY);
        videoSearch.setQ(channelId);
        videoSearch.setType("video");
        videoSearch.setMaxResults(maxResults);
        return videoSearch.execute();
    }

    private YouTube createYoutubeInstance() {
        return new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {}})
                .setApplicationName("youtube-data-processor")
                .build();
    }
}
