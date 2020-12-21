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
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class CalendarNotifier extends Application {

    @Override
    public void start(Stage primaryStage) throws RuntimeException, IOException {
        StageInfo.pStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("calendarUI.fxml"));

        Scene scene = new Scene(root, 1000, StageInfo.height);
        StageInfo.pStage.setTitle("Calendar");
        StageInfo.pStage.setScene(scene);
        StageInfo.pStage.show();
    }

    public static void main(String[] args) throws RuntimeException {
        try{
            BackgroundTasks bgt1 = new BackgroundTasks();
            launch(args);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}

class BackgroundTasks {
    Timer timer = new Timer();

    /**
     * Constructor creates new {@link TimerTask} that runs every 15 seconds.
     * Compares current time to every notification time in the {@link HandleNotifications} notifMap.
     */
    BackgroundTasks() {
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    notifTimeMatch(HandleNotifications.notifMap);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 15000);
    }

    /**
     * Compares current time to parameterized time.
     * <p>
     * @param notifMap  -   map holds key(date and time), value(notification text) pair.
     * @throws AWTException
     */
    void notifTimeMatch(Map<String, String> notifMap) throws AWTException {
        String currentTime = new SimpleDateFormat(" MM-dd-yyyy").format(new Date()) + " at " + new SimpleDateFormat("h:mma").format(new Date());
        for(Map.Entry<String, String> map : notifMap.entrySet()) {
            if(map.getKey().equals(currentTime)) {
                displayTray(map.getValue(), map.getKey()); // send notification text and date to system tray
            }
        }
    }

    /**
     * Handles WIN10 notification popup.
     * <p>
     * @param notification  -   notification String.
     * @param time  -   date and time String.
     * @throws AWTException
     */
    public void displayTray(String notification, String time) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);

        trayIcon.displayMessage(notification, null, TrayIcon.MessageType.NONE);
    }
}

class StageInfo {
    protected static Stage pStage;
    protected static int height = 650;

    static void modifyStageHeight(int heightChange) {
        StageInfo.height += heightChange;
        StageInfo.pStage.setHeight(StageInfo.height);
    }

    static void resetStageHeight() {
        StageInfo.height = 650;
        StageInfo.pStage.setHeight(StageInfo.height);
    }
}

interface DateInfo {
    Locale locale = Locale.getDefault();
    Calendar currentCalendar = new GregorianCalendar();
    Calendar updateCalendar = new GregorianCalendar(); // update when month/year changes
    int hour = currentCalendar.get(Calendar.HOUR);
    String minute = String.format("%02d", currentCalendar.get(Calendar.MINUTE));
    String ampm = currentCalendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, locale);
}

class SendSMS {
    private String smsMessage = null;
    private List<String> phoneNumbers = new ArrayList<>();

    public String getSMSMessage() {
        return smsMessage;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setSMSMessage(String newMessage) {
        this.smsMessage = newMessage;
    }

    public void addPhoneNumbers(String newNumbers) {
        String[] numbers = newNumbers.split(" ");
        this.phoneNumbers.addAll(Arrays.asList(numbers));
    }
}

class HandleNotifications {
    String filePath = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
    private final File f = new File(filePath + "\\Notification Repository.txt");
    String notification, minute, ampm, repeat; // add notification UI
    Date updatedDate; // update when month/year changes, set to first day of new month
    int day, hour;
    Button[] buttons = new Button[31]; // buttons for day grid
    Map<String, Button> notifListMap = new HashMap<>();

    static Map<String, String> notifMap = new HashMap<>();

    /**
     * Writes new notifications to .txt repository.
     * <p>
     * @param smsMessage    -   String to be sent as a SMS message.
     * @param phoneNumbers  -   List of phone numbers for the smsMessage String to be sent.
     * @throws IOException
     */
    void saveNotification(String smsMessage, List<String> phoneNumbers) throws IOException {
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
        if(smsMessage.equals("Enter text message")) {
            storedNotification =
                    notifDate + " at " + hour + ":" + minute + ampm + "\n"
                    + notification + "\n\n";
        } else { // handle sms message
            storedNotification =
                    notifDate + " at " + hour + ":" + minute + ampm + "\n"
                    + notification + "\n"
                    + "SMS: " + smsMessage + " Number(s): " + phoneNumbers + "\n\n";
        }

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
     * @throws IOException
     */
    void readNotifications(VBox notificationList) throws IOException {
        StageInfo.resetStageHeight();
        if(f.createNewFile()) {
            System.out.println("New File created at " + f.getAbsolutePath());
        }
        Scanner input;
        String line, lineCopy, notification = null, time = null;
        input = new Scanner(f);
        int month;
        String weekday;
        Calendar c = (Calendar) DateInfo.updateCalendar.clone();

        while(input.hasNextLine()) {
            line = input.nextLine();
            lineCopy = line;

            if(line.startsWith("Once") || line.startsWith("Yearly") || line.startsWith("Monthly") || line.startsWith("Weekly")) {
                line = line.substring(line.indexOf(' '));
                time = line;

                month = Integer.parseInt(line.substring(1, 3)) - 1;
                day = Integer.parseInt(line.substring(4, 6));
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, day);
                weekday = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

            } else if (line.isEmpty()) { continue; }
            else {
                notification = line;
                //System.out.println("key: " + time + " value: " + notification);
                notifMap.put(time, notification);
                if(Integer.valueOf(DateInfo.updateCalendar.get(Calendar.DAY_OF_MONTH)).equals(day)) {
                    if(!input.hasNextLine()) { break; }
                    notificationList.getChildren().add(displayNotifications(notification));
                }
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
                    continue;
                }
            }
        }
    }

    /**
     * Adds new HBox with notification and delete Button to the notifListMap.
     * <p>
     * @param notification  -   notification text.
     * @return  -   new HBox with notification and delete button.
     */
    private HBox displayNotifications(String notification) {
        HBox notifListItem = new HBox();
        Button b = new Button("Delete");
        b.setOnAction(this::deleteNotification);

        notifListMap.put(notification, b);
        notifListItem.getChildren().addAll(new Text(notification), b);
        notifListItem.setAlignment(Pos.CENTER);
        StageInfo.modifyStageHeight(25);
        return notifListItem;
    }

    /**
     *  Writes everything but the specified notification to new temp file, renames temp file and deletes original.
     *  <p>
     * @param a -   delete Button press.
     */
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
                            input.nextLine(); // SMS message line
                            //input.nextLine(); // line spacer
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
