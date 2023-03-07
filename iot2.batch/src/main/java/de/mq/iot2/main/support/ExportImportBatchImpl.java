package de.mq.iot2.main.support;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;


import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.configuration.ConfigurationService;

@Service
class ExportImportBatchImpl {
	private static Logger LOGGER = LoggerFactory.getLogger(ExportImportBatchImpl.class);
	private final CalendarService calendarService;
	final ConfigurationService configurationService;

	ExportImportBatchImpl(final CalendarService calendarService, final ConfigurationService configurationService) {
		this.calendarService = calendarService;
		this.configurationService=configurationService;
	}
	
	@BatchMethod(value = "export-calendar", converterClass = ExportImportBatchArgumentConverterImpl.class)
	void exportCalendar(final File file) throws IOException {
		LOGGER.info("Start export calendar, file: {}.", file.getAbsolutePath());
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		calendarService.export(os);
		FileCopyUtils.copy(os.toByteArray(), file);
		LOGGER.info("Export calendar finished.");
	}
	
	@BatchMethod(value = "export-configuration", converterClass = ExportImportBatchArgumentConverterImpl.class)
	void exportConfiguration(final File file) throws IOException {
		LOGGER.info("Start export configuration, file: {}.", file.getAbsolutePath());
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		configurationService.export(os);
		FileCopyUtils.copy(os.toByteArray(), file);
		LOGGER.info("Export configuration finished.");
	}
	

}
