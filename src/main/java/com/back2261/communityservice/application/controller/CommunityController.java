package com.back2261.communityservice.application.controller;

import com.back2261.communityservice.domain.service.CommunityService;
import com.back2261.communityservice.interfaces.request.CommunityRequest;
import com.back2261.communityservice.interfaces.request.CreateCommentRequest;
import com.back2261.communityservice.interfaces.request.CreateCommunityRequest;
import com.back2261.communityservice.interfaces.request.PostRequest;
import com.back2261.communityservice.interfaces.response.*;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_MESSAGE = "Authorization field cannot be empty";

    @GetMapping("/get/communities")
    public ResponseEntity<CommunityResponse> getCommunities(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token) {
        return new ResponseEntity<>(communityService.getCommunities(token.substring(7)), HttpStatus.OK);
    }

    @GetMapping("/get/members/{communityId}")
    public ResponseEntity<MemberResponse> getMembers(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable("communityId") String communityId) {
        return new ResponseEntity<>(communityService.getMembers(communityId), HttpStatus.OK);
    }

    @GetMapping("/get/posts/{communityId}")
    public ResponseEntity<PostResponse> getCommunitiesPosts(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable("communityId") String communityId) {
        return new ResponseEntity<>(
                communityService.getCommunitiesPosts(token.substring(7), communityId), HttpStatus.OK);
    }

    @GetMapping("/get/post/likes/{postId}")
    public ResponseEntity<MemberResponse> getPostLikes(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable("postId") String postId) {
        return new ResponseEntity<>(communityService.getPostLikes(postId), HttpStatus.OK);
    }

    @GetMapping("/get/comment/likes/{commentId}")
    public ResponseEntity<MemberResponse> getCommentLikes(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable("commentId") String commentId) {
        return new ResponseEntity<>(communityService.getCommentLikes(commentId), HttpStatus.OK);
    }

    @GetMapping("/get/joined/posts")
    public ResponseEntity<PostResponse> getJoinedCommunitiesPosts(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token) {
        return new ResponseEntity<>(communityService.getJoinedCommunitiesPosts(token.substring(7)), HttpStatus.OK);
    }

    @PostMapping("/create/post")
    public ResponseEntity<DefaultMessageResponse> createPost(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody PostRequest postRequest) {
        return new ResponseEntity<>(communityService.createPost(token.substring(7), postRequest), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<DefaultMessageResponse> createCommunity(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody CreateCommunityRequest createCommunityRequest) {
        return new ResponseEntity<>(
                communityService.createCommunity(token.substring(7), createCommunityRequest), HttpStatus.OK);
    }

    @PostMapping("/create/comment")
    public ResponseEntity<DefaultMessageResponse> createComment(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody CreateCommentRequest commentRequest) {
        return new ResponseEntity<>(communityService.createComment(token.substring(7), commentRequest), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<DefaultMessageResponse> deleteCommunity(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody CommunityRequest communityRequest) {
        return new ResponseEntity<>(
                communityService.deleteCommunity(token.substring(7), communityRequest), HttpStatus.OK);
    }

    @DeleteMapping("/delete/post/{postId}")
    public ResponseEntity<DefaultMessageResponse> deletePost(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable String postId) {
        return new ResponseEntity<>(communityService.deletePost(token.substring(7), postId), HttpStatus.OK);
    }

    @DeleteMapping("/delete/comment/{commentId}")
    public ResponseEntity<DefaultMessageResponse> deleteComment(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable String commentId) {
        return new ResponseEntity<>(communityService.deleteComment(token.substring(7), commentId), HttpStatus.OK);
    }

    @PostMapping("/join")
    public ResponseEntity<DefaultMessageResponse> joinCommunity(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody CommunityRequest communityRequest) {
        return new ResponseEntity<>(
                communityService.joinCommunity(token.substring(7), communityRequest), HttpStatus.OK);
    }

    @PostMapping("/leave")
    public ResponseEntity<DefaultMessageResponse> leaveCommunity(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody CommunityRequest communityRequest) {
        return new ResponseEntity<>(
                communityService.leaveCommunity(token.substring(7), communityRequest), HttpStatus.OK);
    }

    @GetMapping("/get/post/comments/{postId}")
    public ResponseEntity<CommentsResponse> getPostComments(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable String postId) {
        return new ResponseEntity<>(communityService.getPostComments(postId), HttpStatus.OK);
    }

    @PostMapping("/like/post/{postId}")
    public ResponseEntity<DefaultMessageResponse> likePost(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable String postId) {
        return new ResponseEntity<>(communityService.likePost(token.substring(7), postId), HttpStatus.OK);
    }

    @PostMapping("/like/comment/{commentId}")
    public ResponseEntity<DefaultMessageResponse> likeComment(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable String commentId) {
        return new ResponseEntity<>(communityService.likeComment(token.substring(7), commentId), HttpStatus.OK);
    }
}
