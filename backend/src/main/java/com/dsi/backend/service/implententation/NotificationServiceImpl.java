package com.dsi.backend.service.implententation;

import com.dsi.backend.exception.NotificationNotFoundException;
import com.dsi.backend.model.*;
import com.dsi.backend.projection.NotificationView;
import com.dsi.backend.repository.AppUserRepository;
import com.dsi.backend.repository.NotificationRepository;
import com.dsi.backend.service.JwtTokenService;
import com.dsi.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Override
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Notification saveApproveByAdminNotification(Product product, Boolean isApprovedByAdmin){
        Notification notification = new Notification();
        StringBuilder text = new StringBuilder("Your product " + product.getName());
        if(isApprovedByAdmin){
            text.append(" has been approved and up for bidding!");
        } else{
            text.append(" has been declined.");
        }
        notification.setText(text.toString());
        notification.setLink("/product/"+product.getId());
        notification.setReceiver(product.getSeller());
        notification.setNotificationTime(LocalDateTime.now());
        return this.saveNotification(notification);
    }

    @Override
    public Notification saveAcceptOrRevertBidNotification(AppUser receiver, Product product, Boolean isReverted){
        Notification notification = new Notification();
        StringBuilder text = new StringBuilder(product.getSeller().getName()+" has ");
        if(!isReverted){
            text.append("accepted your offer for ");
        } else{
            text.append("retracted your offer to buy ");
        }
        text.append(product.getName());
        notification.setText(text.toString());
        notification.setLink("/product/"+product.getId());
        notification.setReceiver(receiver);
        notification.setNotificationTime(LocalDateTime.now());
        return this.saveNotification(notification);
    }

    @Override
    public Notification transactionNotification(AppUser receiver, String tranId){
        Notification notification = new Notification();
        notification.setText("Your payment has been successful with transaction id "+tranId);
        notification.setNotificationTime(LocalDateTime.now());
        notification.setReceiver(receiver);
        return this.saveNotification(notification);
    }

    @Override
    public List<NotificationView> fetchNotification(String token) {
        AppUser receiver = appUserRepository.findByEmail(jwtTokenService.getUsernameFromToken(token.substring(7)));
        List<Notification> notifications = notificationRepository.findByReceiverOrderByNotificationTimeDesc(receiver);
        return notifications.stream()
                .map(this::convertToView)
                .toList();
    }

    @Override
    public String clearAllNotification(String token) {
        AppUser receiver = appUserRepository.findByEmail(jwtTokenService.getUsernameFromToken(token.substring(7)));
        List<Notification> notifications = notificationRepository.findByReceiverOrderByNotificationTimeDesc(receiver);
        notificationRepository.deleteAll(notifications);
        return "All notifications cleared";
    }

    @Override
    public String clearNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(()->new NotificationNotFoundException("Notification id invalid"));

        notificationRepository.delete(notification);
        return "Notification cleared";
    }

    @Override
    public NotificationView convertToView(Notification notification) {
        return new SpelAwareProxyProjectionFactory().createProjection(NotificationView.class, notification);
    }


}
