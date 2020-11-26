package qpco.calendar;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
    static Calendar updateCalendar = new GregorianCalendar(); // update when month/year changes
    int hour = currentCalendar.get(Calendar.HOUR);
    String minute = String.format("%02d", currentCalendar.get(Calendar.MINUTE));
    String ampm = currentCalendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, locale);
}

class HandleNotifications {
    String filePath = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
    private final File f = new File(filePath + "\\Notification Repository.txt");
    String notification, minute, ampm, repeat; // add notification UI
    Date updatedDate; // update when month/year changes
    int day, hour;
    Button[] buttons = new Button[31]; // buttons for day grid
    HashMap<String, Button> notifListMap = new HashMap<>();

    void saveNotification() throws IOException {
        System.out.println("Saving...");
        if(f.createNewFile()) {
            System.out.println("New File created at " + f.getAbsolutePath());
        }
        String notifDate = null;
        switch(repeat) {
            case "Once":    notifDate = "Once " + new SimpleDateFormat("MM-dd-yyyy").format(updatedDate);     break;
            case "Yearly":  notifDate = "Yearly " + new SimpleDateFormat("MM-dd-yyyy").format(updatedDate);   break;
            case "Monthly": notifDate = "Monthly " + new SimpleDateFormat("MM-dd-yyyy").format(updatedDate);  break;
            case "Weekly":  notifDate = "Weekly " + new SimpleDateFormat("MM-dd-yyyy").format(updatedDate);   break;
            case "Daily":   notifDate = "Daily " + new SimpleDateFormat("MM-dd-yyyy").format(updatedDate);    break;
        }
        // "notification text" at 12:00AM
        // Once 1-1-2000
        String storedNotification = notification + " at " + hour + ":" + minute + ampm + "\n" +
                notifDate + "\n\n";

        try {
            Files.write(Paths.get(f.getAbsolutePath()), storedNotification.getBytes(), StandardOpenOption.APPEND);
            System.out.println(storedNotification);
        } catch (IOException e) {
            System.out.println("Failed to write to file: " + f.getAbsolutePath());
            e.printStackTrace();
        }
    }

    /*
        @param notificationList     VBox to add list items to
     */
    void readNotifications(VBox notificationList) throws IOException {
        if(f.createNewFile()) {
            System.out.println("New File created at " + f.getAbsolutePath());
        }
        Scanner input;
        String line, lineCopy, notification = null;
        input = new Scanner(f);
        int month;
        String weekday;
        Calendar c = (Calendar) DateInfo.updateCalendar.clone();

        while(input.hasNextLine()) {
            line = input.nextLine();
            lineCopy = line;

            if(line.startsWith("Once") || line.startsWith("Yearly") || line.startsWith("Monthly") || line.startsWith("Weekly")) {
                line = line.substring(line.indexOf(' '));
                month = Integer.parseInt(line.substring(1, 3)) - 1;
                day = Integer.parseInt(line.substring(4, 6));
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, day);
                weekday = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

            } else {
                notification = line;
                continue;
            }

            if(Integer.valueOf(DateInfo.updateCalendar.get(Calendar.MONTH)).equals(month)) {
                if(lineCopy.contains("Once ") && line.contains(Integer.toString(DateInfo.updateCalendar.get(Calendar.YEAR)))) {
                    buttons[day-1].setStyle("-fx-background-color: #039ED3");
                } else if (lineCopy.contains("Yearly ")) {
                    buttons[day-1].setStyle("-fx-background-color: #039ED3");
                }
            }
            if (lineCopy.startsWith("Monthly ")) {
                buttons[day-1].setStyle("-fx-background-color: #039ED3");
            }
            if(lineCopy.startsWith("Weekly ")) {
                if(weekday.equals(DateInfo.updateCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()))) {
                    if(!input.hasNextLine()) { break; }
                    // add loop highlighting every weekday in a month
                    buttons[day-1].setStyle("-fx-background-color: #039ED3");
                    notificationList.getChildren().add(displayNotifications(notification));
                    continue;
                }
            }
            if(Integer.valueOf(DateInfo.updateCalendar.get(Calendar.DAY_OF_MONTH)).equals(day)) {
                if(!input.hasNextLine()) { break; }
                notificationList.getChildren().add(displayNotifications(notification));
            }
        }
    }

    private HBox displayNotifications(String notification) {
        HBox notifListItem = new HBox();
        Button b = new Button("Delete");
        b.setOnAction(this::deleteNotification);

        notifListMap.put(notification, b);
        notifListItem.getChildren().addAll(new Text(notification), b);
        notifListItem.setAlignment(Pos.CENTER);
        return notifListItem;
    }

    private void deleteNotification(ActionEvent a) {
        File temp = new File(filePath + "\\temp.txt");
        Scanner input = null;
        StringBuilder line;
        Button b = (Button)a.getSource();
        try {
            if(temp.createNewFile()) { System.out.println("New File created at " + temp.getAbsolutePath()); }
        } catch (IOException e) { e.printStackTrace(); }
        try {
            input = new Scanner(f);
            while(input.hasNextLine()) {
                line = new StringBuilder(input.nextLine());
                for(Map.Entry<String, Button> map : notifListMap.entrySet()) {
                    if(b.equals(map.getValue())) { // if button pressed equals button in map
                        if(line.toString().equals(map.getKey())) {
                            System.out.println("Deleted: " + line); // notification line
                            line = new StringBuilder("Deleted: " + input.nextLine());
                            System.out.println(line); // date line
                            if(!input.hasNextLine()) { break; }
                            input.nextLine(); // blank line
                        } else {
                            try {
                                line.append("\n");
                                System.out.print("Saved: " + line);
                                Files.write(Paths.get(temp.getAbsolutePath()), line.toString().getBytes(), StandardOpenOption.APPEND);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } finally {
            assert input != null;
            input.close();
        }
        boolean delete = f.delete();
        if(delete) { System.out.println("original file deleted"); }
        boolean rename = temp.renameTo(f);
        if(rename) { System.out.println("temp file renamed"); }
    }
}
