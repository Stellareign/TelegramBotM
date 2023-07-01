package pro.sky.telegrambot.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notification_tasks")
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String message;
    @Column(name = "chat_id", nullable = false)
    private long chatId;

    @Column(name = "notification_date_time", nullable = false)
    private LocalDateTime notificationTime;
    //  @OneToOne
    //  private User user;

    public long getId() {
        return id;
    }

    public NotificationTask setId(long id) {
        this.id = id;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public NotificationTask setMessage(String message) {
        this.message = message;
        return this;
    }

    public long getChatId() {
        return chatId;
    }

    public NotificationTask setChatId(long chatId) {
        this.chatId = chatId;
        return this;
    }

    public LocalDateTime getNotificationTime() {
        return notificationTime;
    }

    public NotificationTask setNotificationTime(LocalDateTime notificationTime) {
        this.notificationTime = notificationTime;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationTask)) return false;
        NotificationTask that = (NotificationTask) o;
        return getId() == that.getId() && getChatId() == that.getChatId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getChatId());
    }

    @Override
    public String toString() {
        return "Задача " +
                "id=" + id +
                ": " + message + "," +
                " запланированное время: " + notificationTime.getDayOfMonth() +  "."+ notificationTime.getMonthValue() + "."+
                notificationTime.getYear() + " в "+ notificationTime.getHour()+ ":"+ notificationTime.getMinute() +
                '}'+ "\n";
    }
}

