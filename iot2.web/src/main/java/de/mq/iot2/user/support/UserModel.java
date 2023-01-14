package de.mq.iot2.user.support;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class UserModel {
	
	private String id; 

	private String name;

	private String algorithm;
	
	@NotNull
	private String locale;
	
	@Size(min = 5, max=15)
	private String password;
	
	@Size(min = 5, max=15)
	private String confirmedPassword;
	
	private boolean passwordChanged;
	
	private boolean loginRequired;
	
	
	public void setLoginRequired(boolean loginRequired) {
		this.loginRequired = loginRequired;
	}

	public boolean isLoginRequired() {
		return loginRequired;
	}

	public String getConfirmedPassword() {
		return confirmedPassword;
	}

	public void setConfirmedPassword(String confirmedPassword) {
		this.confirmedPassword = confirmedPassword;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isPasswordChanged() {
		return passwordChanged;
	}

	public void setPasswordChanged(boolean passwordChanged) {
		this.passwordChanged = passwordChanged;
	}

}
