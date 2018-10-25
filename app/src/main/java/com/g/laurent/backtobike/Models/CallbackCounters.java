package com.g.laurent.backtobike.Models;

public interface CallbackCounters {

    void onCompleted(int counterFriend, int counterEvents, int counterInvits);

    void onFailure(String error);
}
