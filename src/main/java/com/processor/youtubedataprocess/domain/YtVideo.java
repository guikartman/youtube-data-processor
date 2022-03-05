package com.processor.youtubedataprocess.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "video")
public class YtVideo {

    @Id
    private String id;

    private String description;
    private String link;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private final Task task;

    public YtVideo(String id, String description, String link, Task task) {
        this.id = id;
        this.description = description;
        this.link = link;
        this.task = task;
    }

    public YtVideo() {
        task = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Task getTask() {
        return task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YtVideo video = (YtVideo) o;
        return Objects.equals(id, video.id) && Objects.equals(task, video.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, task);
    }

    @Override
    public String toString() {
        return "YtVideo { id=" + id + ", description=" + description +
                ", link=" + link + ", task=" + task + " }";
    }
}
