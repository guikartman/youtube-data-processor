package com.processor.youtubedataprocess.service.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long taskId) {
        super("Couldn't find any task with the id: "+taskId);
    }
}
