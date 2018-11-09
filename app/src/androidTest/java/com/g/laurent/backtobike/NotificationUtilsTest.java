package com.g.laurent.backtobike;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import com.g.laurent.backtobike.Utils.NotificationUtils;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static android.support.test.InstrumentationRegistry.getInstrumentation;


@RunWith(AndroidJUnit4.class)
@MediumTest
public class NotificationUtilsTest {

    @Test
    public void test_notification_message(){
        String content = NotificationUtils.buildContentNotification("yannick91","new_invit");
        Assert.assertEquals("yannick91 has sent you a new invitation!", NotificationUtils.buildMessageNotification(getInstrumentation().getTargetContext(), content));
    }

}
