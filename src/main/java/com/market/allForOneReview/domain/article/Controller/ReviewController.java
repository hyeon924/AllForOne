package com.market.allForOneReview.domain.article.Controller;

import com.market.allForOneReview.domain.answer.dto.AnswerForm;
import com.market.allForOneReview.domain.article.Service.ReviewService;
import com.market.allForOneReview.domain.article.entity.Review;
import com.market.allForOneReview.domain.article.dto.ReviewForm;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class  ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/sub")
    public String showReviews(
            @RequestParam(value="category", defaultValue="drama") String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        Page<Review> paging = reviewService.getReviewsByCategoryAndPage(category, page);
        model.addAttribute("paging", paging);
        model.addAttribute("currentCategory", category);
        model.addAttribute("reviews", paging.getContent());


        return "sub/sub";
    }



    @GetMapping("/sub/search")
    public String searchReviews(@RequestParam("filter") String filter,
                                @RequestParam("query") String query,
                                @RequestParam(value="category", defaultValue="drama") String category,
                                @RequestParam(value = "subCategory", defaultValue = "all") String subCategory,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                Model model) {
        Page<Review> paging = reviewService.searchReviews(filter, query, "drama", subCategory, page);
        model.addAttribute("paging", paging);
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentSubCategory", subCategory);
        model.addAttribute("filter", filter);
        model.addAttribute("query", query);
        model.addAttribute("reviews", paging.getContent());


        return "sub/sub";
    }




    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id, AnswerForm answerForm) {
        Review review = this.reviewService.getReview(id);
        model.addAttribute("review", review);
        return "sub/sub_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String reviewCreate(ReviewForm reviewForm) {
        return "review/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String reviewCreate(@Valid ReviewForm reviewForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "review/create";
        }
        SiteUser siteUser = this.userService.findByUsername(principal.getName());
        this.reviewService.create(reviewForm.getTitle(), reviewForm.getContentStory(), reviewForm.getContent(), reviewForm.getCategory(), reviewForm.getSubCategory(), siteUser);

        return "redirect:/review/sub";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String reviewModify(ReviewForm reviewForm, @PathVariable("id") Long id, Principal principal) {
        Review review = this.reviewService.getReview(id);
        if(!review.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        reviewForm.setTitle(review.getTitle());
        reviewForm.setContentStory(review.getContentStory());
        reviewForm.setContent(review.getContent());
        return "review/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String reviewModify(@Valid ReviewForm reviewForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "review/create";
        }
        Review review = this.reviewService.getReview(id);
        if (!review.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.reviewService.modify(review, reviewForm.getTitle(),reviewForm.getContentStory(), reviewForm.getContent());
        return String.format("redirect:/review/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String reviewDelete(Principal principal, @PathVariable("id") Long id) {
        Review review = this.reviewService.getReview(id);
        if (!review.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.reviewService.delete(review);
        return "redirect:/review/sub";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String reviewVote(Principal principal, @PathVariable("id") Long id) {
        Review review = this.reviewService.getReview(id);
        SiteUser siteUser = this.userService.findByUsername(principal.getName());
        this.reviewService.vote(review, siteUser);
        return String.format("redirect:/review/detail/%s", id);
    }


    @GetMapping("/index")
    public String moviePage() {
        return "sub/sub_index";
    }
}
