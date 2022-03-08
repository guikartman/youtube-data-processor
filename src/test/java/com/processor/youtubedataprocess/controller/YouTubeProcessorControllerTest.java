package com.processor.youtubedataprocess.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.processor.youtubedataprocess.controller.exception.StandardError;
import com.processor.youtubedataprocess.domain.Task;
import com.processor.youtubedataprocess.domain.YtVideo;
import com.processor.youtubedataprocess.dto.VideoDTO;
import com.processor.youtubedataprocess.service.TaskService;
import com.processor.youtubedataprocess.service.enums.Status;
import com.processor.youtubedataprocess.service.exception.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(YouTubeProcessorController.class)
class YouTubeProcessorControllerTest {

    public static final String BASE_URL = "/api/tasks";

    @MockBean
    private TaskService taskService;

    @Autowired
    private MockMvc mvc;

    private JacksonTester<List<Task>> tasksJson;
    private JacksonTester<List<VideoDTO>> videosJson;
    private JacksonTester<StandardError> standardErrorJson;

    @BeforeEach
    void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }


    @Test
    void whenCallCreateNewTask_shouldReturn201CodeAndTheRightLocation() throws Exception {
        String mockedChannelId = "MockedId";
        String location = "http://localhost"+BASE_URL+"/3";

        given(taskService.createNewTask(mockedChannelId))
                .willReturn(3L);

        var response = mvc.perform(
                post(BASE_URL)
                        .param("channelId", mockedChannelId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("location"))
                .isEqualTo(location);
    }

    @Test
    void whenCallRetrieveAllTasks_shouldReturn200CodeAndTheMockedTask() throws Exception {
        Task task = new Task("mockedTask");
        task.setStatus(Status.COMPLETED);

        given(taskService.retrieveAllTasks())
                .willReturn(Collections.singletonList(task));

        var response = mvc.perform(
                get(BASE_URL)
                    .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString())
                .isEqualTo(tasksJson.write(Collections.singletonList(task)).getJson());
    }

    @Test
    void whenCallRetrieveVideosFromTaskAnExistTaskId_shouldReturn200CodeAndTheVideosList() throws Exception {
        YtVideo video = new YtVideo("mockedId",
                "Mocked description.", "mocked-link.com", new Task("mockedTask"));
        VideoDTO videoDTO = new VideoDTO(video);

        given(taskService.getVideosByTaskId(21L))
                .willReturn(List.of(video));

        var response = mvc.perform(
                get(BASE_URL+"/21")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString())
                .isEqualTo(videosJson.write(List.of(videoDTO)).getJson());
    }

    @Test
    void whenCallRetrieveVideosFromTaskPassingAnNotFoundTaskId_shouldReturn400Code() throws Exception {
        long taskId = 7;

        given(taskService.getVideosByTaskId(taskId))
                .willThrow(new TaskNotFoundException(taskId));

        var response = mvc.perform(
                get(BASE_URL+"/7")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}