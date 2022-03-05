package com.processor.youtubedataprocess.repository;

import com.processor.youtubedataprocess.domain.Task;
import com.processor.youtubedataprocess.domain.YtVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YtVideoRepository extends JpaRepository<YtVideo, String> {
    List<YtVideo> findAllByTask(Task task);
}
