package by.perfectlink.telegrambot.service;

import by.perfectlink.telegrambot.config.Mapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.IOException;


@SpringBootTest(classes = {TelegramBot.class, Mapper.class})
class TelegramBotTest {

    @Autowired
    TelegramBot telegramBot;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void onUpdateReceived() throws IOException {
        Update update = objectMapper.readValue( new File( "src/test/resources/update.json" ), Update.class );
        telegramBot.onUpdateReceived( update );
    }

    @Test
    void sendMessage() {
        SendMessage sendMessage = new SendMessage( );
        sendMessage.setChatId( 468388774L );

    }
}