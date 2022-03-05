package com.processor.youtubedataprocess.service;

import com.processor.youtubedataprocess.domain.Task;
import com.processor.youtubedataprocess.domain.YtVideo;

import java.util.List;

public interface TaskService {
    Long createNewTask(String channelId);

    List<Task> retrieveAllTasks();

    List<YtVideo> getVideosByTaskId(Long id);
}
