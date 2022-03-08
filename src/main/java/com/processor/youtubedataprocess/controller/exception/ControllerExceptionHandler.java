package com.processor.youtubedataprocess.controller.exception;

import com.processor.youtubedataprocess.service.exception.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<StandardError> taskNotFoundException(TaskNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorMessage = "Task not found.";
        StandardError err = new StandardError(Instant.now(),
                status.value(), errorMessage, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}
