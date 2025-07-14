package com.webbeam.courseplatform.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class VideoController {

    @GetMapping("/secure-video/{filename}")
    public void getVideo(@PathVariable String filename,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String ajaxHeader = request.getHeader("X-Requested-With");
        if (ajaxHeader == null || !ajaxHeader.equals("XMLHttpRequest")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Direct access not allowed");
            return;
        }

        ClassPathResource videoFile = new ClassPathResource("protected-videos/" + filename);
        if (!videoFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("video/webm");
        StreamUtils.copy(videoFile.getInputStream(), response.getOutputStream());
        response.flushBuffer();
    }
}
