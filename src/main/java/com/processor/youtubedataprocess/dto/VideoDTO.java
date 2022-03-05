package com.processor.youtubedataprocess.dto;

import com.processor.youtubedataprocess.domain.YtVideo;

import java.io.Serializable;

public class VideoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String description;
    private final String link;

    public VideoDTO(YtVideo video){
        this.id = video.getId();
        this.description = video.getDescription();
        this.link = video.getLink();
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }
}
