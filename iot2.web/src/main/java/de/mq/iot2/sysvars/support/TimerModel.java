package de.mq.iot2.sysvars.support;

public class TimerModel {

	@ValidTime
	private String upTime;
	@ValidTime
	private String sunUpTime;
	@ValidTime
	private String shadowTime;
	@ValidTime
	private String sunDownTime;
	
	private boolean update;

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public String getUpTime() {
		return upTime;
	}

	public void setUpTime(final String upTime) {
		this.upTime = upTime;
	}

	public String getSunUpTime() {
		return sunUpTime;
	}

	public void setSunUpTime(final String sunUpTime) {
		this.sunUpTime = sunUpTime;
	}

	public String getShadowTime() {
		return shadowTime;
	}

	public void setShadowTime(final String shadowTime) {
		this.shadowTime = shadowTime;
	}

	public String getSunDownTime() {
		return sunDownTime;
	}

	public void setSunDownTime(final String sunDownTime) {
		this.sunDownTime = sunDownTime;
	}
	

}
