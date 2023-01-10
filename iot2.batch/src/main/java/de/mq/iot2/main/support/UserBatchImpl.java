package de.mq.iot2.main.support;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.mq.iot2.user.UserService;

@Service
public class UserBatchImpl {
	private static Logger LOGGER = LoggerFactory.getLogger(EndOfDayBatchImpl.class);
	
	private final UserService userService;
	
	UserBatchImpl(final UserService userService) {
		this.userService=userService;
	}
	
	@BatchMethod(value = "update-user", converterClass = UserBatchImplUpdateUserArgumentConverterImpl.class)
	final void updateUser(final String name, final String rawPassword, final String algorithm) {
		updateUser(name, rawPassword, Optional.ofNullable(StringUtils.hasText(algorithm) ? algorithm:null));
	}

	private void updateUser(final String name, final String rawPassword, final Optional<String> algorithm) {
		userService.update(name, rawPassword, algorithm);
		LOGGER.info("User {} stored.", name);
	}
	
	@BatchMethod(value = "delete-user", converterClass = UserBatchImplDeleteUserArgumentConverterImpl.class)
	final void deleteUser(final String name) {
		Assert.hasText(name, "Name is required.");
		if ( userService.delete(name) ) {
			LOGGER.info("User {} deleted.", name);
		} else {
			LOGGER.warn("User {} not found.", name);
		}
	}

}

class UserBatchImplUpdateUserArgumentConverterImpl implements Converter<List<String>, Object[]> {
	static final String INVALID_NUMBER_OF_PARAMETERS_MESSAGE_UPDATE_USER = "UserBatchImpl.updateUser has 2 mandatory and 1 optional Parameter.";

	@Override
	final public Object[] convert(final List<String> objects) {
		Assert.isTrue(objects.size() <= 3 && objects.size() >= 2, INVALID_NUMBER_OF_PARAMETERS_MESSAGE_UPDATE_USER);
		if (objects.size() == 3) {
			return objects.toArray(new String[objects.size()]);
		} else {
			return new String[] { objects.get(0), objects.get(1), null };
		}

	}

}

class UserBatchImplDeleteUserArgumentConverterImpl implements Converter<List<String>, Object[]> {
	static final String INVALID_NUMBER_OF_PARAMETERS_MESSAGE_DELETE_USER = "UserBatchImpl.deleteUser has 1 mandatory Parameter.";

	@Override
	final public Object[] convert(final List<String> objects) {
		Assert.isTrue(objects.size() ==1 , INVALID_NUMBER_OF_PARAMETERS_MESSAGE_DELETE_USER);
		return objects.toArray(new String[objects.size()]);
	}

}