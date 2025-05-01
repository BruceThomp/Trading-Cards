public class TradingCard {
    private String imagePath;
    private String name;
    private String year;
    private String type;

    public TradingCard(String imagePath, String name, String year, String type) {
        this.imagePath = imagePath;
        this.name = name;
        this.year = year;
        this.type = type;
    }

    public String getImagePath() { return imagePath; }
    public String getName() { return name; }
    public String getYear() { return year; }
    public String getType() { return type; }
}
