package com.g.laurent.backtobike.Utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.g.laurent.backtobike.R;
import butterknife.BindView;
import butterknife.ButterKnife;


public class ConfigureDisplayFragment {

    @BindView(R.id.text_date) TextView dateView;
    @BindView(R.id.text_hour) TextView timeView;
    @BindView(R.id.title_route) TextView titleRoute;
    @BindView(R.id.arrow_back) ImageView arrowBack;
    @BindView(R.id.arrow_next) ImageView arrowNext;
    @BindView(R.id.friends_recyclerview) RecyclerView friendsView;
    @BindView(R.id.reject_button) Button buttonCancel;
    @BindView(R.id.accept_button) Button buttonAccept;
    private Context context;
    private View view;

    public ConfigureDisplayFragment(Context context, View view) {
        this.context = context;
        this.view = view;
        ButterKnife.bind(this,view);
    }




}
