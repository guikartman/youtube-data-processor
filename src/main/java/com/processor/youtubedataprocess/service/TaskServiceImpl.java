package com.processor.youtubedataprocess.service;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.processor.youtubedataprocess.domain.Task;
import com.processor.youtubedataprocess.domain.YtVideo;
import com.processor.youtubedataprocess.repository.TaskRepository;
import com.processor.youtubedataprocess.repository.YtVideoRepository;
import com.processor.youtubedataprocess.service.enums.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.processor.youtubedataprocess.service.YoutubeDataHandle.generateLink;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final YtVideoRepository videoRepository;
    private final YoutubeDataHandle youtubeDataHandle;

    public TaskServiceImpl(TaskRepository taskRepository, YtVideoRepository videoRepository, YoutubeDataHandle youtubeDataHandle) {
        this.taskRepository = taskRepository;
        this.videoRepository = videoRepository;
        this.youtubeDataHandle = youtubeDataHandle;
    }

    @Override
    public Long createNewTask(String channelId) {
        LOGGER.info("TaskServiceImpl - createNewTask - New task is being created, received channelID = {}",channelId);
        Task task = new Task(channelId);
        Task persistedEntity = taskRepository.save(task);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new YoutubeTask(youtubeDataHandle, task));
        LOGGER.info("TaskServiceImpl - createNewTask - The app is processing the new task in an async mode.");
        return persistedEntity.getTaskId();
    }

    @Override
    public List<Task> retrieveAllTasks() {
        LOGGER.info("TaskServiceImpl - retrieveAllTasks - Getting persisted tasks...");
        return taskRepository.findAll();
    }

    @Override
    public List<YtVideo> getVideosByTaskId(Long id) {
        List<YtVideo> videos = Collections.emptyList();
        LOGGER.info("TaskServiceImpl - getVideosByTaskId - Getting videos by task id...");
        Task task = taskRepository.getById(id);
        if (Objects.isNull(task)) {
            LOGGER.warn("TaskServiceImpl - getVideosByTaskId - Couldn't find any task with id = {}", id);
        } else  {
            videos = videoRepository.findAllByTask(task);
        }
        return videos;
    }

    public class YoutubeTask implements Runnable {

        private final YoutubeDataHandle youtubeDataHandle;
        private final Task task;

        public YoutubeTask(YoutubeDataHandle youtubeDataHandle,
                           Task task) {
            this.youtubeDataHandle = youtubeDataHandle;
            this.task = task;
        }

        @Override
        public void run() {
            LOGGER.info("TaskServiceImpl - run - The task started the process to get the videos.");
            try {
                if (Objects.isNull(task.getSearchedKey())) {
                    LOGGER.error("TaskServiceImpl - run - The channelId is mandatory.");
                    return;
                }
                youtubeDataHandle.retrieveDataFromAChannel(task.getSearchedKey()).getItems().forEach(response -> {
                    ResourceId resourceId = response.getId();
                    SearchResultSnippet snippet = response.getSnippet();
                    if (Objects.nonNull(resourceId) && Objects.nonNull(resourceId.getVideoId()) && Objects.nonNull(snippet)) {
                        String videoId = resourceId.getVideoId();
                        YtVideo video = new YtVideo(videoId, snippet.getDescription(), generateLink(videoId), task);
                        task.getVideos().add(video);
                        if (LOGGER.isDebugEnabled())
                            LOGGER.debug("TaskServiceImpl - run - Video information retrieved = {}", video);
                    }
                });
            } catch (IOException e) {
                LOGGER.error("TaskServiceImpl - run - The thread failed on processing the videos for the channelId = {}", task.getTaskId(), e);
                task.setStatus(Status.FAILED);
            }
            if (task.getStatus().equals(Status.PROCESSING))
                task.setStatus(Status.COMPLETED);
            taskRepository.save(task);
            LOGGER.info("TaskServiceImpl - run - The task completed the process with success.");
        }
    }
}
