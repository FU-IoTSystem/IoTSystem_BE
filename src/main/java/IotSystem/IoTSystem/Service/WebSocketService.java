package IotSystem.IoTSystem.Service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Send notification to specific user
     */
    public void sendNotificationToUser(String userId, Object notification) {
        messagingTemplate.convertAndSend("/queue/notifications/" + userId, notification);
    }

    /**
     * Send notification to all admins
     */
    public void sendNotificationToAdmins(Object notification) {
        messagingTemplate.convertAndSend("/topic/admin/notifications", notification);
    }

    /**
     * Send rental request update to all admins
     */
    public void sendRentalRequestToAdmins(Object rentalRequest) {
        messagingTemplate.convertAndSend("/topic/admin/rental-requests", rentalRequest);
    }

    /**
     * Send rental request update to specific user
     */
    public void sendRentalRequestUpdateToUser(String userId, Object rentalRequest) {
        messagingTemplate.convertAndSend("/queue/rental-requests/" + userId, rentalRequest);
    }

    /**
     * Send wallet update to specific user
     */
    public void sendWalletUpdateToUser(String userId, Object walletUpdate) {
        messagingTemplate.convertAndSend("/queue/wallet/" + userId, walletUpdate);
    }

    /**
     * Send wallet transaction to specific user
     */
    public void sendWalletTransactionToUser(String userId, Object transaction) {
        messagingTemplate.convertAndSend("/queue/wallet-transactions/" + userId, transaction);
    }

    /**
     * Send penalty update to specific user
     */
    public void sendPenaltyUpdateToUser(String userId, Object penalty) {
        messagingTemplate.convertAndSend("/queue/penalties/" + userId, penalty);
    }

    /**
     * Send group update to specific user (for lecturers)
     */
    public void sendGroupUpdateToUser(String userId, Object groupUpdate) {
        messagingTemplate.convertAndSend("/queue/groups/" + userId, groupUpdate);
    }
}

