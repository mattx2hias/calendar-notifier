package qpco.calendar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

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
    static Locale locale = Locale.getDefault();
    Calendar currentCalendar = new GregorianCalendar();
    static Calendar updateCalendar = new GregorianCalendar(); // updates when month is changed in the ui
    int hour = currentCalendar.get(Calendar.HOUR);
    String minute = String.format("%02d", currentCalendar.get(Calendar.MINUTE));
    String ampm = currentCalendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, locale);
}

class HandleNotifications {
    String filePath = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
    private final File f = new File(filePath + "\\Notification Repository.txt");
    String notification, minute, ampm, repeat;
    Date updatedDate;
    int day, hour;
    Label[] notifications;
    Button[] buttons = new Button[31];

    void saveNotification() throws IOException {
        System.out.println("Saving...");
        if(f.createNewFile()) {
            System.out.println("New File created @ " + f.getAbsolutePath());
        }
        String notifDate = null;
        switch(repeat) {
            case "Once":    notifDate = "Once " + new SimpleDateFormat("MM-dd-yyyy").format(updatedDate);     break;
            case "Yearly":  notifDate = "Yearly " + new SimpleDateFormat("MM-dd-yyyy").format(updatedDate);   break;
            case "Monthly": notifDate = "Monthly " + new SimpleDateFormat("MM-dd-yyyy").format(updatedDate);  break;
            case "Weekly":  notifDate = "Weekly " + new SimpleDateFormat("MM-dd-yyyy").format(updatedDate);   break;
            case "Daily":   notifDate = "Daily " + new SimpleDateFormat("MM-dd-yyyy").format(updatedDate);    break;
        }
        String storedNotification =  notifDate + "\n" + notification + " at " + hour + ":" + minute + ampm + "\n\n";

        try {
            Files.write(Paths.get(f.getAbsolutePath()), storedNotification.getBytes(), StandardOpenOption.APPEND);
            System.out.println(storedNotification);
        } catch (IOException e) {
            System.out.println("Failed to write to file: " + f.getAbsolutePath());
            e.printStackTrace();
        }
    }

    void readNotifications() throws FileNotFoundException {
        Scanner input;
        String line, lineCopy;
        input = new Scanner(f);
        int month;

        while(input.hasNextLine()) {
            line = input.nextLine();
            lineCopy = line;

            if(line.startsWith("Once") || line.startsWith("Yearly") || line.startsWith("Monthly") || line.startsWith("Weekly")) {
                line = line.substring(line.indexOf(' '));
                month = Integer.parseInt(line.substring(1, 3));
                day = Integer.parseInt(line.substring(4, 6));
            }
            else { continue; }

            if(Integer.valueOf(DateInfo.updateCalendar.get(Calendar.MONTH)).equals(month-1)) {
                if(lineCopy.contains("Once ") && line.contains(Integer.toString(DateInfo.updateCalendar.get(Calendar.YEAR)))) {
                    buttons[day-1].setStyle("-fx-background-color: #039ED3");
                } else if (lineCopy.contains("Yearly ")) {
                    buttons[day-1].setStyle("-fx-background-color: #039ED3");
                }
            }
            if (lineCopy.startsWith("Monthly ")) {
                buttons[day-1].setStyle("-fx-background-color: #039ED3");
            }
            //System.out.println(month + " " + day + " " + DateInfo.updateCalendar.get(Calendar.YEAR));
        }
    }
}
