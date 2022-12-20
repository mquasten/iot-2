package de.mq.iot2.calendar.support;

import org.springframework.stereotype.Component;

import de.mq.iot2.calendar.Day;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;

@Component
class DayMapper implements  ModelMapper<Day<?>, DayModel> {



	@Override
	public DayModel toWeb(final Day<?> day) {
		final var dayModel = new DayModel();
		dayModel.setId(IdUtil.getId(day));
		dayModel.setValue(day.value());
		day.description().ifPresent(dayModel::setDescription);
		return dayModel;
	}

}
