package de.mq.iot2.user.support;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.user.User;

@Component
class UserModelMapper implements   ModelMapper<User, UserModel> {

	private final boolean loginRequired;
	
	
	UserModelMapper(@Value("${iot2.login.required:true}") final boolean loginRequired){
		 this.loginRequired=loginRequired;
	 }
	@Override
	public UserModel toWeb(final User user) {
		final UserModel userModel = new UserModel();
		userModel.setId(IdUtil.getId(user));
		userModel.setName(user.name());
		userModel.setPassword(user.encodedPassword());
		userModel.setLoginRequired(loginRequired);
		user.algorithm().ifPresent( algorithm -> userModel.setAlgorithm(algorithm));
		return userModel;
	}

}
