package controllers;

import services.FarmDataService;

public interface PageController {
    void bind(FarmDataService farmDataService, MainController mainController);

    void refreshView();
}
