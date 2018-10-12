package com.g.laurent.backtobike.Models;

public interface OnDataGetListener {

    void onSuccess(Friend friend);

    void onFailure(String error);
}
