package de.mq.iot2.calendar.support;

import static de.mq.iot2.calendar.support.CalendarServiceImp.DAY_GROUP_NOT_FOUND_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import de.mq.iot2.calendar.Cycle;
import de.mq.iot2.calendar.DayGroup;
import de.mq.iot2.support.IdUtil;
import jakarta.persistence.EntityNotFoundException;

class DayGroupMapperTest {

	private final Cycle cycle = new CycleImpl(randomLong(), randomString(), 0);
	private final DayGroupRepository dayGroupRepository = mock(DayGroupRepository.class);
	private final DayGroupMapper dayGroupMapper = new DayGroupMapper(dayGroupRepository);
	private final DayGroup dayGroup = new DayGroupImpl(cycle, randomLong(), randomString(), true);

	private String randomString() {
		return UUID.randomUUID().toString();
	}

	@Test
	void toWeb() {
		final DayGroupModel dayGroupModel = dayGroupMapper.toWeb(dayGroup);

		assertEquals(IdUtil.getId(dayGroup), dayGroupModel.getId());
		assertEquals(dayGroup.name(), dayGroupModel.getName());
		assertEquals(IdUtil.getId(cycle), dayGroupModel.getCycleId());
		assertTrue(dayGroupModel.isReadonly());
	}

	private Long randomLong() {
		return Long.valueOf((long) (Math.random() * 1e12));
	}

	@Test
	void toDomain() {
		doReturn(Optional.of(dayGroup)).when(dayGroupRepository).findById(IdUtil.getId(dayGroup));

		assertEquals(dayGroup, dayGroupMapper.toDomain(IdUtil.getId(dayGroup)));
	}

	@Test
	void toDomainNotFound() {
		doReturn(Optional.empty()).when(dayGroupRepository).findById(IdUtil.getId(dayGroup));
		assertEquals(String.format(DAY_GROUP_NOT_FOUND_MESSAGE, IdUtil.getId(dayGroup)),
				assertThrows(EntityNotFoundException.class, () -> dayGroupMapper.toDomain(IdUtil.getId(dayGroup))).getMessage());
	}

}
