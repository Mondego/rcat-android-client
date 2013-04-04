package com.bravelittlescientist.rcat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

public class RcatNotification {

    private NotificationManager notificationManager;
    private Context notificationContext;

    public RcatNotification(Context context) {
        notificationContext = context;
        notificationManager =
                (NotificationManager) notificationContext.getSystemService(notificationContext.NOTIFICATION_SERVICE);
    }

    /**
     * Runs notification
     * @param n
     */
    private void notify(Notification n) {
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, n);
    }

    /**
     * @message {'go' : {}}
     */
    public void notifyGameOver() {
        Notification notification = new Notification.Builder(notificationContext)
                .setContentTitle("Game Over!")
                .setContentText(":(")
                .setSmallIcon(R.drawable.puzzle_icon)
                .build();
        notify(notification);
    }

    /**
     * @message {'uid': 'player1234-uid', 'user': 'Luke', 'score': 99}
     * @param uid
     * @param user
     * @param score
     */
    public void notifyNewUserJoined(String uid, String user, Integer score) {
        Notification notification = new Notification.Builder(notificationContext)
                .setContentTitle("Player Joined Game")
                .setContentText(user + " [" + score + " points] joined.")
                .setSmallIcon(R.drawable.puzzle_icon)
                .build();
        notify(notification);
    }

    /**
     * @message {'uid' : 'player1234-uid'}
     * @param uid
     */
    public void notifyUserLeft(String uid) {
        Notification notification = new Notification.Builder(notificationContext)
                .setContentTitle("Jigsaw Game")
                .setContentText("A user has left the game")
                .setSmallIcon(R.drawable.puzzle_icon)
                .build();
        notify(notification);
    }

    /**
     * @message {'scu': [
     *              {'uid': 'player1234-uid', 'user': 'Luke', 'score': 60},
     *              {'uid': 'player5678-uid', 'user': 'Han', 'score': 100},
     *          ]}
     * @param scores
     */
    public void notifyScoreUpdate(Bundle scores) {
        Notification notification = new Notification.Builder(notificationContext)
                .setContentTitle("New Scores!")
                .setContentText("[Scores / High Score / ...?]")
                .setSmallIcon(R.drawable.puzzle_icon)
                .build();
        notify(notification);
    }

    /**
     * @message {'pm': {'id': pid, 'x': 123, 'y': 456, 'l': 'player1234-uid'}}
     * @param uid
     * @param pid
     */
    public void notifyPlayerMovingPiece(String uid, String pid) {
        // TODO
    }

    /**
     * @message {'pd': {'id': pid, 'x': 134, 'y': 678, 'b': false}}
     * @param uid
     * @param pid
     */
    public void notifyPlayerDroppedPiece(String uid, String pid) {
        // TODO
    }

    public void notifyInformation(String info) {
        Notification notification = new Notification.Builder(notificationContext)
                .setContentTitle("RCAT")
                .setContentText(info)
                .setSmallIcon(R.drawable.puzzle_icon)
                .build();
        notify(notification);
    }

}
