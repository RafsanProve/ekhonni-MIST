package com.dsi.backend.service;

import com.dsi.backend.model.AppUser;
import com.dsi.backend.model.Notification;
import com.dsi.backend.model.Product;
import com.dsi.backend.projection.NotificationView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {

    Notification saveNotification(Notification notification);

    Notification saveApproveByAdminNotification(Product product, Boolean isApprovedByAdmin);

    Notification saveAcceptOrRevertBidNotification(AppUser receiver, Product product, Boolean isReverted);

    Notification transactionNotification(AppUser receiver, String tranId);

    List<NotificationView> fetchNotification(String token);

    String clearAllNotification(String token);

    String clearNotification(Long id);

    NotificationView convertToView(Notification notification);
}
