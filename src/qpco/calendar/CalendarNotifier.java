package qpco.calendar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CalendarNotifier extends Application {
    @Override
    public void start(Stage pm) throws RuntimeException, IOException {
        Parent root = FXMLLoader.load(getClass().getResource("calendarUI.fxml"));

        Scene scene = new Scene(root, 1000, 1000);
        pm.setTitle("Calendar");
        pm.setScene(scene);
        pm.show();
    }

    public static void main(String[] args) throws RuntimeException {
        try{
            launch(args);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}

class DateInfo {
    Locale locale = Locale.getDefault();
    Calendar currentCalendar = new GregorianCalendar();
    int hour = currentCalendar.get(Calendar.HOUR);
    int minute = currentCalendar.get(Calendar.MINUTE);
    String ampm = currentCalendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, locale);
}
