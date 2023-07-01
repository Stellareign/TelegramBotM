package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationTaskService {
    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    public void save(NotificationTask notificationTask) {
        notificationTaskRepository.save(notificationTask);
    }

    public List<NotificationTask> allTaskAtTime(LocalDateTime localDateTime) {
        return notificationTaskRepository.findAllByNotificationTime(localDateTime);
    }

    public List<NotificationTask> allTasksList() {
        return notificationTaskRepository.findAll();
    }
public void deleteNotificationTask(long notificationID) {
        NotificationTask notificationTask =
  notificationTaskRepository.getById(notificationID);
    notificationTaskRepository.delete(notificationTask);
}

}


