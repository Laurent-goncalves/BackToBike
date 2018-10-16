package com.g.laurent.backtobike.Models;

import java.util.List;

public interface OnFriendDataGetListener {

    void onSuccess(Friend friend);

    void onSuccess(List<Friend> listFriend);

    void onFailure(String error);
}
