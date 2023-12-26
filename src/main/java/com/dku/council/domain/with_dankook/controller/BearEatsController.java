package com.dku.council.domain.with_dankook.controller;

import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.with_dankook.model.dto.list.SummarizedBearEatsDto;
import com.dku.council.domain.with_dankook.model.dto.request.RequestCreateBearEatsDto;
import com.dku.council.domain.with_dankook.model.dto.response.ResponseSingleBearEatsDto;
import com.dku.council.domain.with_dankook.service.BearEatsService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.UserAuth;
import com.dku.council.global.model.dto.ResponseIdDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "BearEats", description = "단국 BearEats 게시판")
@RestController
@RequestMapping("with-dankook/bear-eats")
@RequiredArgsConstructor
public class BearEatsController {

    private final BearEatsService bearEatsService;

    /**
     * BearEats 게시글 목록 조회
     *
     * @param bodySize    게시글 본문 길이. (글자 단위) 지정하지 않으면 50 글자.
     * @param pageable    페이징 size, sort, page
     * @return            페이징된 BearEats 게시판 목록
     */
    @GetMapping
    public ResponsePage<SummarizedBearEatsDto> list(@RequestParam(defaultValue = "50") int bodySize,
                                                    @ParameterObject Pageable pageable) {
        Page<SummarizedBearEatsDto> list = bearEatsService.list(pageable, bodySize);
        return new ResponsePage<>(list);
    }

    /**
     * 내가 쓴 BearEats 게시글 목록 조회
     *
     * @param pageable    페이징 size, sort, page
     * @return            페이징된 내가 쓴 BearEats 게시판 목록
     */
    @GetMapping("/my")
    @UserAuth
    public ResponsePage<SummarizedBearEatsDto> listMyPots(AppAuthentication auth,
                                                          @ParameterObject Pageable pageable) {
        Page<SummarizedBearEatsDto> list = bearEatsService.listMyPosts(auth.getUserId(), pageable);
        return new ResponsePage<>(list);
    }

    /**
     * BearEats 게시글 상세 조회
     *
     * @param id    게시글 id
     */
    @GetMapping("/{id}")
    @UserAuth
    public ResponseSingleBearEatsDto findOne(AppAuthentication auth,
                                             @PathVariable Long id) {
        return bearEatsService.findOne(id, auth.getUserId(), auth.getUserRole());
    }

    /**
     * BearEats 게시글 작성
     */
    @PostMapping
    @UserAuth
    public ResponseIdDto create(AppAuthentication auth,
                                @Valid @RequestBody RequestCreateBearEatsDto dto) {
        Long id = bearEatsService.create(auth.getUserId(), dto);
        return new ResponseIdDto(id);
    }

    /**
     * BearEats 게시글 신청
     *
     * @param id    게시글 id
     */
    @PostMapping("/{id}/enter")
    @UserAuth
    public void enter(AppAuthentication auth, @PathVariable @Valid Long id) {
        bearEatsService.enter(id, auth.getUserId(), auth.getUserRole());
    }

    /**
     * BearEats 게시글 삭제
     *
     * @param id    게시글 id
     */
    @DeleteMapping("/{id}")
    @UserAuth
    public void delete(AppAuthentication auth,
                       @PathVariable Long id) {
        bearEatsService.delete(id, auth.getUserId(), auth.isAdmin());
    }
}
