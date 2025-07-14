package com.webbeam.courseplatform.repository;

import com.webbeam.courseplatform.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
