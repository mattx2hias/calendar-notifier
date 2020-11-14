package qpco.calendar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
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
    String strMonth = getCurrentDate("MMMM");
    int intYear = Integer.parseInt(getCurrentDate("YYYY"));
    int intMonth = Integer.parseInt(this.getCurrentDate("MM"));

    private String getCurrentDate(String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        LocalDate d = LocalDate.now();
        return d.format(dtf);
    }
    protected String getMonth(int intMonth) {
        return Month.of(intMonth).getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
    }
}
