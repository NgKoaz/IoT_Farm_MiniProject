package bku.iot.farmapp.controller;

import bku.iot.farmapp.view.pages.HistoryActivity;

public class HistoryController {
    private final HistoryActivity historyActivity;

    public HistoryController(HistoryActivity historyActivity){
        this.historyActivity = historyActivity;
    }

    public void backToPreviousActivity(){
        historyActivity.finish();
    }
}
