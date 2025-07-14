package com.webbeam.courseplatform.repository;

import com.webbeam.courseplatform.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
