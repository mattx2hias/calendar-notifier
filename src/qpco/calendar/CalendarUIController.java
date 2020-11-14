package qpco.calendar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CalendarUIController {
    DateInfo di = new DateInfo();

    @FXML Button leftArrow;
    @FXML Label monthYearLabel;
    @FXML Button rightArrow;

    @FXML private void initialize() {
        monthYearLabel.setText(di.strMonth + " " + di.intYear);
    }

    @FXML private void changeMonth(ActionEvent e) {
        di.intMonth = (e.getSource() == leftArrow) ? --di.intMonth : ++di.intMonth;
        if(di.intMonth == 13) {
            di.intMonth = 1;
            di.intYear++;
        } else if (di.intMonth == 0) {
            di.intMonth = 12;
            di.intYear--;
        }
        monthYearLabel.setText(di.getMonth(di.intMonth) + " " + di.intYear);
    }
}