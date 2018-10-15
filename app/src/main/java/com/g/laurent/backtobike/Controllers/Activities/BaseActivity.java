package com.g.laurent.backtobike.Controllers.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.g.laurent.backtobike.Models.ToolbarManager;
import com.g.laurent.backtobike.R;

public class BaseActivity extends AppCompatActivity {

    protected final static String MENU_MAIN_PAGE = "menu_main_page";
    protected final static String MENU_MY_FRIENDS = "menu_my_friends";
    protected final static String MENU_MY_EVENTS = "menu_my_events";
    protected final static String MENU_MY_INVITS = "menu_my_invits";
    protected final static String MENU_MY_ROUTES = "menu_my_routes";
    protected final static String MENU_TRACE_ROUTE = "menu_trace_route";
    protected final static String MENU_CREATE_EVENT = "menu_create_event";
    protected final static String MENU_SIGN_OUT= "menu_sign_out";
    protected ToolbarManager toolbarManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        toolbarManager = new ToolbarManager();
    }

    public void launchMainActivity(){




    }

    public void launchFriendsActivity(){




    }

    public void launchEventActivity(){




    }

    public void launchDisplayActivity(String menuSelected){




    }

}
