package com.webbeam.courseplatform.config;

import com.webbeam.courseplatform.model.Course;
import com.webbeam.courseplatform.model.Module;
import com.webbeam.courseplatform.model.User;
import com.webbeam.courseplatform.model.Video;
import com.webbeam.courseplatform.repository.CourseRepository;
import com.webbeam.courseplatform.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataLoader {

//    @Bean
//    public CommandLineRunner initDatabase(CourseRepository courseRepository) {
//        return args -> {
//            Course course = new Course();
//            course.setTitle("Разработка сайта на WordPress");
//            course.setDescription("С 0 до результата");
//            course.setIcons(List.of("html.png", "css.png", "php.png", "server.png", "wp.png"));
//
//            Course course2 = new Course();
//            course2.setTitle("Git и GitHub полный курс");
//            course2.setDescription("Изучите систему управления версиями");
//            course2.setIcons(List.of("gith.png", "github.png", "branch.png"));
//
//            Module module21 = new Module();
//            module21.setTitle("Конфигурация Spring");
//            module21.setCourse(course2);
//
//            Module module1 = new Module();
//            module1.setTitle("Введение в Java");
//            module1.setCourse(course);
//
//            Module module2 = new Module();
//            module2.setTitle("ООП в Java");
//            module2.setCourse(course);
//
//            Video video1 = new Video();
//            video1.setTitle("Что такое Java?");
//            video1.setFileName("test.webm");
//            video1.setModule(module1);
//
//            Video video2 = new Video();
//            video2.setTitle("Классы и объекты");
//            video2.setFileName("test2.webm");
//            video2.setModule(module2);
//
//            Video video21 = new Video();
//            video21.setTitle("Аннотации и конфигурация");
//            video21.setFileName("");
//            video21.setModule(module21);
//
//            module1.setVideos(List.of(video1));
//            module2.setVideos(List.of(video2));
//            module21.setVideos(List.of(video21));
//
//            course.setModules(List.of(module1, module2));
//            course2.setModules(List.of(module21));
//
//            courseRepository.save(course);
//            courseRepository.save(course2);
//        };
//    }

    @Bean
    public CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123")); // Сменить на свой надёжный пароль
                admin.setRole("ADMIN");
                userRepository.save(admin);
            }
        };
    }
}
