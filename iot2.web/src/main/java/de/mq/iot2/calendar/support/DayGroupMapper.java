package de.mq.iot2.calendar.support;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;
import jakarta.persistence.EntityNotFoundException;

@Component
class DayGroupMapper implements ModelMapper<DayGroup, DayGroupModel> {

	private final DayGroupRepository dayGroupRepository;
	
	DayGroupMapper(final DayGroupRepository dayGroupRepository) {
		this.dayGroupRepository = dayGroupRepository;
	}

	@Override
	public DayGroupModel toWeb(final DayGroup dayGroup) {
		Assert.notNull(dayGroup, "DayGroup is required.");
		final var dayGroupModel = new DayGroupModel();
		dayGroupModel.setName(dayGroup.name());
		dayGroupModel.setId(IdUtil.getId(dayGroup));
		dayGroupModel.setReadonly(dayGroup.readOnly());
		dayGroupModel.setCycleId(IdUtil.getId(dayGroup.cycle()));
		return dayGroupModel;
	}
	@Override
	public DayGroup toDomain(final String id) {
		Assert.notNull(id , "Id is required.");
		return dayGroupRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(String.format(CalendarServiceImp.DAY_GROUP_NOT_FOUND_MESSAGE, id)));
	}

}
