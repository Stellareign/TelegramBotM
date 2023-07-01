package pro.sky.telegrambot.repository;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.sky.telegrambot.entity.NotificationTask;


import java.time.LocalDateTime;
import java.util.List;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    List<NotificationTask> findAllByNotificationTime(LocalDateTime localDateTime);

    @Override
    List<NotificationTask> findAll();

    List<NotificationTask> findAllByNotificationTimeAndChatId(LocalDateTime localDateTime, long chatId);


    //@Query("SELECT nt FROM NotificationTask nt WHERE nt.user.name like %:nameLike%") // спринг будет брать запрос из этой строки
//@Query(value = "SELECT nt.* FROM notification_tasks nt INNER JOIN user u ON nt.user_id = u.id WHERE u.name like %:nameLike%", nativeQuery = true)
//List<NotificationTask> findAllByUser_NameLike(@Param("nameLike") String nameLike);
    @Modifying
    @Query("DELETE FROM NotificationTask WHERE message like %:nameLike%")
    void removeAllLike(@Param("nameLike") String nameLike);
//    @Modifying
//    @Query("DELETE FROM NotificationTask WHERE id like  %:notyficationId")
//    void removeById(@Param("id") long notyficationId);


}
