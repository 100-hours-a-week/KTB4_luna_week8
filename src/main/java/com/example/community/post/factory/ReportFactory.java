package com.example.community.post.factory;

import com.example.community.post.dto.ReportRequestDTO;
import com.example.community.post.entity.Post;
import com.example.community.post.entity.Report;
import com.example.community.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ReportFactory {
    public Report create(Post post, User reporter, ReportRequestDTO requestDTO) {
        return new Report(post, reporter, requestDTO.getReason(), requestDTO.getDescription());
    }
}
