package com.processor.youtubedataprocess.controller;

import com.processor.youtubedataprocess.domain.Task;
import com.processor.youtubedataprocess.dto.VideoDTO;
import com.processor.youtubedataprocess.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class YouTubeProcessorController {

    private final TaskService taskService;

    public YouTubeProcessorController( TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Long> createNewTask(@RequestParam(name = "channelId") String channelId) throws IOException {
        Long taskId = taskService.createNewTask(channelId);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(taskId)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping
    public ResponseEntity<List<Task>> retrieveAllTasks() {
        List<Task> tasks = taskService.retrieveAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<List<VideoDTO>> retrieveVideosFromTask(@PathVariable(name = "taskId") Long taskId) {
        List<VideoDTO> videos = taskService.getVideosByTaskId(taskId)
                .stream().map(VideoDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(videos);
    }
}
