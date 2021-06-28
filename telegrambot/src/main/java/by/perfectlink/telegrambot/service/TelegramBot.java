package by.perfectlink.telegrambot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Component
@PropertySource("application.properties")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    private String stateOfBot = "Start";
    private String linkName;
    private String uRL;
    private String someInformation;
    private String keyToDelete;
    private int counter = 1;

    private final HashMap<Integer, PerfectLink> linkMap = new HashMap<>();

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MessageService messageService;

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage;
        switch (stateOfBot) {
            case ("Start"):
                start(update);
                break;
            case ("Create"):
                create(update, "URL");
                break;
            case ("URL"):
                create(update, "SomeInformation");
            case ("SomeInformation"):
                someInformation(update);
                break;
            case ("delete"):
                delete(update);
                break;
        }
    }

    private void delete(Update update) {
        SendMessage sendMessage;
        sendMessage = getSendMessage(update);
        try {
            updateSendMessage(update, sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        deleteLink(Integer.parseInt(keyToDelete));
        setStateOfBot("Start");
        counter--;
    }

    private void someInformation(Update update) {
        SendMessage sendMessage;
        sendMessage = getSendMessage(update);
        try {
            updateSendMessage(update, sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        sendMessage.setText("Нажми /show, чтобы посмотреть ссылку или /delete, чтобы удалить её");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        createLink(linkName, uRL, someInformation);
        setStateOfBot("Start");
    }

    private void create(Update update, String url) {
        SendMessage sendMessage;
        sendMessage = getSendMessage(update);
        try {
            updateSendMessage(update, sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        setStateOfBot(url);
    }

    private void start(Update update) {
        SendMessage sendMessage;
        if (update.getMessage().getText().equals("/start")
                | update.getMessage().getText().equals("/help")
                | update.getMessage().getText().equals("/create")
                | update.getMessage().getText().equals("/delete")) {
            sendMessage = getSendMessage(update);
            try {
                sendMessage = messageService.onUpdateReceived(update);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            if (sendMessage != null) {
                sendMessage.setReplyMarkup(getMainMenu());
            }
        } else if (update.getMessage().getText().equals("/show")) {
            sendMessage = getSendMessage(update);
            showLinks(sendMessage, linkMap);
        } else {
            saveJson(update);
            sendMessage = getSendMessage(update);
            sendMessage.setText("Вы ввели: " + update.getMessage().getText());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void showLinks(SendMessage sendMessage, HashMap<Integer, PerfectLink> linkMap) {
        for (Integer key : linkMap.keySet()) {
            PerfectLink value = linkMap.get(key);
            sendMessage.setText(key + " -> " + value);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void createLink(String linkName, String uRL, String someInformation) {
        PerfectLink perfectLink = new PerfectLink(linkName, uRL, someInformation);
        linkMap.put(counter, perfectLink);
        counter++;
    }

    private void deleteLink(int deleteKey) {
        linkMap.remove(deleteKey);
        counter++;
    }

    private void updateSendMessage(Update update, SendMessage sendMessage) throws TelegramApiException {
        switch (stateOfBot) {
            case "Create":
                addInformationAboutLinkName(update, sendMessage);
                break;
            case "URL":
                addInformationAboutURL(update, sendMessage);
                break;
            case "SomeInformation":
                addInformationAboutSomeInformation(update, sendMessage);
                break;
            case "delete":
                addInformationAboutKeyDelete(update, sendMessage);
            default:
                sendMessage.setText("Вы ввели" + sendMessage.getText());
        }
    }

    private SendMessage getSendMessage(Update update) {
        saveJson(update);
        SendMessage sendMessage = new SendMessage();
        Message message = update.getMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setReplyMarkup(getMainMenu());
        return sendMessage;
    }

    private void addInformationAboutKeyDelete(Update update, SendMessage sendMessage) throws TelegramApiException {
        sendMessage.setText("Ссылка удалена!");
        execute(sendMessage);
        keyToDelete = update.getMessage().getText();

    }

    private void addInformationAboutLinkName(Update update, SendMessage sendMessage) throws TelegramApiException {
        sendMessage.setText("Введите URL: ");
        execute(sendMessage);
        linkName = update.getMessage().getText();

    }

    private void addInformationAboutURL(Update update, SendMessage sendMessage) throws TelegramApiException {
        sendMessage.setText("Добавьте описание ссылки: ");
        execute(sendMessage);
        uRL = update.getMessage().getText();
    }

    private void addInformationAboutSomeInformation(Update update, SendMessage sendMessage) throws
            TelegramApiException {
        someInformation = update.getMessage().getText();
        sendMessage.setText("Ссылка создана!" + "\nНазвание ссылки: " + getLinkName() + "\nURL ссылки: " + uRL + "\nОписание ссылки: " + getSomeInformation());
        execute(sendMessage);
    }

    private void saveJson(Update update) {
        try {
            objectMapper.writeValue(new File("src/test/resources/update.json"), update);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup getMainMenu() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("/start");
        row1.add("/help");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("/create");
        row2.add("/delete");
        row2.add("/show");

        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);
        markup.setKeyboard(rows);
        return markup;
    }
}
