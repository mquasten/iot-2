package de.mq.iot2.calendar.support;

import org.springframework.stereotype.Component;

import de.mq.iot2.calendar.Day;
import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.LocaleContextRepository;
import de.mq.iot2.support.ModelMapper;

@Component
class DayMapper implements  ModelMapper<Day<?>, DayModel> {

	private final LocaleContextRepository localeContextRepository;

	DayMapper(final LocaleContextRepository localeContextRepository) {
		this.localeContextRepository = localeContextRepository;
	}

	@Override
	public DayModel toWeb(final Day<?> day) {
		System.out.println( "?"+ localeContextRepository.localeContext().getLocale());
		final var dayModel = new DayModel();
		dayModel.setId(IdUtil.getId(day));
		dayModel.setValue(day.value());
		day.description().ifPresent(dayModel::setDescription);
		return dayModel;
	}


	

	
	
	
	

	


	

	

	

	
	
		

}
