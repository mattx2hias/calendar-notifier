package main.java;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

public class HandleNotifications {
    String filePath = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
    private final File f = new File(filePath + "\\Notification Repository.txt");
    String notification, minute, ampm, repeat; // add notification UI
    Date updatedDate; // update when month/year changes, set to first day of new month
    int day, hour;
    javafx.scene.control.Button[] buttons = new javafx.scene.control.Button[31]; // buttons for day grid
    Map<String, Button> notifListMap = new HashMap<>();

    static Map<String, String> notifMap = new HashMap<>();

    /**
     * Writes new notifications to .txt repository.
     * <p>
     * @throws IOException  -
     */
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
        String storedNotification;
        storedNotification = notifDate + " at " + hour + ":" + minute + ampm + ", " + notification + "\n\n";

        try {
            Files.write(Paths.get(f.getAbsolutePath()), storedNotification.getBytes(), StandardOpenOption.APPEND);
            System.out.println(storedNotification);
        } catch (IOException e) {
            System.out.println("Failed to write to file: " + f.getAbsolutePath());
            e.printStackTrace();
        }
    }

    /**
     * Parses .txt repository, highlights days with notifications. Calls displayNotifications.
     * <p>
     * @param notificationList  -   VBox to add list items to.
     * @throws IOException      -
     */
    void readNotifications(VBox notificationList) throws IOException {
        StageInfo.resetStageHeight();
        if(f.createNewFile()) {
            System.out.println("New File created at " + f.getAbsolutePath());
        }
        Scanner input;
        String line, lineCopy, notification;
        //String time;
        input = new Scanner(f);
        int month;
        String weekday;
        Calendar c = (Calendar) DateInfo.updateCalendar.clone();

        while(input.hasNextLine()) {
            line = input.nextLine();
            lineCopy = line;
            if(line.isBlank()) { continue; }
            line = line.substring(line.indexOf(' ')); // remove repeat word
            try {
                month = Integer.parseInt(line.substring(1, 3)) - 1;
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                break;
            }

            day = Integer.parseInt(line.substring(4, 6));
            weekday = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);

            //time = line.substring(0, line.indexOf(","));
            notification = line.substring(line.indexOf(",")+2);
            //System.out.println("key: " + time + " value: " + notification);
            notifMap.put(line, notification);
            if(Integer.valueOf(DateInfo.updateCalendar.get(Calendar.DAY_OF_MONTH)).equals(day)) {
                if(!input.hasNextLine()) { break; }
                notificationList.getChildren().add(displayNotifications(notification));
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
                }
            }
        }
    }

    /**
     * Adds new HBox with notification and delete Button to the notifListMap.
     * <p>
     * @param notification  -   notification text.
     * @return              -   new HBox with notification and delete button.
     */
    private HBox displayNotifications(String notification) {
        HBox notifListItem = new HBox();
        javafx.scene.control.Button b = new javafx.scene.control.Button("Delete");
        b.setOnAction(this::deleteNotification);

        notifListMap.put(notification, b);
        notifListItem.getChildren().addAll(new Text(notification), b);
        notifListItem.setAlignment(Pos.CENTER);
        StageInfo.modifyStageHeight(25);
        return notifListItem;
    }

    /**
     *  Writes everything but the specified notification to new temp file, renames temp file and deletes original.
     * <p>
     * @param a -   delete Button press.
     */
    private void deleteNotification(ActionEvent a) {
        File temp = new File(filePath + "\\temp.txt");
        Scanner input = null;
        String line, notification;
        javafx.scene.control.Button b = (javafx.scene.control.Button)a.getSource();
        try {
            if(temp.createNewFile()) { System.out.println("New File created at " + temp.getAbsolutePath()); }
        } catch (IOException e) { e.printStackTrace(); }
        try {
            input = new Scanner(f);
            while(input.hasNextLine()) {
                line = input.nextLine();
                if(line.isBlank()) {
                    line = "\n\n";
                    Files.write(Paths.get(temp.getAbsolutePath()), line.getBytes(), StandardOpenOption.APPEND);
                    continue;
                }
                notification = line.substring(line.indexOf(',')+2);
                for(Map.Entry<String, Button> map : notifListMap.entrySet()) {
                    if(b.equals(map.getValue())) { // if button pressed equals button in map
                        if(notification.equals(map.getKey())) {
                            System.out.println("\nDeleted: " + line);
                            if(!input.hasNextLine()) { break; }
                            input.nextLine();
                            // handle SMS line
                        } else {
                            try {
                                System.out.print("\nSaved: " + line);
                                Files.write(Paths.get(temp.getAbsolutePath()), line.getBytes(), StandardOpenOption.APPEND);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } finally {
            assert input != null;
            input.close();
        }
        boolean delete = f.delete();
        if(delete) { System.out.println("\noriginal file deleted"); }
        boolean rename = temp.renameTo(f);
        if(rename) { System.out.println("temp file renamed"); }
    }
}
