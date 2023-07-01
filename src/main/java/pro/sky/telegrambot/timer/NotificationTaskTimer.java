package pro.sky.telegrambot.timer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
public class NotificationTaskTimer {
    private final NotificationTaskRepository notificationTaskRepository;
    private final TelegramBot telegramBot;

    public NotificationTaskTimer(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBot = telegramBot;
    }

    //УДАЛЕНИЕ ИЗ БД ПО ИСТЕЧЕНИИ ЗАДАННОГО ВРЕМЕНИ:
    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.DAYS)
    // fixedDelay - задержка между концом и началом, fixeRate - задержка между началами
    public void deleteTask() {
        notificationTaskRepository.findAllByNotificationTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .forEach(notificationTask -> {
                    telegramBot.execute(new SendMessage(notificationTask.getChatId(), notificationTask.getMessage()));
                    notificationTaskRepository.delete(notificationTask);
                });
    }

    // НАПОМИНАНИЕ О ЗАДАЧЕ:
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    // fixedDelay - задержка между концом и началом, fixeRate - задержка между началами
    public void sendTask() {
        notificationTaskRepository.findAllByNotificationTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .forEach(notificationTask -> {
                    telegramBot.execute(new SendMessage(notificationTask.getChatId(), notificationTask.getMessage()));
                });
    }
//    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
//    public List<NotificationTask> findAllByTime(){
//        return notificationTaskRepository.findAllByNotificationTime(LocalDateTime.now());}
}
