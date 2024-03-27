package com.dsi.backend.projection;

public interface NotificationView {
    Long getId();
    String getText();
    String getLink();
    AppUserView getReceiver();
    String getFormattedTime();


}
