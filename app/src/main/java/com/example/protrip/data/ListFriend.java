package com.example.protrip.data;

public class ListFriend {
    private String userId,friends;

    public ListFriend() {
    }

    public ListFriend(String friends,String userId) {
        this.friends = friends;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

}
