package com.g.laurent.backtobike.Models;

import java.util.List;

public interface OnRouteDataGetListener {

    void onSuccess(List<Route> listRoutes);

    void onFailure(String error);
}
