package by.perfectlink.telegrambot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;


@Service
public class EspecialMessageService {

    final
    TelegramBot telegramBot;

    public EspecialMessageService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public String onUpdateReceived(Update update) {
        return update.getMessage( ).getText( );
    }
}
