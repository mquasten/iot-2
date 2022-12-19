package de.mq.iot2.calendar.support;

import org.springframework.stereotype.Component;

import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;

@Component
class DayGroupMapper implements ModelMapper<DayGroup, DayGroupModel> {

	@Override
	public DayGroupModel toWeb(final DayGroup dayGroup) {
		final var dayGroupModel = new DayGroupModel();
		dayGroupModel.setName(dayGroup.name());
		dayGroupModel.setId(IdUtil.getId(dayGroup));
		dayGroupModel.setReadonly(dayGroup.readOnly());
		dayGroupModel.setCycleId(IdUtil.getId(dayGroup.cycle()));
		return dayGroupModel;
	}

}
