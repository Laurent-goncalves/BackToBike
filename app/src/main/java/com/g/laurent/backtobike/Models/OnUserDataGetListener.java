package com.g.laurent.backtobike.Models;

public interface OnUserDataGetListener {

    void onSuccess(Boolean datasOK, String login);

    void onFailure(String error);
}
