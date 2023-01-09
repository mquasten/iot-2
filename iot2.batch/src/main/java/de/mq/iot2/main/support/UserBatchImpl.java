package de.mq.iot2.main.support;

import java.util.List;
import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserBatchImpl {
	@BatchMethod(value = "update-user", converterClass = UserBatchImplUpdateUserArgumentConverterImpl.class)
	final void updateUser(final String name, final String rawPassword, final String algorithm) {
		updateUser(name, rawPassword, Optional.ofNullable(algorithm));
	}

	private void updateUser(final String name, final String rawPassword, final Optional<String> algorithm) {

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