package com.webbeam.courseplatform.repository;

import com.webbeam.courseplatform.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ModuleRepository extends JpaRepository<Module, Long> {
}
