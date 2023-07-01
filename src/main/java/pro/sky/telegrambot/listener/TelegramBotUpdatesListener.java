package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class); // логирование апдейта в консоль
    private static final Pattern NOTIFICATION_PATTERN = Pattern.compile(
            "(\\d{1,2}\\.\\d{1,2}\\.\\d{4} \\d{2}:\\d{2})\\s+([А-я\\d\\s.,!?:]+)");
    private static final Pattern NOTIFICATION_PATTERN_DATE = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4}");
    // регулярные выражения, для проверки и задания параметров,
    // здесь: \\d - цифра, \\s - ghj,tk . {1,2} - количество знаков (можно проверить через Alt + Enter -> CheckRegex)
    private static final DateTimeFormatter NOTIFICATION_DATE_FORMATER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"); // форматер даты и времени из паттерна
    private final NotificationTaskService notificationTaskService; // заинжектили интерфейс для реализации метода репозитория

    //@Autowired
    private final TelegramBot telegramBot;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
        //   this.telegramBot.setUpdatesListener(this); // получение обновлений от Telegram API через telegramBot.
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        try {
            // Process your updates here
            updates.stream() // получаем поток апдейтов из листа
                    .filter(update -> update.message() != null)// фильтруем по тем, что не нулл
                    .forEach(update -> { // итерируемся по ним
                        logger.info("Processing update: {}", update); // записываем лог апдейтов на уровне инфо
                        Message message = update.message(); // получаем сообщение из текущего обновления
                        Long chatId = message.chat().id(); // получаем идентификатор чата, к которому относится апдейт
                        String messageText = message.text(); // получаем текст сообщения
                        //*****
                        boolean delitingTask = false;
                        switch (messageText) {
                            case "/start" -> sendMessege(chatId, """
                                    Привет! Я твой планировщик. Отправь задачу в формате дд.мм.гггг чч:мм Сделать домашку.""");
                            // отправляем заданное сообщение на заданный id чата, """ - текстовый блок);


                            case "/all" ->
                                    sendMessege(chatId, "Список всех задач:\n" + notificationTaskService.allTasksList().toString());

//                            case "/TaskAtTime" -> {
//                                sendMessege(chatId, """
//                                        Введи дату задачи в формате дд.мм.гггг.""");
//
//                                String messageText2 = message.text();
//                                if (messageText2 != null) {
//                                    taskAtTime(chatId, messageText2);
//                                }
//                            }
//                            case "/delete" -> {
//                                sendMessege(chatId, "Введи Id задачи для удаления:\n" +
//                                        notificationTaskService.allTasksList().toString());
//                                delitingTask = true;
//                                String messageText3 = message.text();
//                                if (messageText3 != null) {
//                                    long notificationId = Long.parseLong(messageText3);
//                                    notificationTaskService.deleteNotificationTask(notificationId);
//                                } else sendMessege(chatId, "Номер ID задачи указан неверно или такой задачи нет в списке.");
//                            }


                            default -> {
                                if (messageText != null) { // проверяем, не пустой ли текст
                                    saveNotification(chatId, messageText);
                                }

                            }
                        }
                    });
        } catch (
                Exception e) {
            logger.error(e.getMessage()); // ловим ошибку
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL; // успешно завершаем метод, без падения
    }

    //ЗАДАЧА ПО ВРЕМЕНИ:
    private void taskAtTime(long chatId, String messageData) {
        Matcher matcher = NOTIFICATION_PATTERN_DATE.matcher(messageData);
        if (matcher.find()) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(messageData);
                sendMessege(chatId, "Задачи на указанное время: " + notificationTaskService.allTaskAtTime(dateTime).toString());
            } catch (DateTimeParseException e) {
                return;
            }
            ;

        } else sendMessege(chatId, "Дата указана неверно.");
    }

    // ЗАПИСЬ ЗАДАЧИ:
    private void saveNotification(long chatId, String messageText) {
        Matcher matcher = NOTIFICATION_PATTERN.matcher(messageText); // для поиска соответствия между паттерном и текстом месседжа
        if (matcher.find()) {
            LocalDateTime dateTime = parse(matcher.group(1)); // парсим первую группу после проверки соответствия
            if (Objects.isNull(dateTime)) { // обрабатываем нулловое значение из парсинга
                sendMessege(chatId, "Неверный формат даты и/или времени!");

            } else { // если выше всё норм, проверяем вторую группу паттерна
                String text = matcher.group(2);
                NotificationTask notificationTask = new NotificationTask(); // создаём новое напоминание
                notificationTask.setChatId(chatId); // присваиваем созданному напоминанию значения из нашего апдейта
                notificationTask.setMessage(text);
                notificationTask.setNotificationTime(dateTime);
                notificationTaskService.save(notificationTask); // сохранили наше сообщение в БД
                sendMessege(chatId, "Задача запланирована!");
            }
        } else {
            sendMessege(chatId, "Неправильный формат сообщения!");
        }
    }

    // парсинг даты и времени из паттерна в нужный LocalDateTime формат
    @Nullable // для подсвечивания потенциальных NPE
    private LocalDateTime parse(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, NOTIFICATION_DATE_FORMATER);
        } catch (DateTimeParseException e) { // ловим неправильный формат даты, в т.ч. значения даты и времени
            return null;
        }

    }

    // ОТПРАВКА ОТВЕТА БОТА:
    private void sendMessege(Long chatId, String message) { // выносим отправку в отдельный метод
        SendMessage sendMessage = new SendMessage(chatId, message);
        SendResponse sendResponse = telegramBot.execute(sendMessage); // сохраняем в переменную sendMessage
        if (!sendResponse.isOk()) { // если отправка сообщения не удалась
            logger.error("Ошибка отправки сообщения: {}", sendResponse.description()); // сообщаем об ошибке

        }
    }
}
