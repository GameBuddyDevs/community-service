package com.back2261.communityservice.domain.service;

import com.back2261.communityservice.infrastructure.entity.*;
import com.back2261.communityservice.infrastructure.repository.*;
import com.back2261.communityservice.interfaces.dto.*;
import com.back2261.communityservice.interfaces.request.CommunityRequest;
import com.back2261.communityservice.interfaces.request.CreateCommentRequest;
import com.back2261.communityservice.interfaces.request.CreateCommunityRequest;
import com.back2261.communityservice.interfaces.request.PostRequest;
import com.back2261.communityservice.interfaces.response.CommentsResponse;
import com.back2261.communityservice.interfaces.response.CommunityResponse;
import com.back2261.communityservice.interfaces.response.MemberResponse;
import com.back2261.communityservice.interfaces.response.PostResponse;
import io.github.GameBuddyDevs.backendlibrary.base.BaseBody;
import io.github.GameBuddyDevs.backendlibrary.base.Status;
import io.github.GameBuddyDevs.backendlibrary.enums.Role;
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
    private final AvatarsRepository avatarsRepository;
    private final JwtService jwtService;

    @Override
    public CommunityResponse getCommunities(String token) {
        Gamer gamer = extractGamer(token);
        List<Community> communities = communityRepository.findAll();
        List<CommunityDto> communityDtos = new ArrayList<>();
        communities.forEach(community -> {
            CommunityDto communityDto = new CommunityDto();
            BeanUtils.copyProperties(community, communityDto);
            communityDto.setCommunityId(community.getCommunityId().toString());
            communityDto.setMemberCount(community.getMembers().size());
            communityDto.setPostCount(community.getPosts().size());
            communityDto.setIsJoined(community.getMembers().contains(gamer));
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
    public MemberResponse getMembers(String communityId) {
        Community community = communityRepository
                .findById(UUID.fromString(communityId))
                .orElseThrow(() -> new BusinessException(TransactionCode.COMMUNITY_NOT_FOUND));

        Set<Gamer> members = community.getMembers();
        List<GamerDto> memberDtos = new ArrayList<>();
        members.forEach(member -> {
            GamerDto memberDto = new GamerDto();
            BeanUtils.copyProperties(member, memberDto);
            memberDto.setIsOwner(community.getOwner().equals(member));
            memberDto.setAvatar(avatarsRepository
                    .findById(member.getAvatar())
                    .orElse(new Avatars())
                    .getImage());
            memberDtos.add(memberDto);
        });
        MemberResponse memberResponse = new MemberResponse();
        MemberResponseBody body = new MemberResponseBody();
        body.setMembers(memberDtos);
        memberResponse.setBody(new BaseBody<>(body));
        memberResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return memberResponse;
    }

    @Override
    public PostResponse getCommunitiesPosts(String token, String communityId) {
        Gamer gamer = extractGamer(token);
        Community community = communityRepository
                .findById(UUID.fromString(communityId))
                .orElseThrow(() -> new BusinessException(TransactionCode.COMMUNITY_NOT_FOUND));

        PostResponse postResponse = new PostResponse();
        PostResponseBody body = new PostResponseBody();
        postResponse.setStatus(new Status(TransactionCode.DEFAULT_100));

        List<PostDto> postDtos = new ArrayList<>();
        if (Boolean.TRUE.equals(community.getMembers().contains(gamer))) {
            Set<Post> posts = community.getPosts();
            mapPosts(new ArrayList<>(posts), postDtos, gamer);
        }

        body.setPosts(postDtos);
        postResponse.setBody(new BaseBody<>(body));

        return postResponse;
    }

    @Override
    public MemberResponse getPostLikes(String postId) {
        Post post = postRepository
                .findById(UUID.fromString(postId))
                .orElseThrow(() -> new BusinessException(TransactionCode.POST_NOT_FOUND));

        Set<Gamer> postLikes = post.getLikes();
        List<GamerDto> likeDtos = new ArrayList<>();
        mapLikes(postLikes, likeDtos, post.getOwner());
        MemberResponse memberResponse = new MemberResponse();
        MemberResponseBody body = new MemberResponseBody();
        body.setMembers(likeDtos);
        memberResponse.setBody(new BaseBody<>(body));
        memberResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return memberResponse;
    }

    @Override
    public MemberResponse getCommentLikes(String commentId) {
        Comment comment = commentRepository
                .findById(UUID.fromString(commentId))
                .orElseThrow(() -> new BusinessException(TransactionCode.COMMENT_NOT_FOUND));

        Set<Gamer> commentLikes = comment.getLikes();
        List<GamerDto> likeDtos = new ArrayList<>();
        mapLikes(commentLikes, likeDtos, comment.getOwner());
        MemberResponse memberResponse = new MemberResponse();
        MemberResponseBody body = new MemberResponseBody();
        body.setMembers(likeDtos);
        memberResponse.setBody(new BaseBody<>(body));
        memberResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return memberResponse;
    }

    @Override
    public PostResponse getJoinedCommunitiesPosts(String token) {
        Gamer gamer = extractGamer(token);
        Set<Community> communities = gamer.getJoinedCommunities();
        List<Post> joinedCommunitiesPosts = new ArrayList<>();
        communities.forEach(community -> {
            Set<Post> posts = community.getPosts();
            joinedCommunitiesPosts.addAll(posts);
        });
        List<PostDto> postDtos = new ArrayList<>();
        mapPosts(joinedCommunitiesPosts, postDtos, gamer);
        postDtos.sort(Comparator.comparing(PostDto::getUpdatedDate).reversed());

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
        post.setCommunity(community);
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
        gamer.getOwnedCommunities().add(community);
        gamer.getJoinedCommunities().add(community);
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
        if (Boolean.FALSE.equals(community.getOwner().equals(gamer))
                && Boolean.FALSE.equals(Objects.equals(gamer.getRole(), Role.ADMIN))) {
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
        if (Boolean.FALSE.equals(post.getOwner().equals(gamer.getUserId()))
                && Boolean.FALSE.equals(Objects.equals(gamer.getRole(), Role.ADMIN))) {
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
        if (Boolean.FALSE.equals(comment.getOwner().equals(gamer.getUserId()))
                && Boolean.FALSE.equals(Objects.equals(gamer.getRole(), Role.ADMIN))) {
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
        gamer.getJoinedCommunities().add(community);
        gamerRepository.save(gamer);

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
        gamer.getJoinedCommunities().remove(community);
        gamerRepository.save(gamer);

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
            String avatar = avatarsRepository
                    .findById(gamer.getAvatar())
                    .orElse(new Avatars())
                    .getImage();
            commentDto.setAvatar(avatar);
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

    private void mapPosts(List<Post> posts, List<PostDto> postDtos, Gamer gamer) {
        posts.forEach(post -> {
            PostDto postDto = new PostDto();
            BeanUtils.copyProperties(post, postDto);

            postDto.setPostId(post.getPostId().toString());
            postDto.setCommunityName(post.getCommunity().getName());
            Gamer postOwner = gamerRepository
                    .findById(post.getOwner())
                    .orElseThrow(() -> new BusinessException(TransactionCode.USER_NOT_FOUND));
            postDto.setUsername(postOwner.getGamerUsername());
            String avatar = avatarsRepository
                    .findById(postOwner.getAvatar())
                    .orElse(new Avatars())
                    .getImage();
            postDto.setAvatar(avatar);
            postDto.setCommentCount(post.getComments().size());
            postDto.setIsLiked(post.getLikes().contains(gamer));
            postDtos.add(postDto);
        });
    }

    private void mapLikes(Set<Gamer> likes, List<GamerDto> likeDtos, String ownerId) {
        likes.forEach(like -> {
            GamerDto likeDto = new GamerDto();
            BeanUtils.copyProperties(like, likeDto);
            likeDto.setIsOwner(Objects.equals(ownerId, like.getUserId()));
            likeDto.setAvatar(avatarsRepository
                    .findById(like.getAvatar())
                    .orElse(new Avatars())
                    .getImage());
            likeDtos.add(likeDto);
        });
    }
}
