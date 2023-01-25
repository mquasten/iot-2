package de.mq.iot2.user.support;
import org.springframework.stereotype.Component;
import de.mq.iot2.support.ModelMapper;
import de.mq.iot2.user.User;

@Component
class UserModelMapper implements   ModelMapper<User, UserModel> {
	
	@Override
	public UserModel toWeb(final User user) {
		final UserModel userModel = new UserModel();
		userModel.setName(user.name());
		userModel.setPassword(user.encodedPassword());
		user.algorithm().ifPresent( algorithm -> userModel.setAlgorithm(algorithm));
		return userModel;
	}

}
