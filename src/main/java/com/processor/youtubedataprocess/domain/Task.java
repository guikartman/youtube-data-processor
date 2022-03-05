package com.processor.youtubedataprocess.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.processor.youtubedataprocess.service.enums.Status;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    private String searchedKey;

    private Status status;

    @JsonIgnore
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private final List<YtVideo> videos = new ArrayList<>();

    public Task(String searchedKey) {
        this.searchedKey = searchedKey;
        status = Status.PROCESSING;
    }

    public Task(){
        status = Status.PROCESSING;
    }

    public Long getTaskId() {
        return taskId;
    }

    public List<YtVideo> getVideos() {
        return videos;
    }

    public Status getStatus() {
        return status;
    }

    public String getSearchedKey() {
        return searchedKey;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(taskId, task.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }
}
