package by.perfectlink.telegrambot.service;

import by.perfectlink.telegrambot.logic.PerfectLink;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.HashMap;


@Service
public class MessageService {

    @Autowired
    TelegramBot telegramBot;
    @Autowired
    ObjectMapper objectMapper;


    public SendMessage onUpdateReceived(Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage( );
        if ( update != null ) {
            Message message = update.getMessage( );
            sendMessage.setChatId( message.getChatId( ) );
            if ( message.hasText( ) ) {
                String magText = message.getText( );
                switch (magText) {
                    case "/start":
                        sendMessage.setText( "Привет, " + update.getMessage( ).getFrom( ).getFirstName( ) +
                                "\n" + "Это телеграм бот, который позволит создать тебе мультиссылку! " +
                                "\nНажми -> /help, чтобы увидеть все команды" );
                        telegramBot.execute( sendMessage );
                        break;
                    case "/create":
                        telegramBot.setStateOfBot( "Create" );
                        sendMessage.setText( "Введите название ссылки: " );
                        telegramBot.execute( sendMessage );
                        break;
                    case "/help":
                        sendMessage.setText( "Список всех команд: " +
                                "\n Создать ссылку: -> /create" +
                                "\n Удалить ссылку: -> /delete" +
                                "\n Показать все сслыки: -> /show" );
                        telegramBot.execute( sendMessage );
                        break;
                    default:
                        sendMessage.setText( "Вы ввели: " + message.getText( ) );
                        telegramBot.execute( sendMessage );
                }
            }
        }
        return sendMessage;
    }
}