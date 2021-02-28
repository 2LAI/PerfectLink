package by.perfectlink.telegrambot;

import by.perfectlink.telegrambot.config.Mapper;
import by.perfectlink.telegrambot.service.MessageService;
import by.perfectlink.telegrambot.service.TelegramBot;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {TelegramBot.class, Mapper.class, MessageService.class})
class TelegrambotApplicationTests {

	@Test
	void contextLoads() {
	}

}
