package com.market.allForOneReview.domain.notice.service;

import com.market.allForOneReview.domain.notice.entity.Board;
import com.market.allForOneReview.domain.notice.entity.NoticePost;
import com.market.allForOneReview.domain.notice.Repository.BoardRepository;
import com.market.allForOneReview.domain.notice.Repository.NoticePostRepository;
import com.market.allForOneReview.domain.user.entity.SiteUser;
import com.market.allForOneReview.global.exception.DataNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticePostService {
    private final NoticePostRepository noticePostRepository;
    private final BoardRepository boardRepository;

    //    페이징
    public Page<NoticePost> getNoticesByBoardTypeAndPage(String boardType, int page) {
        Pageable pageable = PageRequest.of(page, 10); // 10개씩 페이징 처리
        return noticePostRepository.findByBoard_BoardType(boardType, pageable);
    }

    @Transactional
    public NoticePost getNotice(Long id, HttpServletRequest request, HttpServletResponse response) {
        Optional<NoticePost> notice = this.noticePostRepository.findById(id);
        if (notice.isPresent()) {
            Cookie[] cookies = request.getCookies();
            boolean hasViewed = false;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("Viewed_Notice")) {
                        String[] viewedNotices = cookie.getValue().split("\\|");
                        for (String viewedId : viewedNotices) {
                            if (viewedId.equals(String.valueOf(id))) {
                                hasViewed = true; // 이미 조회한 공지사항
                                break;
                            }
                        }
                        if (hasViewed) break; // 이미 조회한 경우 반복 종료
                    }
                }
            }


            // 조회하지 않은 경우에만 조회수 증가
            if (!hasViewed) {
                notice.get().incrementViews(); // 조회수 증가
                this.noticePostRepository.save(notice.get()); // DB에 변경 사항 저장
                System.out.println("Updated views: " + notice.get().getViews()); // 업데이트된 조회수 로그

                // 쿠키 업데이트
                StringBuilder newCookieValue = new StringBuilder();
                try {
                    if (cookies != null && cookies.length > 0) {
                        for (Cookie cookie : cookies) {
                            if (cookie.getName().equals("Viewed_Notice")) {
                                String[] viewedNotices = cookie.getValue().split("\\|");
                                for (String viewedId : viewedNotices) {
                                    // 중복되지 않는 ID만 추가
                                    if (!viewedId.equals(String.valueOf(id))) {
                                        newCookieValue.append(viewedId).append("|");
                                    }
                                }
                            }
                        }
                    }
                    // 새 ID 추가
                    newCookieValue.append(URLEncoder.encode(String.valueOf(id), StandardCharsets.UTF_8.toString())).append("|");

                    // 마지막 구분자 제거
                    if (newCookieValue.length() > 0 && newCookieValue.charAt(newCookieValue.length() - 1) == '|') {
                        newCookieValue.setLength(newCookieValue.length() - 1);
                    }

                    Cookie viewedCookie = new Cookie("Viewed_Notice", newCookieValue.toString());
                    viewedCookie.setPath("/");
                    viewedCookie.setMaxAge(60 * 60 * 24); // 1일 동안 유지
                    response.addCookie(viewedCookie);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace(); // 예외 발생 시 스택 트레이스 출력
                }
            } else {
                System.out.println("Notice ID " + id + " has already been viewed."); // 이미 조회한 공지사항 로그
            }
            return notice.get();

        } else {
            throw new DataNotFoundException("notice not found");
        }
    }

    public void create(String title, String content, String boardType, SiteUser siteUser){

        Optional<Board> board = this.boardRepository.findByBoardType(boardType);
        NoticePost noticePost = new NoticePost();
        noticePost.setTitle(title);
        noticePost.setContent(content);
        noticePost.setCreateDate(LocalDateTime.now());
        noticePost.setBoard(board.get());
        noticePost.setViews(0);
        noticePost.setAuthor(siteUser);
        this.noticePostRepository.save(noticePost);
    }
    public void modify(NoticePost notice, String title, String content){
        notice.setTitle(title);
        notice.setContent(content);
        notice.setModifiedDate(LocalDateTime.now());
        this.noticePostRepository.save(notice);
    }
    public void delete(NoticePost notice){
        this.noticePostRepository.delete(notice);
    }

    public Page<NoticePost> searchNoticePosts(String searchTerm, String boardType, String searchType, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        switch (searchType) {
            case "title":
                return noticePostRepository.findByTitleContainingAndBoard_BoardType(searchTerm, boardType, pageRequest);
            case "content":
                return noticePostRepository.findByContentContainingAndBoard_BoardType(searchTerm, boardType, pageRequest);
            default:
                // 전체 검색: 제목과 내용에서 모두 검색
                Page<NoticePost> titlePage = noticePostRepository.findByTitleContainingAndBoard_BoardType(searchTerm, boardType, pageRequest);
                Page<NoticePost> contentPage = noticePostRepository.findByContentContainingAndBoard_BoardType(searchTerm, boardType, pageRequest);

                // 결과 합치기
                List<NoticePost> allResults = new ArrayList<>();
                allResults.addAll(titlePage.getContent());
                allResults.addAll(contentPage.getContent());

                // 중복 제거
                List<NoticePost> uniqueResults = allResults.stream().distinct().collect(Collectors.toList());

                // Page 객체로 반환
                return new PageImpl<>(uniqueResults, pageRequest, uniqueResults.size());
        }
    }
}
