package com.market.allForOneReview.domain.notice.controller;

import com.market.allForOneReview.domain.notice.dto.CommentForm;
import com.market.allForOneReview.domain.notice.dto.NoticeForm;
import com.market.allForOneReview.domain.notice.entity.NoticePost;

import com.market.allForOneReview.domain.notice.service.NoticePostService;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticePostController {
    private final NoticePostService noticePostService;
    private final UserService userService;

    @GetMapping("")
    public String notice(
            @RequestParam(value="boardType", defaultValue="Notice") String boardType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        // 공통된 처리 로직
        Page<NoticePost> paging = noticePostService.getNoticesByBoardTypeAndPage(boardType, page);

        model.addAttribute("paging", paging);
        model.addAttribute("currentBoardType", boardType);

        // boardType에 따라 다른 데이터를 추가
        if (boardType.equals("FAQ")) {
            model.addAttribute("faqs", paging.getContent());
        } else {
            model.addAttribute("notices", paging.getContent());
        }

        return "notice/notice";
    }


    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id")Long id, CommentForm commentForm, HttpServletRequest request, HttpServletResponse response) {
        NoticePost noticePost = this.noticePostService.getNotice(id,request, response);
        model.addAttribute("notice", noticePost);
        return "notice/notice_detail";
    }

    @GetMapping("/create")
    public String create(NoticeForm noticeform){
        return "notice/notice_create";
    }

    @PostMapping("/create")
    public String create(@Valid NoticeForm noticeForm, BindingResult bindingResult, Principal principal){
        if(bindingResult.hasErrors()){
            return "notice/notice_create";
        }
        SiteUser siteUser = this.userService.findByUsername(principal.getName());
        this.noticePostService.create(noticeForm.getTitle(), noticeForm.getContent(), noticeForm.getBoardType(),siteUser);
        return "redirect:/notice";
    }
    @GetMapping("/modify/{id}")
    public String noticeModify(NoticeForm noticeForm, @PathVariable("id")Long id,HttpServletRequest request, HttpServletResponse response){
        NoticePost notice = this.noticePostService.getNotice(id,request,response);
        noticeForm.setTitle(notice.getTitle());
        noticeForm.setContent(notice.getContent());
        return "notice/notice_create";
    }
    @PostMapping("/modify/{id}")
    public String noticeModify(@Valid NoticeForm noticeForm, BindingResult bindingResult, @PathVariable("id") Long id,HttpServletRequest request, HttpServletResponse response){
        if (bindingResult.hasErrors()) {
            return "notice/notice_create";
        }
        NoticePost notice = this.noticePostService.getNotice(id, request, response);
        this.noticePostService.modify(notice, noticeForm.getTitle(), noticeForm.getContent());
        return String.format("redirect:/notice/detail/%s",id);
    }

    @GetMapping("/delete/{id}")
    public String noticeDelete(@PathVariable("id")Long id,HttpServletRequest request, HttpServletResponse response){
        NoticePost notice = this.noticePostService.getNotice(id,request,response);
        this.noticePostService.delete(notice);
        return "redirect:/notice";
    }

    @GetMapping("/search")
    public String searchNotices(
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "boardType", defaultValue = "Notice") String boardType,
            @RequestParam(value = "searchType", defaultValue = "all") String searchType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        Page<NoticePost> paging = noticePostService.searchNoticePosts(searchTerm, boardType, searchType, page, 10); // 페이지 크기를 10으로 설정

        model.addAttribute("paging", paging);
        model.addAttribute("currentBoardType", boardType);
        model.addAttribute("searchTerm", searchTerm); // 검색어를 모델에 추가

        // boardType에 따라 다른 데이터를 추가
        if (boardType.equals("FAQ")) {
            model.addAttribute("faqs", paging.getContent());
        } else {
            model.addAttribute("notices", paging.getContent());
        }
        return "notice/notice"; // 검색 결과를 notice 템플릿에서 표시
    }

}
