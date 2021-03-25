package main.java;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundTasks {
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
     * @param notifMap      -   map holds key(date and time), value(notification text) pair.
     * @throws AWTException -   from displayTray method.
     */
    void notifTimeMatch(Map<String, String> notifMap) throws AWTException {
        String currentTime = new SimpleDateFormat(" MM-dd-yyyy").format(new Date()) + " at " + new SimpleDateFormat("h:mma").format(new Date());
        for(Map.Entry<String, String> map : notifMap.entrySet()) {
            if(map.getKey().equals(currentTime)) {
                displayTray(map.getValue()); // send notification text and date to system tray
            }
        }
    }

    /**
     * Handles WIN10 notification popup.
     * <p>
     * @param notification  -   notification String.
     * @throws AWTException -   handles SystemTray.
     */
    public void displayTray(String notification) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("System tray icon demo");
        tray.add(trayIcon);

        trayIcon.displayMessage(notification, null, TrayIcon.MessageType.NONE);
    }
}
