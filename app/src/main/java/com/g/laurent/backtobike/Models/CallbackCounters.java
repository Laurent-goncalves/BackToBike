package com.g.laurent.backtobike.Models;

import java.util.List;

public interface CallbackCounters {

    void onCompleted(List<Difference> differenceList, List<String> differenceStringList, int counterFriend, int counterEvents, int counterInvits);

    void onFailure(String error);
}
