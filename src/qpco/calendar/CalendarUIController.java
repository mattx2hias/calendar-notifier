package qpco.calendar;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

public class CalendarUIController {
    @FXML VBox notificationList;
    DateInfo di = new DateInfo();
    private int currentDay;

    @FXML private Button leftArrow;
    @FXML private Label monthYearLabel;
    //@FXML private Button rightArrow;
    @FXML private GridPane dayGrid;
    @FXML HBox notificationUI;
    @FXML HBox smsMessageBox;
    private Button pressedButton;
    HandleNotifications nh = new HandleNotifications();

    @FXML private void initialize() {
        monthYearLabel.setText(di.currentCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, DateInfo.locale)
                + " " + di.currentCalendar.get(Calendar.YEAR));
        currentDay = di.currentCalendar.get(Calendar.DAY_OF_MONTH);
        buildDayGrid();
    }

    @FXML private void changeMonth(ActionEvent e) {
        dayGrid.getChildren().clear();
        notificationUI.getChildren().clear();

        int amount = 0;
        amount = (e.getSource() == leftArrow) ? --amount : ++amount;
        DateInfo.updateCalendar.add(Calendar.MONTH, amount);

        monthYearLabel.setText(DateInfo.updateCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, DateInfo.locale)
                + " " + DateInfo.updateCalendar.get(Calendar.YEAR));

        buildDayGrid();
    }

    /**
     * Call to display days in previous or next month.
     * @param c             Calendar object to manipulate
     * @param monthChange   -1 for the previous month, 1 for the next month
     */
    private void displayPrevOrNextMonthDays(Calendar c, int monthChange) {
        int row = (monthChange == -1) ? 0 : c.get(Calendar.WEEK_OF_MONTH) - 1;
        c.add(Calendar.MONTH, monthChange);
        int day = (monthChange == -1) ? c.getActualMaximum(Calendar.DAY_OF_MONTH) : 1;
        c.set(Calendar.DAY_OF_MONTH, day);

        int col = c.get(Calendar.DAY_OF_WEEK) - 1;
        int colLimit = (monthChange == -1) ? -1 : 7;

        while(col != colLimit) {
            Text dayNumber = new Text(Integer.toString(day));
            dayNumber.setFill(Color.GRAY);
            day = (monthChange == -1) ? --day : ++day;
            dayGrid.getChildren().add(dayNumber);
            GridPane.setColumnIndex(dayNumber, col);
            GridPane.setRowIndex(dayNumber, row);
            GridPane.setHalignment(dayNumber, HPos.CENTER);
            GridPane.setMargin(dayNumber, new Insets(10, 0, 10, 0));
            col = (monthChange == -1) ? --col : ++col;
        }
    }

    @FXML private void buildDayGrid() {
        StageInfo.resetStageHeight();

        Calendar buildCalendar = (Calendar) DateInfo.updateCalendar.clone();
        int min = buildCalendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        int max = buildCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        DateInfo.updateCalendar.set(Calendar.DAY_OF_MONTH, max);

        // handle months with 6 weeks
        if(DateInfo.updateCalendar.get(Calendar.WEEK_OF_MONTH) == 6) {
            StageInfo.modifyStageHeight(75);
        }

        buildCalendar.set(Calendar.DAY_OF_MONTH, max);
        if(buildCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            Calendar modifyCalendar = (Calendar) buildCalendar.clone();
            displayPrevOrNextMonthDays(modifyCalendar, 1);
        }

        buildCalendar.set(Calendar.DAY_OF_MONTH, 1);
        if(buildCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            Calendar modifyCalendar = (Calendar) buildCalendar.clone();
            displayPrevOrNextMonthDays(modifyCalendar, -1);
        }

        int col = buildCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        int row = 0;

        for(int i = min-1; i < max; i++) {
            nh.buttons[i] = new Button(Integer.toString(i+1));
            Button b = nh.buttons[i];
            b.setStyle("-fx-background-color: none");

            dayGrid.getChildren().add(b);
            GridPane.setColumnIndex(b, col);
            GridPane.setRowIndex(b, row);
            GridPane.setHalignment(b, HPos.CENTER);
            GridPane.setMargin(b, new Insets(10, 0, 10, 0));
            b.setOnAction(this::openNotificationUI);

            buildCalendar.add(Calendar.DAY_OF_MONTH,1);
            if(currentDay-1 == i && DateInfo.updateCalendar.get(Calendar.MONTH) == di.currentCalendar.get(Calendar.MONTH)) {
                b.setStyle("-fx-border-color: #039ED3"); // add blue border to current day
            }
            if(col==6) {
                row++;
                col=0;
            } else col++;
        }
        try {
            nh.readNotifications(notificationList);
            notificationList.getChildren().clear();
        } catch (FileNotFoundException e1) {
            System.out.println("File not found.");
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void openNotificationUI(ActionEvent e) {
//        StageInfo.height += 25;
//        StageInfo.pStage.setHeight(StageInfo.height);
        nh.updatedDate = DateInfo.updateCalendar.getTime();
        notificationList.getChildren().clear();

        if(pressedButton != null && pressedButton.getText().equals(Integer.toString(DateInfo.updateCalendar.get(Calendar.DAY_OF_MONTH)))){
            pressedButton.setStyle("-fx-border-color: none");
        }
        notificationUI.getChildren().clear();
        notificationUI.setAlignment(Pos.CENTER);
        pressedButton = (Button)e.getSource();
        pressedButton.setStyle("-fx-border-color: #039ED3");
        DateInfo.updateCalendar.set(Calendar.DAY_OF_MONTH, (Integer.parseInt(pressedButton.getText())));
        nh.day = Integer.parseInt(pressedButton.getText());

        try {
            nh.readNotifications(notificationList);
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        // NOTIFICATION TEXT
        TextField notifText = new TextField("Enter notification text");
        notifText.setAlignment(Pos.CENTER);

        // HOUR
        ChoiceBox<Integer> hourSelector = new ChoiceBox<>();
        for (int i = 1; i <= 12; i++) { hourSelector.getItems().add(i); }
        hourSelector.setValue(di.hour);

        // MINUTE
        ChoiceBox<String> minuteSelector = new ChoiceBox<>();
        for(int i = 0; i <= 59; i++) {
            String s = String.format("%02d", i);
            minuteSelector.getItems().add(s);
        }
        minuteSelector.setValue(di.minute);

        // AMPM
        ChoiceBox<String> ampmSelector = new ChoiceBox<>(FXCollections.observableArrayList("AM", "PM"));
        ampmSelector.setValue(di.ampm);

        // REPEAT
        ChoiceBox<String> repeatSelector = new ChoiceBox<>(FXCollections.observableArrayList(
                "Once", "Yearly", "Monthly", "Weekly", "Daily")
        );
        repeatSelector.setValue("Once");

        // NOTIFY OPTIONS

        TextField smsMessageField = new TextField("Enter text message");
        TextField phoneNumberField = new TextField("Enter phone number(s)");
        smsMessageBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setMargin(smsMessageField, new Insets(0, 10, 0, 0));
        HBox.setMargin(phoneNumberField, new Insets(0, 50, 0, 0));
        CheckBox smsCheck = new CheckBox("SMS");
        smsCheck.setOnAction(e2 -> {
            if(smsCheck.isSelected()) {
                StageInfo.modifyStageHeight(25);
                smsMessageBox.getChildren().addAll(smsMessageField, phoneNumberField);
            } else {
                StageInfo.height -= 25;
                StageInfo.pStage.setHeight(StageInfo.height);
                smsMessageBox.getChildren().clear();
            }
        });

        CheckBox windowsCheck = new CheckBox("WIN10");

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e1 -> {
            SendSMS newSMS = new SendSMS();
            nh.notification = notifText.getCharacters().toString();
            nh.hour = hourSelector.getValue();
            nh.minute = minuteSelector.getValue();
            nh.ampm = ampmSelector.getValue();
            nh.repeat = repeatSelector.getValue();
            newSMS.smsMessage = smsMessageField.getCharacters().toString();
            newSMS.phoneNumbers.add(phoneNumberField.getCharacters().toString());

            Button b2 = (Button)e.getSource();
            nh.day = Integer.parseInt(b2.getText());
            DateInfo.updateCalendar.set(Calendar.DAY_OF_MONTH, nh.day);
            nh.updatedDate = DateInfo.updateCalendar.getTime();
            try {
                notificationList.getChildren().clear();
                nh.saveNotification(newSMS.smsMessage, newSMS.phoneNumbers);
                nh.readNotifications(notificationList);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        notificationUI.getChildren().addAll(notifText, hourSelector, minuteSelector,ampmSelector,
                repeatSelector, smsCheck, windowsCheck, saveButton);

        HBox.setMargin(notifText, new Insets(10, 5, 5, 5));
        HBox.setMargin(hourSelector, new Insets(10, 0, 5, 0));
        HBox.setMargin(minuteSelector, new Insets(10, 0, 5, 0));
        HBox.setMargin(ampmSelector, new Insets(10, 0, 5, 0));
        HBox.setMargin(repeatSelector, new Insets(10, 5, 5, 10));
        HBox.setMargin(saveButton, new Insets(10, 5, 5, 10));
    }
}