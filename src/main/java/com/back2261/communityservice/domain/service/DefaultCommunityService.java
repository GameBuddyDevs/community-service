package com.back2261.communityservice.domain.service;

import com.back2261.communityservice.infrastructure.entity.Comment;
import com.back2261.communityservice.infrastructure.entity.Community;
import com.back2261.communityservice.infrastructure.entity.Gamer;
import com.back2261.communityservice.infrastructure.entity.Post;
import com.back2261.communityservice.infrastructure.repository.CommentRepository;
import com.back2261.communityservice.infrastructure.repository.CommunityRepository;
import com.back2261.communityservice.infrastructure.repository.GamerRepository;
import com.back2261.communityservice.infrastructure.repository.PostRepository;
import com.back2261.communityservice.interfaces.dto.*;
import com.back2261.communityservice.interfaces.request.CommunityRequest;
import com.back2261.communityservice.interfaces.request.CreateCommentRequest;
import com.back2261.communityservice.interfaces.request.CreateCommunityRequest;
import com.back2261.communityservice.interfaces.request.PostRequest;
import com.back2261.communityservice.interfaces.response.CommentsResponse;
import com.back2261.communityservice.interfaces.response.CommunityResponse;
import com.back2261.communityservice.interfaces.response.PostResponse;
import io.github.GameBuddyDevs.backendlibrary.base.BaseBody;
import io.github.GameBuddyDevs.backendlibrary.base.Status;
import io.github.GameBuddyDevs.backendlibrary.enums.TransactionCode;
import io.github.GameBuddyDevs.backendlibrary.exception.BusinessException;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageBody;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import io.github.GameBuddyDevs.backendlibrary.service.JwtService;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultCommunityService implements CommunityService {

    private final CommunityRepository communityRepository;
    private final GamerRepository gamerRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final JwtService jwtService;

    @Override
    public CommunityResponse getCommunities() {
        List<Community> communities = communityRepository.findAll();
        List<CommunityDto> communityDtos = new ArrayList<>();
        communities.forEach(community -> {
            CommunityDto communityDto = new CommunityDto();
            BeanUtils.copyProperties(community, communityDto);
            communityDto.setCommunityId(community.getCommunityId().toString());
            communityDtos.add(communityDto);
        });
        CommunityResponse communityResponse = new CommunityResponse();
        CommunityResponseBody body = new CommunityResponseBody();
        body.setCommunities(communityDtos);
        communityResponse.setBody(new BaseBody<>(body));
        communityResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return communityResponse;
    }

    @Override
    public PostResponse getCommunitiesPosts(CommunityRequest communityRequest) {
        String communityId = communityRequest.getCommunityId();
        Community community = communityRepository
                .findById(UUID.fromString(communityId))
                .orElseThrow(() -> new BusinessException(TransactionCode.COMMUNITY_NOT_FOUND));

        Set<Post> posts = community.getPosts();
        List<PostDto> postDtos = new ArrayList<>();
        posts.forEach(post -> {
            PostDto postDto = new PostDto();
            BeanUtils.copyProperties(post, postDto);

            postDto.setPostId(post.getPostId().toString());
            Gamer gamer = gamerRepository
                    .findById(post.getOwner())
                    .orElseThrow(() -> new BusinessException(TransactionCode.USER_NOT_FOUND));
            postDto.setUsername(gamer.getGamerUsername());
            postDto.setAvatar(gamer.getAvatar());
            postDto.setCommentCount(post.getComments().size());
            postDtos.add(postDto);
        });
        PostResponse postResponse = new PostResponse();
        PostResponseBody body = new PostResponseBody();
        body.setPosts(postDtos);
        postResponse.setBody(new BaseBody<>(body));
        postResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return postResponse;
    }

    @Override
    public DefaultMessageResponse createPost(String token, PostRequest postRequest) {
        Gamer gamer = extractGamer(token);
        String communityId = postRequest.getCommunityId();
        Community community = communityRepository
                .findById(UUID.fromString(communityId))
                .orElseThrow(() -> new BusinessException(TransactionCode.COMMUNITY_NOT_FOUND));
        if (Boolean.FALSE.equals(community.getMembers().contains(gamer))) {
            throw new BusinessException(TransactionCode.NOT_MEMBER);
        }

        Post post = new Post();
        post.setOwner(gamer.getUserId());
        post.setBody(postRequest.getBody());
        post.setPicture(postRequest.getPicture());
        post.setTitle(postRequest.getTitle());
        post.setPostId(UUID.randomUUID());
        post.setComments(new HashSet<>());
        post.setLikes(new HashSet<>());
        community.getPosts().add(post);
        postRepository.save(post);
        communityRepository.save(community);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Post created successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse createCommunity(String token, CreateCommunityRequest createCommunityRequest) {
        Gamer gamer = extractGamer(token);
        Community community = new Community();
        community.setCommunityId(UUID.randomUUID());
        community.setName(createCommunityRequest.getName());
        community.setDescription(createCommunityRequest.getDescription());
        community.setCommunityAvatar(createCommunityRequest.getAvatar());
        community.setPosts(new HashSet<>());
        community.setWallpaper(createCommunityRequest.getWallpaper());
        community.setMembers(new HashSet<>());
        community.getMembers().add(gamer);
        community.setOwner(gamer);
        communityRepository.save(community);
        gamerRepository.save(gamer);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Community created successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse createComment(String token, CreateCommentRequest commentRequest) {
        Gamer gamer = extractGamer(token);
        String postId = commentRequest.getPostId();
        Post post = postRepository
                .findById(UUID.fromString(postId))
                .orElseThrow(() -> new BusinessException(TransactionCode.POST_NOT_FOUND));
        Comment comment = new Comment();
        comment.setCommentId(UUID.randomUUID());
        comment.setMessage(commentRequest.getMessage());
        comment.setOwner(gamer.getUserId());
        comment.setLikes(new HashSet<>());
        post.getComments().add(comment);
        commentRepository.save(comment);
        postRepository.save(post);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Comment created successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse deleteCommunity(String token, CommunityRequest communityRequest) {
        Gamer gamer = extractGamer(token);
        String communityId = communityRequest.getCommunityId();
        Community community = communityRepository
                .findById(UUID.fromString(communityId))
                .orElseThrow(() -> new BusinessException(TransactionCode.COMMUNITY_NOT_FOUND));
        if (Boolean.FALSE.equals(community.getOwner().equals(gamer))) {
            throw new BusinessException(TransactionCode.NOT_OWNER);
        }
        communityRepository.delete(community);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Community deleted successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse deletePost(String token, String postId) {
        Gamer gamer = extractGamer(token);
        Post post = postRepository
                .findById(UUID.fromString(postId))
                .orElseThrow(() -> new BusinessException(TransactionCode.POST_NOT_FOUND));
        if (Boolean.FALSE.equals(post.getOwner().equals(gamer.getUserId()))) {
            throw new BusinessException(TransactionCode.NOT_OWNER);
        }
        postRepository.delete(post);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Post deleted successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse deleteComment(String token, String commentId) {
        Gamer gamer = extractGamer(token);
        Comment comment = commentRepository
                .findById(UUID.fromString(commentId))
                .orElseThrow(() -> new BusinessException(TransactionCode.COMMENT_NOT_FOUND));
        if (Boolean.FALSE.equals(comment.getOwner().equals(gamer.getUserId()))) {
            throw new BusinessException(TransactionCode.NOT_OWNER);
        }
        commentRepository.delete(comment);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Comment deleted successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse joinCommunity(String token, CommunityRequest communityRequest) {
        Gamer gamer = extractGamer(token);
        String communityId = communityRequest.getCommunityId();
        Community community = communityRepository
                .findById(UUID.fromString(communityId))
                .orElseThrow(() -> new BusinessException(TransactionCode.COMMUNITY_NOT_FOUND));
        if (Boolean.TRUE.equals(community.getMembers().contains(gamer))) {
            throw new BusinessException(TransactionCode.ALREADY_MEMBER);
        }
        community.getMembers().add(gamer);
        communityRepository.save(community);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Joined " + community.getName() + " successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse leaveCommunity(String token, CommunityRequest communityRequest) {
        Gamer gamer = extractGamer(token);
        String communityId = communityRequest.getCommunityId();
        Community community = communityRepository
                .findById(UUID.fromString(communityId))
                .orElseThrow(() -> new BusinessException(TransactionCode.COMMUNITY_NOT_FOUND));
        if (Boolean.FALSE.equals(community.getMembers().contains(gamer))) {
            throw new BusinessException(TransactionCode.NOT_MEMBER);
        } else if (Boolean.TRUE.equals(community.getOwner().equals(gamer))) {
            throw new BusinessException(TransactionCode.USER_OWNER);
        }

        community.getMembers().remove(gamer);
        communityRepository.save(community);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Left " + community.getName() + " successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public CommentsResponse getPostComments(String postId) {
        Post post = postRepository
                .findById(UUID.fromString(postId))
                .orElseThrow(() -> new BusinessException(TransactionCode.POST_NOT_FOUND));
        Set<Comment> comments = post.getComments();
        List<CommentDto> commentDtos = new ArrayList<>();
        comments.forEach(comment -> {
            CommentDto commentDto = new CommentDto();
            BeanUtils.copyProperties(comment, commentDto);
            commentDto.setCommentId(comment.getCommentId().toString());
            Gamer gamer = gamerRepository
                    .findById(comment.getOwner())
                    .orElseThrow(() -> new BusinessException(TransactionCode.USER_NOT_FOUND));
            commentDto.setUsername(gamer.getGamerUsername());
            commentDto.setAvatar(gamer.getAvatar());
            commentDtos.add(commentDto);
        });

        CommentsResponse commentsResponse = new CommentsResponse();
        CommentsResponseBody body = new CommentsResponseBody();
        body.setComments(commentDtos);
        commentsResponse.setBody(new BaseBody<>(body));
        commentsResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return commentsResponse;
    }

    @Override
    public DefaultMessageResponse likePost(String token, String postId) {
        Gamer gamer = extractGamer(token);
        Post post = postRepository
                .findById(UUID.fromString(postId))
                .orElseThrow(() -> new BusinessException(TransactionCode.POST_NOT_FOUND));
        if (Boolean.TRUE.equals(post.getLikes().contains(gamer))) {
            throw new BusinessException(TransactionCode.ALREADY_LIKED);
        }
        post.getLikes().add(gamer);
        post.setLikeCount(post.getLikes().size());
        postRepository.save(post);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Liked post successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse likeComment(String token, String commentId) {
        Gamer gamer = extractGamer(token);
        Comment comment = commentRepository
                .findById(UUID.fromString(commentId))
                .orElseThrow(() -> new BusinessException(TransactionCode.COMMENT_NOT_FOUND));
        if (Boolean.TRUE.equals(comment.getLikes().contains(gamer))) {
            throw new BusinessException(TransactionCode.ALREADY_LIKED);
        }
        comment.getLikes().add(gamer);
        comment.setLikeCount(comment.getLikes().size());
        commentRepository.save(comment);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Liked comment successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    private Gamer extractGamer(String token) {
        String email = jwtService.extractUsername(token);
        Optional<Gamer> gamerOptional = gamerRepository.findByEmail(email);
        if (gamerOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.USER_NOT_FOUND);
        }
        return gamerOptional.get();
    }
}
