package de.mq.iot2.main.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.util.FileCopyUtils;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.protocol.ProtocolService;

class ExportImportBatchImplTest {

	private final CalendarService calendarService = mock(CalendarService.class);
	private final ConfigurationService configurationService = mock(ConfigurationService.class);
	private final ProtocolService protocolService = mock(ProtocolService.class);
	private final ExportImportBatchImpl exportImportBatch = new ExportImportBatchImpl(calendarService, configurationService,protocolService);
	private final String csvContent = "spalte1;...;spalte10";

	@Test
	void exportCalendar() throws IOException {

		final File file = Mockito.mock(File.class);
		when(file.getAbsolutePath()).thenReturn("path");
		doAnswer(answer -> {
			answer.getArgument(0, OutputStream.class).write(csvContent.getBytes());
			return null;
		}).when(calendarService).export(any());

		try (final MockedStatic<FileCopyUtils> util = mockStatic(FileCopyUtils.class)) {

			exportImportBatch.exportCalendar(file);

			util.verify(() -> FileCopyUtils.copy((byte[]) argThat(arg -> csvContent.equals(new String((byte[]) arg))), (File) argThat(arg -> arg.equals(file))));
		}
	}

	@Test
	void exportConfiguration() throws IOException {

		final File file = Mockito.mock(File.class);
		when(file.getAbsolutePath()).thenReturn("path");
		doAnswer(answer -> {
			answer.getArgument(0, OutputStream.class).write(csvContent.getBytes());
			return null;
		}).when(configurationService).export(any());

		try (final MockedStatic<FileCopyUtils> util = mockStatic(FileCopyUtils.class)) {

			exportImportBatch.exportConfiguration(file);

			util.verify(() -> FileCopyUtils.copy((byte[]) argThat(arg -> csvContent.equals(new String((byte[]) arg))), (File) argThat(arg -> arg.equals(file))));
		}
	}
	
	@Test
	void exportProtocol() throws IOException {

		final File file = Mockito.mock(File.class);
		when(file.getAbsolutePath()).thenReturn("path");
		doAnswer(answer -> {
			answer.getArgument(0, OutputStream.class).write(csvContent.getBytes());
			return null;
		}).when(protocolService).export(any());

		try (final MockedStatic<FileCopyUtils> util = mockStatic(FileCopyUtils.class)) {

			exportImportBatch.exportProtocol(file);

			util.verify(() -> FileCopyUtils.copy((byte[]) argThat(arg -> csvContent.equals(new String((byte[]) arg))), (File) argThat(arg -> arg.equals(file))));
		}
	}

	@Test
	void importCalendar() throws IOException {
		final File file = Mockito.mock(File.class);
		when(file.getAbsolutePath()).thenReturn("path");
		Mockito.doAnswer(x -> {
			assertEquals(csvContent, inputStream(x.getArgument(0, InputStream.class)));
			return null;
		}).when(calendarService).importCsv(Mockito.any());
		try (final MockedStatic<FileCopyUtils> util = mockStatic(FileCopyUtils.class)) {
			util.when(() -> FileCopyUtils.copyToByteArray(file)).thenReturn(csvContent.getBytes());
			
			exportImportBatch.importCalendar(file);
			
			util.verify(() -> FileCopyUtils.copyToByteArray(file));
		}
		verify(calendarService).importCsv(Mockito.any());
	}

	// check exceptions are shit, the most biggest shit ever !!!
	private String inputStream(final InputStream arg) {
		try {

			return new String(arg.readAllBytes());
		} catch (final IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	@Test
	void importConfiguration() throws IOException {
		final File file = Mockito.mock(File.class);
		when(file.getAbsolutePath()).thenReturn("path");
		Mockito.doAnswer(x -> {
			assertEquals(csvContent, inputStream(x.getArgument(0, InputStream.class)));
			return null;
		}).when(configurationService).importCsv(Mockito.any());
		try (final MockedStatic<FileCopyUtils> util = mockStatic(FileCopyUtils.class)) {
			util.when(() -> FileCopyUtils.copyToByteArray(file)).thenReturn(csvContent.getBytes());
			
			exportImportBatch.importConfiguration(file);
			
			util.verify(() -> FileCopyUtils.copyToByteArray(file));
		}
		verify(configurationService).importCsv(Mockito.any());
	}
	
	@Test
	void importProtocol() throws IOException {
		final File file = Mockito.mock(File.class);
		when(file.getAbsolutePath()).thenReturn("path");
		Mockito.doAnswer(x -> {
			assertEquals(csvContent, inputStream(x.getArgument(0, InputStream.class)));
			return null;
		}).when(protocolService).importCsv(Mockito.any());
		try (final MockedStatic<FileCopyUtils> util = mockStatic(FileCopyUtils.class)) {
			util.when(() -> FileCopyUtils.copyToByteArray(file)).thenReturn(csvContent.getBytes());
			
			exportImportBatch.importProtocol(file);
			
			util.verify(() -> FileCopyUtils.copyToByteArray(file));
		}
		verify(protocolService).importCsv(Mockito.any());
	}
	
	@Test
	void deleteCalendarAndConfiguration() {
		
		final boolean removeConfigurationCalled[] = {false};
		doAnswer(answer -> {
			assertTrue(removeConfigurationCalled[0]);
			return null;
		}).when(calendarService).removeCalendar();
		doAnswer(answer -> {
			removeConfigurationCalled[0]=true;
			return null;
		}).when(configurationService).removeConfigurations();
		
		exportImportBatch.deleteCalendarAndConfigurations();
		
		assertTrue(removeConfigurationCalled[0]);
		
		verify(calendarService, times(1)).removeCalendar();
		verify(configurationService, times(1)).removeConfigurations();
	}
	
	@Test
	void deleteConfiguration() {
		exportImportBatch.deleteConfigurations();
		
		verify(configurationService, times(1)).removeConfigurations();
	}

}
