package com.g.laurent.backtobike.Models;

public interface CallbackSynchronizeEnd {

    void onCompleted();

    void onFailure(String error);
}
