package models;

public enum AppPage {
    DASHBOARD("Dashboard", "Farm pulse, performance, and key summaries"),
    ZONES("Zones", "Manage operational areas and production groups"),
    SENSORS("Sensors", "Manage and monitor sensor devices and readings"),
    ALERTS("Alerts", "Track warnings, critical events, and acknowledgements");

    private final String title;
    private final String subtitle;

    AppPage(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
