package com.g.laurent.backtobike.Models;

import java.util.List;

public interface OnBikeEventDataGetListener {

    void onSuccess(BikeEvent bikeEvent);

    void onSuccess(List<BikeEvent> bikeEvent);

    void onFailure(String error);

}
