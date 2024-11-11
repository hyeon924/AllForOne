package com.market.allForOneReview.domain.notice.controller;

import com.market.allForOneReview.domain.notice.entity.NoticeComment;
import com.market.allForOneReview.domain.notice.service.CommentService;
import com.market.allForOneReview.domain.notice.dto.CommentForm;
import com.market.allForOneReview.domain.notice.entity.NoticePost;
import com.market.allForOneReview.domain.notice.service.NoticePostService;

import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final NoticePostService noticePostService;
    private final CommentService commentService;
    private final UserService userService;


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createComment(Model model, @PathVariable("id") Long id, @Valid CommentForm commentForm, BindingResult bindingResult, Principal principal, HttpServletRequest request, HttpServletResponse response){
        NoticePost noticePost = this.noticePostService.getNotice(id,request,response);
        SiteUser siteUser = this.userService.findByUsername(principal.getName());
        if(bindingResult.hasErrors()){
            model.addAttribute("notice", noticePost);
            return "notice/notice_detail";
        }
        this.commentService.create(noticePost,commentForm.getContent(), siteUser);
        return String.format("redirect:/notice/detail/%s" , id);
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String commentModify(CommentForm commentForm, @PathVariable("id") Long id, Principal principal) {
        NoticeComment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        commentForm.setContent(comment.getContent());
        return "notice/comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String commentModify(@Valid CommentForm commentForm, BindingResult bindingResult,
                               @PathVariable("id") Long id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "notice/comment_form";
        }
        NoticeComment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.commentService.modify(comment, commentForm.getContent());
        return String.format("redirect:/notice/detail/%s", comment.getNoticePost().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String commentDelete(Principal principal, @PathVariable("id") Long id) {
        NoticeComment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/notice/detail/%s", comment.getNoticePost().getId());
    }

}
