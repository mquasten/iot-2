package de.mq.iot2.main.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.util.FileCopyUtils;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.configuration.ConfigurationService;
class ExportImportBatchImplTest {

	private final CalendarService calendarService = mock(CalendarService.class);
	private final ConfigurationService configurationService = mock(ConfigurationService.class);
	private final ExportImportBatchImpl exportBatch = new ExportImportBatchImpl(calendarService, configurationService);

	@Test
	void exportCalendar() throws IOException {
		final var csvContent = "spalte1;...;spalte10";
		final File file = Mockito.mock(File.class);
		when(file.getAbsolutePath()).thenReturn("path");
		doAnswer(answer -> {
			answer.getArgument(0, OutputStream.class).write(csvContent.getBytes());
			return null;
		}).when(calendarService).export(any());
		
		try (final MockedStatic<FileCopyUtils> util = mockStatic(FileCopyUtils.class)) {
			
			exportBatch.exportCalendar(file);
			
			util.verify(() -> FileCopyUtils.copy((byte[]) argThat(arg -> csvContent.equals(new String((byte[]) arg))), (File) argThat(arg -> arg.equals(file))));
		}
	}
	
	@Test
	void exportConfiguration() throws IOException {
		final var csvContent = "spalte1;...;spalte7";
		final File file = Mockito.mock(File.class);
		when(file.getAbsolutePath()).thenReturn("path");
		doAnswer(answer -> {
			answer.getArgument(0, OutputStream.class).write(csvContent.getBytes());
			return null;
		}).when(configurationService).export(any());
		
		try (final MockedStatic<FileCopyUtils> util = mockStatic(FileCopyUtils.class)) {
			
			exportBatch.exportConfiguration(file);
			
			util.verify(() -> FileCopyUtils.copy((byte[]) argThat(arg -> csvContent.equals(new String((byte[]) arg))), (File) argThat(arg -> arg.equals(file))));
		}
	}

}
