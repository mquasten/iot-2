package de.mq.iot2.calendar.support;

import org.springframework.stereotype.Component;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.ModelMapper;

@Component
class CycleMapper implements ModelMapper<Cycle, CycleModel> {
	
	@Override
	public CycleModel toWeb(final Cycle cycle) {
		final var cycleModel = new CycleModel();
		cycleModel.setId(IdUtil.getId(cycle));
		cycleModel.setName(cycle.name());
		return cycleModel;
	}

}