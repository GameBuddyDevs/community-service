package com.back2261.communityservice.domain.service;

import com.back2261.communityservice.interfaces.request.CommunityRequest;
import com.back2261.communityservice.interfaces.request.CreateCommentRequest;
import com.back2261.communityservice.interfaces.request.CreateCommunityRequest;
import com.back2261.communityservice.interfaces.request.PostRequest;
import com.back2261.communityservice.interfaces.response.*;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;

public interface CommunityService {

    CommunityResponse getCommunities(String token);

    MemberResponse getMembers(String communityId);

    PostResponse getCommunitiesPosts(String token, String communityId);

    MemberResponse getPostLikes(String postId);

    MemberResponse getCommentLikes(String commentId);

    PostResponse getJoinedCommunitiesPosts(String token);

    DefaultMessageResponse createPost(String token, PostRequest postRequest);

    DefaultMessageResponse createCommunity(String token, CreateCommunityRequest communityRequest);

    DefaultMessageResponse createComment(String token, CreateCommentRequest commentRequest);

    DefaultMessageResponse deleteCommunity(String token, CommunityRequest communityRequest);

    DefaultMessageResponse deletePost(String token, String postId);

    DefaultMessageResponse deleteComment(String token, String commentId);

    DefaultMessageResponse joinCommunity(String token, CommunityRequest communityRequest);

    DefaultMessageResponse leaveCommunity(String token, CommunityRequest communityRequest);

    CommentsResponse getPostComments(String postId);

    DefaultMessageResponse likePost(String token, String postId);

    DefaultMessageResponse likeComment(String token, String commentId);
}
