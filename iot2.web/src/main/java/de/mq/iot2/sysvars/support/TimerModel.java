package de.mq.iot2.sysvars.support;

import java.io.Serializable;

public class TimerModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private  String firstUp;
	
	private String secondUp;
	
	private String shadow;
	
	private String down;
	
	
	public String getFirstUp() {
		return firstUp;
	}

	public void setFirstUp(String firstUp) {
		this.firstUp = firstUp;
	}

	public String getSecondUp() {
		return secondUp;
	}

	public void setSecondUp(String secondUp) {
		this.secondUp = secondUp;
	}

	public String getShadow() {
		return shadow;
	}

	public void setShadow(String shadow) {
		this.shadow = shadow;
	}

	public String getDown() {
		return down;
	}

	public void setDown(String down) {
		this.down = down;
	}

	
	
	
	
	
	
	

}
