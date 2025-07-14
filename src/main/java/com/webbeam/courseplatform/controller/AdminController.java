package com.webbeam.courseplatform.controller;

import com.webbeam.courseplatform.model.Course;
import com.webbeam.courseplatform.model.User;
import com.webbeam.courseplatform.model.Video;
import com.webbeam.courseplatform.repository.CourseRepository;
import com.webbeam.courseplatform.repository.ModuleRepository;
import com.webbeam.courseplatform.repository.UserRepository;
import com.webbeam.courseplatform.repository.VideoRepository;
import com.webbeam.courseplatform.model.Module;

import jakarta.servlet.http.HttpServletRequest;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    private String cleanHtml(String input) {
        return Jsoup.clean(input, "", Safelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

    @GetMapping("/panel")
    public String panel(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("courses", courseRepository.findAll());
        return "admin/panel";
    }

    @PostMapping("/save-course")
    public String saveCourse(@RequestParam String title,
                             @RequestParam String description,
                             @RequestParam("icons") MultipartFile[] icons) throws IOException {

        Course course = new Course();
        course.setTitle(cleanHtml(title));
        course.setDescription(cleanHtml(description));

        List<String> iconFileNames = new ArrayList<>();
        String uploadPath = new ClassPathResource("static/img").getFile().getAbsolutePath();

        for (MultipartFile icon : icons) {
            if (!icon.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + icon.getOriginalFilename();
                File dest = new File(uploadPath, fileName);
                icon.transferTo(dest);
                iconFileNames.add(fileName);
            }
        }

        course.setIcons(iconFileNames);
        courseRepository.save(course);

        return "redirect:/admin/panel?success";
    }

    @GetMapping("/module-form/{courseId}")
    public String showModuleForm(@PathVariable Long courseId, Model model) {
        model.addAttribute("courseId", courseId);
        return "admin/module-form";
    }

    @PostMapping("/save-module")
    public String saveModule(@RequestParam Long courseId,
                             @RequestParam String title,
                             @RequestParam String description) {

        Course course = courseRepository.findById(courseId).orElseThrow();
        Module module = new Module();
        module.setTitle(cleanHtml(title));
        module.setDescription(cleanHtml(description));
        module.setCourse(course);

        if (course.getModules() == null) {
            course.setModules(new ArrayList<>());
        }

        course.getModules().add(module);
        courseRepository.save(course);

        return "redirect:/admin/panel";
    }

    @GetMapping("/video-form/{moduleId}")
    public String showVideoForm(@PathVariable Long moduleId, Model model) {
        model.addAttribute("moduleId", moduleId);
        return "admin/video-form";
    }

    @PostMapping("/save-video")
    public String saveVideo(@RequestParam Long moduleId,
                            @RequestParam String title,
                            @RequestParam("file") MultipartFile file,
                            @RequestParam String description) throws IOException {

        Module module = moduleRepository.findById(moduleId).orElseThrow();
        Video video = new Video();
        video.setTitle(cleanHtml(title));
        video.setDescription(cleanHtml(description));
        video.setModule(module);

        if (!file.isEmpty()) {
            String uploadPath = new ClassPathResource("/protected-videos").getFile().getAbsolutePath();
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(uploadPath, fileName);
            file.transferTo(dest);
            video.setFileName(fileName);
        }

        if (module.getVideos() == null) {
            module.setVideos(new ArrayList<>());
        }

        module.getVideos().add(video);
        moduleRepository.save(module);

        return "redirect:/admin/panel?videoAdded";
    }

    @GetMapping
    public String redirectNonAdminUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        if (!user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/courses";
        }
        return "redirect:/admin/panel";
    }

    @GetMapping("/edit-course/{id}")
    public String editCourse(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id).orElseThrow();
        model.addAttribute("course", course);
        return "admin/edit-course";
    }

    @PostMapping("/update-course")
    public String updateCourse(@RequestParam Long id,
                               @RequestParam String title,
                               @RequestParam String description,
                               @RequestParam(value = "deleteIcons", required = false) String deleteIcons,
                               @RequestParam("icons") MultipartFile[] icons) throws IOException {

        Course course = courseRepository.findById(id).orElseThrow();
        course.setTitle(title);
        course.setDescription(description);

        String uploadPath = new ClassPathResource("static/img").getFile().getAbsolutePath();

        if (deleteIcons != null && deleteIcons.equals("on")) {
            // Удаляем старые файлы с диска
            if (course.getIcons() != null) {
                for (String icon : course.getIcons()) {
                    File file = new File(uploadPath, icon);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
            course.setIcons(new ArrayList<>());
        }

        if (icons != null && icons.length > 0 && !icons[0].isEmpty()) {
            List<String> iconFileNames = new ArrayList<>();

            for (MultipartFile icon : icons) {
                if (!icon.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_" + icon.getOriginalFilename();
                    File dest = new File(uploadPath, fileName);
                    icon.transferTo(dest);
                    iconFileNames.add(fileName);
                }
            }

            course.setIcons(iconFileNames);
        }

        courseRepository.save(course);
        return "redirect:/admin/panel?updated";
    }

    @GetMapping("/delete-course/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        return "redirect:/admin/panel?deleted";
    }

    @PostMapping("/update-module")
    public String updateModule(@RequestParam Long id,
                               @RequestParam Long courseId,
                               @RequestParam String title,
                               @RequestParam String description) {

        Module module = moduleRepository.findById(id).orElseThrow();
        module.setTitle(cleanHtml(title));
        module.setDescription(cleanHtml(description));

        moduleRepository.save(module);
        return "redirect:/admin/panel?moduleUpdated";
    }

    @GetMapping("/edit-module/{id}")
    public String editModule(@PathVariable Long id, Model model) {
        Module module = moduleRepository.findById(id).orElseThrow();
        model.addAttribute("module", module);
        return "admin/edit-module";
    }

    @GetMapping("/delete-module/{id}")
    public String deleteModule(@PathVariable Long id) {
        moduleRepository.deleteById(id);
        return "redirect:/admin/panel?moduleDeleted";
    }

    @GetMapping("/edit-video/{id}")
    public String editVideo(@PathVariable Long id, Model model) {
        Video video = videoRepository.findById(id).orElseThrow();
        model.addAttribute("video", video);
        return "admin/edit-video";
    }

    @PostMapping("/update-video")
    public String updateVideo(@RequestParam Long id,
                              @RequestParam Long moduleId,
                              @RequestParam String title,
                              @RequestParam String description,
                              @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        Video video = videoRepository.findById(id).orElseThrow();
        video.setTitle(cleanHtml(title));
        video.setDescription(cleanHtml(description));

        if (file != null && !file.isEmpty()) {
            String uploadPath = new ClassPathResource("/protected-videos").getFile().getAbsolutePath();
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(uploadPath, fileName);
            file.transferTo(dest);
            video.setFileName(fileName);
        }

        videoRepository.save(video);
        return "redirect:/admin/panel?videoUpdated";
    }

    @GetMapping("/delete-video/{id}")
    public String deleteVideo(@PathVariable Long id) {
        videoRepository.deleteById(id);
        return "redirect:/admin/panel?videoDeleted";
    }


    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/user-form")
    public String showUserForm() {
        return "admin/user-form";
    }

    @PostMapping("/save-user")
    public String saveUser(@RequestParam("username") String username,
                           @RequestParam("role") String role) {
        if (userRepository.existsByUsername(username)) {
            return "redirect:/admin/panel?userExists";
        }

        User user = new User();
        user.setUsername(username);
        user.setRole(role);
        user.setEnabled(true);
        userRepository.save(user);

        return "redirect:/admin/panel?userAdded";
    }

}
