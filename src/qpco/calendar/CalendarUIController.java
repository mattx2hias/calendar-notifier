package qpco.calendar;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Calendar;

public class CalendarUIController {
    DateInfo di = new DateInfo();
    int currentDay;

    @FXML Button leftArrow;
    @FXML Label monthYearLabel;
    @FXML Button rightArrow;
    @FXML GridPane dayGrid;
    @FXML HBox notificationUI = new HBox();

    @FXML private void initialize() {
        monthYearLabel.setText(di.currentCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, di.locale)
                + " " + di.currentCalendar.get(Calendar.YEAR));
        currentDay = di.currentCalendar.get(Calendar.DAY_OF_MONTH);
        buildDayGrid();
    }

    @FXML private void changeMonth(ActionEvent e) {
        dayGrid.getChildren().clear();
        notificationUI.getChildren().clear();
        int amount = 0;
        amount = (e.getSource() == leftArrow) ? --amount : ++amount;
        di.currentCalendar.add(Calendar.MONTH, amount);

        monthYearLabel.setText(di.currentCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, di.locale)
                + " " + di.currentCalendar.get(Calendar.YEAR));

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
        Calendar buildCalendar = (Calendar) di.currentCalendar.clone();
        int min = buildCalendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        int max = buildCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

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

        for(int i = min; i <= max; i++) {
            Button b = new Button(Integer.toString(i));
            dayGrid.getChildren().add(b);
            GridPane.setColumnIndex(b, col);
            GridPane.setRowIndex(b, row);
            GridPane.setHalignment(b, HPos.CENTER);
            GridPane.setMargin(b, new Insets(10, 0, 10, 0));
            b.setOnAction(this::openNotificationUI);
            buildCalendar.add(Calendar.DAY_OF_MONTH,1);
            if(currentDay==i) { b.setStyle("-fx-border-color: #039ED3"); } // add blue border to current day
            if(col==6) {
                row++;
                col=0;
            } else col++;
        }
    }

    @FXML private void openNotificationUI(ActionEvent e) {
        notificationUI.getChildren().clear();
        notificationUI.setAlignment(Pos.CENTER);

        // NOTIFICATION TEXT
        TextField notifText = new TextField("Enter notification text");
        notifText.setAlignment(Pos.CENTER);

        // HOUR
        ChoiceBox<Integer> hourSelector = new ChoiceBox<>();
        for (int i = 1; i <= 12; i++) { hourSelector.getItems().add(i); }
        hourSelector.setValue(di.hour);

        // MINUTE
        ChoiceBox<Integer> minuteSelector = new ChoiceBox<>();
        for(int i = 1; i <= 60; i++) { minuteSelector.getItems().add(i); }
        minuteSelector.setValue(di.minute);

        // AMPM
        ChoiceBox<String> ampmSelector = new ChoiceBox<>(FXCollections.observableArrayList("AM", "PM"));
        ampmSelector.setValue(di.ampm);

        // REPEAT
        ChoiceBox<String> repeatSelector = new ChoiceBox<>(FXCollections.observableArrayList(
                "Once", "Yearly", "Monthly", "Weekly", "Daily")
        );
        repeatSelector.setValue("Once");

        Button saveButton = new Button("Save");
        notificationUI.getChildren().addAll(notifText, hourSelector, minuteSelector,ampmSelector,
                repeatSelector, saveButton);

        HBox.setMargin(notifText, new Insets(10, 5, 5, 5));
        HBox.setMargin(hourSelector, new Insets(10, 0, 5, 0));
        HBox.setMargin(minuteSelector, new Insets(10, 0, 5, 0));
        HBox.setMargin(ampmSelector, new Insets(10, 0, 5, 0));
        HBox.setMargin(repeatSelector, new Insets(10, 5, 5, 10));
        HBox.setMargin(saveButton, new Insets(10, 5, 5, 10));
    }
}