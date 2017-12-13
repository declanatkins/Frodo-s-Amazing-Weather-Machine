package service.weather;

public class ForecastInfo {
	
	private double temperature;
	private double humidity;
	private double windSpeed;
	private double precipitation;
	private String description;
	private String timestamp;
	
	public ForecastInfo(double temp, double humid,
						double wSpeed, double precip,
						String desc, String dt) {
		temperature = temp;
		humidity = humid;
		windSpeed = wSpeed;
		precipitation = precip;
		description = desc;
		timestamp = dt;
	}
	
	public double getTemp() {
		return temperature;
	}
	
	public double getHumidity() {
		return humidity;
	}
	
	public double getWind() {
		return windSpeed;
	}
	
	public double getRain() {
		return precipitation;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
}
