package com.back2261.communityservice.application.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.back2261.communityservice.domain.service.DefaultCommunityService;
import com.back2261.communityservice.interfaces.dto.*;
import com.back2261.communityservice.interfaces.request.CommunityRequest;
import com.back2261.communityservice.interfaces.request.CreateCommentRequest;
import com.back2261.communityservice.interfaces.request.CreateCommunityRequest;
import com.back2261.communityservice.interfaces.request.PostRequest;
import com.back2261.communityservice.interfaces.response.CommentsResponse;
import com.back2261.communityservice.interfaces.response.CommunityResponse;
import com.back2261.communityservice.interfaces.response.PostResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.GameBuddyDevs.backendlibrary.base.BaseBody;
import io.github.GameBuddyDevs.backendlibrary.base.Status;
import io.github.GameBuddyDevs.backendlibrary.enums.TransactionCode;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageBody;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import io.github.GameBuddyDevs.backendlibrary.service.JwtService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(
        value = CommunityController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class CommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DefaultCommunityService defaultCommunityService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private DefaultMessageResponse defaultMessageResponse;
    private CommunityRequest communityRequest;

    @BeforeEach
    void setUp() {
        token = "3745290384765934782659238q475";
        defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody defaultMessageBody = new DefaultMessageBody("test");
        defaultMessageResponse.setBody(new BaseBody<>(defaultMessageBody));
        communityRequest = new CommunityRequest();
        communityRequest.setCommunityId("test");
    }

    @Test
    void testGetCommunities_whenRequested_shouldReturnListOfCommunities() throws Exception {
        CommunityResponse communityResponse = new CommunityResponse();
        CommunityResponseBody body = new CommunityResponseBody();
        List<CommunityDto> communities = new ArrayList<>();
        CommunityDto communityDto = new CommunityDto();
        communityDto.setCommunityId("test");
        communityDto.setCommunityAvatar("test");
        communityDto.setName("test");
        communityDto.setWallpaper("test");
        communityDto.setDescription("test");
        communityDto.setCreatedDate(new Date());
        communities.add(new CommunityDto());
        communities.add(communityDto);
        body.setCommunities(communities);
        communityResponse.setBody(new BaseBody<>(body));
        communityResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultCommunityService.getCommunities()).thenReturn(communityResponse);

        var request = MockMvcRequestBuilders.get("/community/get/communities").contentType("application/json");
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        CommunityResponse communityResponse1 = objectMapper.readValue(responseJson, CommunityResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(2, communityResponse1.getBody().getData().getCommunities().size());
    }

    @Test
    void testGetCommunitiesPosts_whenValidCommunityIdProvided_shouldReturnCommunityPosts() throws Exception {
        PostResponse postResponse = new PostResponse();
        PostResponseBody body = new PostResponseBody();
        List<PostDto> posts = new ArrayList<>();
        PostDto postDto = new PostDto();
        postDto.setPostId("test");
        postDto.setAvatar("test");
        postDto.setUsername("test");
        postDto.setCommentCount(0);
        postDto.setBody("test");
        postDto.setPicture("test");
        postDto.setUpdatedDate(new Date());
        postDto.setTitle("test");
        postDto.setLikeCount(0);
        posts.add(new PostDto());
        posts.add(postDto);
        body.setPosts(posts);
        postResponse.setBody(new BaseBody<>(body));
        postResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultCommunityService.getCommunitiesPosts(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(postResponse);

        var request = MockMvcRequestBuilders.get("/community/get/communities/posts/test")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        PostResponse postResponse1 = objectMapper.readValue(responseJson, PostResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(2, postResponse1.getBody().getData().getPosts().size());
    }

    @Test
    void testCreatePost_whenValidUserAndCommunityProvided_shouldReturnSuccessMessage() throws Exception {
        PostRequest postRequest = new PostRequest();
        postRequest.setPicture("test");
        postRequest.setBody("test");
        postRequest.setTitle("test");
        postRequest.setCommunityId("test");

        Mockito.when(defaultCommunityService.createPost(Mockito.anyString(), Mockito.any(PostRequest.class)))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/community/create/post")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(postRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testCreateCommunity_whenValidUserProvided_shouldReturnSuccessMessage() throws Exception {
        CreateCommunityRequest communityRequest = new CreateCommunityRequest();
        communityRequest.setName("test");
        communityRequest.setDescription("test");
        communityRequest.setWallpaper("test");
        communityRequest.setAvatar("test");

        Mockito.when(defaultCommunityService.createCommunity(
                        Mockito.anyString(), Mockito.any(CreateCommunityRequest.class)))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/community/create")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(communityRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testCreateComment_whenValidUserAndPostProvided_shouldReturnSuccessMessage() throws Exception {
        CreateCommentRequest commentRequest = new CreateCommentRequest();
        commentRequest.setMessage("test");
        commentRequest.setPostId("test");

        Mockito.when(defaultCommunityService.createComment(
                        Mockito.anyString(), Mockito.any(CreateCommentRequest.class)))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/community/create/comment")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(commentRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testDeleteCommunity_whenValidUserAndCommunityProvided_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(defaultCommunityService.deleteCommunity(Mockito.anyString(), Mockito.any(CommunityRequest.class)))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.delete("/community/delete")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(communityRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testDeletePost_whenValidUserAndPostProvided_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(defaultCommunityService.deletePost(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.delete("/community/delete/post/test")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testDeleteComment_whenValidUserAndCommentProvided_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(defaultCommunityService.deleteComment(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.delete("/community/delete/comment/test")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testJoinCommunity_whenValidUserAndCommunityProvided_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(defaultCommunityService.joinCommunity(Mockito.anyString(), Mockito.any(CommunityRequest.class)))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/community/join")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(communityRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testLeaveCommunity_whenValidUserAndCommunityProvided_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(defaultCommunityService.leaveCommunity(Mockito.anyString(), Mockito.any(CommunityRequest.class)))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/community/leave")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(communityRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testGetPostComments_whenValidPostIdProvided_shouldReturnListOfPostComments() throws Exception {
        CommentsResponse commentsResponse = new CommentsResponse();
        CommentsResponseBody body = new CommentsResponseBody();
        List<CommentDto> comments = new ArrayList<>();
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentId("test");
        commentDto.setAvatar("test");
        commentDto.setUsername("test");
        commentDto.setMessage("test");
        commentDto.setUpdatedDate(new Date());
        commentDto.setLikeCount(1);
        comments.add(commentDto);
        comments.add(new CommentDto());
        body.setComments(comments);
        commentsResponse.setBody(new BaseBody<>(body));
        commentsResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        Mockito.when(defaultCommunityService.getPostComments(Mockito.anyString()))
                .thenReturn(commentsResponse);

        var request = MockMvcRequestBuilders.get("/community/get/post/comments/test")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        CommentsResponse responseObj = objectMapper.readValue(responseJson, CommentsResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(2, responseObj.getBody().getData().getComments().size());
    }

    @Test
    void testLikePost_whenValidUserAndPostProvided_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(defaultCommunityService.likePost(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/community/like/post/test")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testLikeComment_whenValidUserAndCommentProvided_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(defaultCommunityService.likeComment(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/community/like/comment/test")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }
}
