package by.perfectlink.telegrambot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class MessageService {

    @Autowired
    TelegramBot telegramBot;
    @Autowired
    ObjectMapper objectMapper;


    public SendMessage onUpdateReceived(Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        if (update != null) {
            Message message = update.getMessage();
            sendMessage.setChatId(message.getChatId());

            if (message.hasText()) {
                String magText = message.getText();
                switch (magText) {
                    case "/start":
                        start(update, sendMessage);
                        break;
                    case "/create":
                        create(sendMessage, "Create", "Введите название ссылки: ");
                        break;
                    case "/help":
                        sendMessage(sendMessage, "Список всех команд: " +
                                "\n Создать ссылку: -> /create" +
                                "\n Удалить ссылку: -> /delete" +
                                "\n Показать все сслыки: -> /show");
                        break;
                    case "/delete":
                        create(sendMessage, "delete", "Какую ссылку вы хотите удалить? Введите номер: ");
                        break;
                    default:
                        sendMessage(sendMessage, "Вы ввели: " + message.getText());
                }
            }
        }
        return sendMessage;
    }

    private void sendMessage(SendMessage sendMessage, String s) throws TelegramApiException {
        sendMessage.setText(s);
        telegramBot.execute(sendMessage);
    }

    private void create(SendMessage sendMessage, String create, String s) throws TelegramApiException {
        telegramBot.setStateOfBot(create);
        sendMessage(sendMessage, s);
    }

    private void start(Update update, SendMessage sendMessage) throws TelegramApiException {
        sendMessage.setText("Привет, " + update.getMessage().getFrom().getFirstName() +
                "\n" + "Это телеграм бот, который позволит создать тебе мультиссылку! " +
                "\nНажми -> /help, чтобы увидеть все команды");
        telegramBot.execute(sendMessage);
    }
}