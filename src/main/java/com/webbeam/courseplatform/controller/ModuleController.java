package com.webbeam.courseplatform.controller;

import com.webbeam.courseplatform.model.Module;
import com.webbeam.courseplatform.model.Video;
import com.webbeam.courseplatform.repository.ModuleRepository;
import com.webbeam.courseplatform.service.MarkdownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ModuleController {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private MarkdownService markdownService;

    @GetMapping("/modules/{id}")
    public String getModuleDetail(@PathVariable Long id, Model model) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Модуль не найден"));

        // Преобразуем описание каждого видео из Markdown в HTML
        for (Video video : module.getVideos()) {
            String html = markdownService.toHtml(video.getDescription());
            video.setDescription(html);
        }

        model.addAttribute("module", module);
        return "module-detail";
    }


}
