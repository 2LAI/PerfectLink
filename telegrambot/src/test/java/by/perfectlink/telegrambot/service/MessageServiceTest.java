package by.perfectlink.telegrambot.service;

import by.perfectlink.telegrambot.config.Mapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TelegramBot.class, Mapper.class, MessageService.class})
class MessageServiceTest {

    @Autowired
    TelegramBot telegramBot;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MessageService messageService;

    @Test
    void onUnknownReceived() throws IOException, TelegramApiException {
        Update update = objectMapper.readValue( new File( "src/test/resources/update.json" ), Update.class );
        SendMessage actualResult = messageService.onUpdateReceived( update );
        SendMessage expectedResult = makeMessage( "Do no" );
        assertEquals( expectedResult, actualResult );
    }

    @Test
    void onStartReceived() throws IOException, TelegramApiException {
        Update update = objectMapper.readValue( new File( "src/test/resources/start.json" ), Update.class );
        SendMessage actualResult = messageService.onUpdateReceived( update );
        SendMessage expectedResult = makeMessage( "Hello" );
        assertEquals( expectedResult, actualResult );
    }

    @Test
    void onSettingsReceived() throws IOException, TelegramApiException {
        Update update = objectMapper.readValue( new File( "src/test/resources/settings.json" ), Update.class );
        SendMessage actualResult = messageService.onUpdateReceived( update );
        SendMessage expectedResult = makeMessage( "Settings" );
        assertEquals( expectedResult, actualResult );
    }

    @Test
    void onHelpReceived() throws IOException, TelegramApiException {
        Update update = objectMapper.readValue( new File( "src/test/resources/help.json" ), Update.class );
        SendMessage actualResult = messageService.onUpdateReceived( update );
        SendMessage expectedResult = makeMessage( "Help" );
        assertEquals( expectedResult, actualResult );
    }

    private SendMessage makeMessage(String text) {
        SendMessage sendMessage = new SendMessage( );
        sendMessage.setChatId( 468388774L );
        sendMessage.setText( text );
        return sendMessage;
    }

}