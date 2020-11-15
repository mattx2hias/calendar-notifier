package qpco.calendar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.Calendar;

public class CalendarUIController {
    DateInfo di = new DateInfo();

    @FXML Button leftArrow;
    @FXML Label monthYearLabel;
    @FXML Button rightArrow;
    @FXML GridPane dayGrid;

    @FXML private void initialize() {
        monthYearLabel.setText(di.c.getDisplayName(Calendar.MONTH, Calendar.LONG, di.locale)
                + " " + di.c.get(Calendar.YEAR));
        buildDays();
    }

    @FXML private void changeMonth(ActionEvent e) {
        dayGrid.getChildren().clear();
        int amount = 0;
        amount = (e.getSource() == leftArrow) ? --amount : ++amount;
        di.c.add(Calendar.MONTH, amount);

        monthYearLabel.setText(di.c.getDisplayName(Calendar.MONTH, Calendar.LONG, di.locale)
                + " " + di.c.get(Calendar.YEAR));

        buildDays();
    }

    @FXML private void buildDays() {
        Calendar c2 = (Calendar) di.c.clone();
        int min = c2.getActualMinimum(Calendar.DAY_OF_MONTH);
        int max = c2.getActualMaximum(Calendar.DAY_OF_MONTH);
        c2.set(Calendar.DAY_OF_MONTH, 1);

        int col = c2.get(Calendar.DAY_OF_WEEK)-1;
        int row = 0;

        for(int i = min; i <= max; i++) {
            Button b = new Button(Integer.toString(i));
            dayGrid.getChildren().add(b);
            GridPane.setColumnIndex(b, col);
            GridPane.setRowIndex(b, row);
            GridPane.setHalignment(b, HPos.CENTER);
            GridPane.setMargin(b, new Insets(10, 0, 10, 0));
            c2.add(Calendar.DAY_OF_MONTH,1);

            if(col == 6) {
                row++;
                col=0;
            } else col++;
        }
    }
}