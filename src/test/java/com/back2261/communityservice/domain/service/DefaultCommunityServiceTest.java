package com.back2261.communityservice.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import com.back2261.communityservice.infrastructure.entity.Comment;
import com.back2261.communityservice.infrastructure.entity.Community;
import com.back2261.communityservice.infrastructure.entity.Gamer;
import com.back2261.communityservice.infrastructure.entity.Post;
import com.back2261.communityservice.infrastructure.repository.CommentRepository;
import com.back2261.communityservice.infrastructure.repository.CommunityRepository;
import com.back2261.communityservice.infrastructure.repository.GamerRepository;
import com.back2261.communityservice.infrastructure.repository.PostRepository;
import com.back2261.communityservice.interfaces.request.CommunityRequest;
import com.back2261.communityservice.interfaces.request.CreateCommentRequest;
import com.back2261.communityservice.interfaces.request.CreateCommunityRequest;
import com.back2261.communityservice.interfaces.request.PostRequest;
import com.back2261.communityservice.interfaces.response.CommentsResponse;
import com.back2261.communityservice.interfaces.response.CommunityResponse;
import com.back2261.communityservice.interfaces.response.PostResponse;
import io.github.GameBuddyDevs.backendlibrary.exception.BusinessException;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import io.github.GameBuddyDevs.backendlibrary.service.JwtService;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultCommunityServiceTest {

    @InjectMocks
    private DefaultCommunityService defaultCommunityService;

    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private GamerRepository gamerRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private JwtService jwtService;

    private String token;
    private String id;

    @BeforeEach
    void setUp() {
        token = "test";
        id = UUID.randomUUID().toString();
    }

    @Test
    void testGetCommunities_whenCalled_ReturnListOfCommunities() {
        List<Community> communities = new ArrayList<>();
        communities.add(getCommunity());
        communities.add(getCommunity());

        Mockito.when(communityRepository.findAll()).thenReturn(communities);

        CommunityResponse result = defaultCommunityService.getCommunities();
        assertEquals(2, result.getBody().getData().getCommunities().size());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testGetCommunitiesPosts_whenInvalidCommunityIdProvided_ReturnErrorCode131() {
        CommunityRequest communityRequest = new CommunityRequest();
        communityRequest.setCommunityId("6bd0b158-be7f-45cc-84a2-f0b320e576ed");

        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultCommunityService.getCommunitiesPosts(communityRequest));
        assertEquals(131, exception.getTransactionCode().getId());
    }

    @Test
    void testGetCommunitiesPosts_whenOwnerOfThePostIsEmpty_ReturnErrorCode103() {
        CommunityRequest communityRequest = new CommunityRequest();
        communityRequest.setCommunityId("6bd0b158-be7f-45cc-84a2-f0b320e576ed");
        Community community = getCommunity();
        community.getPosts().add(getPost());
        community.getPosts().add(getPost());

        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(community));
        Mockito.when(gamerRepository.findById(Mockito.any(String.class))).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultCommunityService.getCommunitiesPosts(communityRequest));
        assertEquals(103, exception.getTransactionCode().getId());
    }

    @Test
    void testGetCommunitiesPosts_whenValidCommunityIdProvided_ReturnListOfPostsInTheCommunity() {
        CommunityRequest communityRequest = new CommunityRequest();
        communityRequest.setCommunityId("6bd0b158-be7f-45cc-84a2-f0b320e576ed");
        Community community = getCommunity();
        community.getPosts().add(getPost());
        community.getPosts().add(getPost());
        Gamer gamer = getGamer();

        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(community));
        Mockito.when(gamerRepository.findById(Mockito.any(String.class))).thenReturn(Optional.of(gamer));

        PostResponse result = defaultCommunityService.getCommunitiesPosts(communityRequest);
        assertEquals(2, result.getBody().getData().getPosts().size());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testCreatePost_whenProvidedTokenInvalid_ReturnErrorCode103() {
        PostRequest postRequest = new PostRequest();
        postRequest.setCommunityId(UUID.randomUUID().toString());
        postRequest.setBody("test");
        postRequest.setTitle("test");
        postRequest.setPicture("test");

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultCommunityService.createPost(token, postRequest));
        assertEquals(103, exception.getTransactionCode().getId());
    }

    @Test
    void testCreatePost_whenCommunityNotFound_ReturnErrorCode131() {
        Gamer gamer = getGamer();
        PostRequest postRequest = new PostRequest();
        postRequest.setCommunityId(UUID.randomUUID().toString());
        postRequest.setBody("test");
        postRequest.setTitle("test");
        postRequest.setPicture("test");

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultCommunityService.createPost(token, postRequest));
        assertEquals(131, exception.getTransactionCode().getId());
    }

    @Test
    void testCreatePost_whenUserNotMemberOfProvidedCommunity_ReturnErrorCode132() {
        Gamer gamer = getGamer();
        PostRequest postRequest = new PostRequest();
        postRequest.setCommunityId(UUID.randomUUID().toString());
        postRequest.setBody("test");
        postRequest.setTitle("test");
        postRequest.setPicture("test");
        Community community = getCommunity();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(community));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultCommunityService.createPost(token, postRequest));
        assertEquals(132, exception.getTransactionCode().getId());
    }

    @Test
    void testCreatePost_whenValidUserWithCommunityIdProvided_ReturnSuccess() {
        Gamer gamer = getGamer();
        PostRequest postRequest = new PostRequest();
        postRequest.setCommunityId(UUID.randomUUID().toString());
        postRequest.setBody("test");
        postRequest.setTitle("test");
        postRequest.setPicture("test");
        Community community = getCommunity();
        community.getMembers().add(gamer);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(community));

        DefaultMessageResponse result = defaultCommunityService.createPost(token, postRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testCreateCommunity_whenUserValid_ReturnSuccess() {
        CreateCommunityRequest createCommunityRequest = new CreateCommunityRequest();
        createCommunityRequest.setAvatar("test");
        createCommunityRequest.setName("test");
        createCommunityRequest.setDescription("test");
        createCommunityRequest.setWallpaper("test");
        Gamer gamer = getGamer();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));

        DefaultMessageResponse result = defaultCommunityService.createCommunity(token, createCommunityRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testCreateComment_whenPostNotFound_ReturnErrorCode133() {
        CreateCommentRequest createCommentRequest = new CreateCommentRequest();
        createCommentRequest.setMessage("test");
        createCommentRequest.setPostId(UUID.randomUUID().toString());
        Gamer gamer = getGamer();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(postRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultCommunityService.createComment(token, createCommentRequest));
        assertEquals(133, exception.getTransactionCode().getId());
    }

    @Test
    void testCreateComment_whenUserAndPostValid_ReturnSuccess() {
        CreateCommentRequest createCommentRequest = new CreateCommentRequest();
        createCommentRequest.setMessage("test");
        createCommentRequest.setPostId(UUID.randomUUID().toString());
        Gamer gamer = getGamer();
        Post post = getPost();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(postRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(post));

        DefaultMessageResponse result = defaultCommunityService.createComment(token, createCommentRequest);
        assertEquals("100", result.getStatus().getCode());
        assertEquals(1, post.getComments().size());
    }

    @Test
    void testDeleteCommunity_whenCommunityNotFound_ReturnErrorCode131() {
        CommunityRequest communityRequest = new CommunityRequest();
        communityRequest.setCommunityId(UUID.randomUUID().toString());
        Gamer gamer = getGamer();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultCommunityService.deleteCommunity(token, communityRequest));
        assertEquals(131, exception.getTransactionCode().getId());
    }

    @Test
    void testDeleteCommunity_whenUserNotTheOwner_ReturnErrorCode134() {
        CommunityRequest communityRequest = new CommunityRequest();
        communityRequest.setCommunityId(UUID.randomUUID().toString());
        Gamer gamer = getGamer();
        Community community = getCommunity();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(community));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultCommunityService.deleteCommunity(token, communityRequest));
        assertEquals(134, exception.getTransactionCode().getId());
    }

    @Test
    void testDeleteCommunity_whenValid_ReturnSuccess() {
        CommunityRequest communityRequest = new CommunityRequest();
        communityRequest.setCommunityId(UUID.randomUUID().toString());
        Gamer gamer = getGamer();
        Community community = getCommunity();
        community.setOwner(gamer);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(community));

        DefaultMessageResponse result = defaultCommunityService.deleteCommunity(token, communityRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testDeletePost_whenPostNotFound_ReturnErrorCode133() {
        Gamer gamer = getGamer();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(postRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultCommunityService.deletePost(token, id));
        assertEquals(133, exception.getTransactionCode().getId());
    }

    @Test
    void testDeletePost_whenUserNotOwner_ReturnErrorCode134() {
        Gamer gamer = getGamer();
        gamer.setUserId("test2");
        Post post = getPost();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(postRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(post));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultCommunityService.deletePost(token, id));
        assertEquals(134, exception.getTransactionCode().getId());
    }

    @Test
    void testDeletePost_whenValid_ReturnSuccess() {
        Gamer gamer = getGamer();
        Post post = getPost();
        post.setOwner(gamer.getUserId());

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(postRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(post));

        DefaultMessageResponse result = defaultCommunityService.deletePost(token, id);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testDeleteComment_whenCommentNotFound_ReturnErrorCode135() {
        Gamer gamer = getGamer();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(commentRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultCommunityService.deleteComment(token, id));
        assertEquals(135, exception.getTransactionCode().getId());
    }

    @Test
    void testDeleteComment_whenUserNotCommentOwner_ReturnErrorCode134() {
        Comment comment = getComment();
        Gamer gamer = getGamer();
        gamer.setUserId("test2");

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(commentRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(comment));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultCommunityService.deleteComment(token, id));
        assertEquals(134, exception.getTransactionCode().getId());
    }

    @Test
    void testDeleteComment_whenValid_ReturnSuccess() {
        Comment comment = getComment();
        Gamer gamer = getGamer();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(commentRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(comment));

        DefaultMessageResponse result = defaultCommunityService.deleteComment(token, id);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testJoinCommunity_whenCommunityNotFound_ReturnErrorCode131() {
        CommunityRequest communityRequest = new CommunityRequest();
        communityRequest.setCommunityId(id);
        Gamer gamer = getGamer();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultCommunityService.joinCommunity(token, communityRequest));
        assertEquals(131, exception.getTransactionCode().getId());
    }

    @Test
    void testJoinCommunity_whenUserAlreadyMember_ReturnErrorCode136() {
        CommunityRequest communityRequest = new CommunityRequest();
        communityRequest.setCommunityId(id);
        Gamer gamer = getGamer();
        Community community = getCommunity();
        community.getMembers().add(gamer);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(community));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultCommunityService.joinCommunity(token, communityRequest));
        assertEquals(136, exception.getTransactionCode().getId());
    }

    @Test
    void testJoinCommunity_whenValid_ReturnSuccess() {
        CommunityRequest communityRequest = new CommunityRequest();
        communityRequest.setCommunityId(id);
        Gamer gamer = getGamer();
        Community community = getCommunity();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(community));

        DefaultMessageResponse result = defaultCommunityService.joinCommunity(token, communityRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testLeaveCommunity_whenCommunityNotFound_ReturnErrorCode131() {
        CommunityRequest communityRequest = new CommunityRequest();
        communityRequest.setCommunityId(id);
        Gamer gamer = getGamer();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultCommunityService.leaveCommunity(token, communityRequest));
        assertEquals(131, exception.getTransactionCode().getId());
    }

    @Test
    void testLeaveCommunity_whenUserNotMember_ReturnErrorCode132() {
        CommunityRequest communityRequest = new CommunityRequest();
        communityRequest.setCommunityId(id);
        Gamer gamer = getGamer();
        Community community = getCommunity();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(community));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultCommunityService.leaveCommunity(token, communityRequest));
        assertEquals(132, exception.getTransactionCode().getId());
    }

    @Test
    void testLeaveCommunity_whenUserIsOwnerOfCommunity_ReturnErrorCode138() {
        CommunityRequest communityRequest = new CommunityRequest();
        communityRequest.setCommunityId(id);
        Gamer gamer = getGamer();
        Community community = getCommunity();
        community.setOwner(gamer);
        community.getMembers().add(gamer);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(community));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultCommunityService.leaveCommunity(token, communityRequest));
        assertEquals(138, exception.getTransactionCode().getId());
    }

    @Test
    void testLeaveCommunity_whenValid_ReturnSuccess() {
        CommunityRequest communityRequest = new CommunityRequest();
        communityRequest.setCommunityId(id);
        Gamer gamer = getGamer();
        Community community = getCommunity();
        community.getMembers().add(gamer);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(communityRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(community));

        DefaultMessageResponse result = defaultCommunityService.leaveCommunity(token, communityRequest);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testGetPostComments_whenInvalidPostIdProvided_ReturnErrorCode133() {
        Mockito.when(postRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultCommunityService.getPostComments(id));
        assertEquals(133, exception.getTransactionCode().getId());
    }

    @Test
    void testGetPostComments_whenCommentOwnerNotFound_ReturnErrorCode103() {
        Post post = getPost();
        post.getComments().add(getComment());
        post.getComments().add(getComment());

        Mockito.when(postRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(post));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultCommunityService.getPostComments(id));
        assertEquals(103, exception.getTransactionCode().getId());
    }

    @Test
    void testGetPostComments_whenValidPostIdProvided_ReturnListOfPostComments() {
        Post post = getPost();
        post.getComments().add(getComment());
        post.getComments().add(getComment());
        Gamer gamer = getGamer();

        Mockito.when(postRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(post));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(gamer));

        CommentsResponse result = defaultCommunityService.getPostComments(id);
        assertEquals(2, result.getBody().getData().getComments().size());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testLikePost_whenPostNotFound_ReturnErrorCode133() {
        Gamer gamer = getGamer();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(postRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultCommunityService.likePost(token, id));
        assertEquals(133, exception.getTransactionCode().getId());
    }

    @Test
    void testLikePost_whenUserAlreadyLiked_ReturnErrorCode139() {
        Gamer gamer = getGamer();
        Post post = getPost();
        post.getLikes().add(gamer);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(postRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(post));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultCommunityService.likePost(token, id));
        assertEquals(139, exception.getTransactionCode().getId());
    }

    @Test
    void testLikePost_whenValid_ReturnSuccess() {
        Gamer gamer = getGamer();
        Post post = getPost();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(postRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(post));

        DefaultMessageResponse result = defaultCommunityService.likePost(token, id);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testLikeComment_whenCommentNotFound_ReturnErrorCode135() {
        Gamer gamer = getGamer();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(commentRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultCommunityService.likeComment(token, id));
        assertEquals(135, exception.getTransactionCode().getId());
    }

    @Test
    void testLikeComment_whenUserAlreadyLiked_ReturnErrorCode139() {
        Gamer gamer = getGamer();
        Comment comment = getComment();
        comment.getLikes().add(gamer);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(commentRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(comment));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultCommunityService.likeComment(token, id));
        assertEquals(139, exception.getTransactionCode().getId());
    }

    @Test
    void testLikeComment_whenValid_ReturnSuccess() {
        Gamer gamer = getGamer();
        Comment comment = getComment();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn(gamer.getEmail());
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(commentRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(comment));

        DefaultMessageResponse result = defaultCommunityService.likeComment(token, id);
        assertEquals("100", result.getStatus().getCode());
    }

    private Community getCommunity() {
        Community community = new Community();
        community.setCommunityId(UUID.randomUUID());
        community.setCommunityAvatar("test");
        community.setName("test");
        community.setPosts(new HashSet<>());
        community.setOwner(getGamer());
        community.setMembers(new HashSet<>());
        community.setWallpaper("test");
        community.setDescription("test");
        community.setCreatedDate(new Date());
        return community;
    }

    private Post getPost() {
        Post post = new Post();
        post.setPostId(UUID.randomUUID());
        post.setBody("test");
        post.setComments(new HashSet<>());
        post.setCreatedDate(new Date());
        post.setLikes(new HashSet<>());
        post.setLikeCount(0);
        post.setTitle("test");
        post.setOwner("test");
        post.setPicture("test");
        post.setUpdatedDate(new Date());
        return post;
    }

    private Comment getComment() {
        Comment comment = new Comment();
        comment.setCommentId(UUID.randomUUID());
        comment.setMessage("test");
        comment.setCreatedDate(new Date());
        comment.setLikes(new HashSet<>());
        comment.setLikeCount(0);
        comment.setOwner("test");
        comment.setUpdatedDate(new Date());
        return comment;
    }

    private Gamer getGamer() {
        Gamer gamer = new Gamer();
        gamer.setUserId("test");
        gamer.setGamerUsername("test");
        gamer.setEmail("test");
        gamer.setAge(15);
        gamer.setCountry("test");
        gamer.setAvatar("71927b70-8a51-4844-a306-00313fec4f09");
        gamer.setLastModifiedDate(new Date());
        gamer.setPwd("test");
        gamer.setGender("E");
        gamer.setCoin(0);
        gamer.setIsBlocked(false);
        gamer.setOwnedCommunities(new HashSet<>());
        return gamer;
    }
}
