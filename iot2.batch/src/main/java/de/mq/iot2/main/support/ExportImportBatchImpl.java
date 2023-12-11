package de.mq.iot2.main.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import de.mq.iot2.calendar.CalendarService;
import de.mq.iot2.configuration.ConfigurationService;
import de.mq.iot2.protocol.ProtocolService;

@Service
class ExportImportBatchImpl {
	private static Logger LOGGER = LoggerFactory.getLogger(ExportImportBatchImpl.class);
	private final CalendarService calendarService;
	private final ConfigurationService configurationService;
	private final ProtocolService protocolService;

	ExportImportBatchImpl(final CalendarService calendarService, final ConfigurationService configurationService, final ProtocolService protocolService) {
		this.calendarService = calendarService;
		this.configurationService = configurationService;
		this.protocolService = protocolService;
	}

	@BatchMethod(value = "export-calendar", converterClass = ExportImportBatchArgumentConverterImpl.class)
	void exportCalendar(final File file) throws IOException {
		LOGGER.info("Start export calendar, file: {}.", file.getAbsolutePath());
		try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			calendarService.export(os);
			FileCopyUtils.copy(os.toByteArray(), file);
		}
		LOGGER.info("Export calendar finished.");
	}

	@BatchMethod(value = "export-configuration", converterClass = ExportImportBatchArgumentConverterImpl.class)
	void exportConfiguration(final File file) throws IOException {
		LOGGER.info("Start export configuration, file: {}.", file.getAbsolutePath());
		try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			configurationService.export(os);
			FileCopyUtils.copy(os.toByteArray(), file);
		}
		LOGGER.info("Export configuration finished.");
	}
	
	@BatchMethod(value = "export-protocol", converterClass = ExportImportBatchArgumentConverterImpl.class)
	void exportProtocol(final File file) throws IOException {
		LOGGER.info("Start export protocol, file: {}.", file.getAbsolutePath());
		try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			protocolService.export(os);
			FileCopyUtils.copy(os.toByteArray(), file);
		}
		LOGGER.info("Export protocol finished.");
	}

	@BatchMethod(value = "import-calendar", converterClass = ExportImportBatchArgumentConverterImpl.class)
	void importCalendar(final File file) throws IOException {
		LOGGER.info("Start import calendar, file: {}.", file.getAbsolutePath());
		final byte[] data = FileCopyUtils.copyToByteArray(file);
		try (InputStream is = new ByteArrayInputStream(data)) {
			calendarService.importCsv(is);
		}
		LOGGER.info("Import calendar finished.");
	}

	@BatchMethod(value = "import-configuration", converterClass = ExportImportBatchArgumentConverterImpl.class)
	void importConfiguration(final File file) throws IOException {
		LOGGER.info("Start import configuration, file: {}.", file.getAbsolutePath());
		final byte[] data = FileCopyUtils.copyToByteArray(file);
		try (final InputStream is = new ByteArrayInputStream(data)) {
			configurationService.importCsv(is);
		}
		LOGGER.info("Import Configuration finished.");
	}

	@BatchMethod(value = "delete-calendar-and-configurations", converterClass = NoArgumentConverterImpl.class)
	void deleteCalendarAndConfigurations() {
		LOGGER.info("Delete calendar and configurations.");
		configurationService.removeConfigurations();
		calendarService.removeCalendar();
		LOGGER.info("Delete calendar and configurations finished.");
	}
	
	@BatchMethod(value = "delete-configurations", converterClass = NoArgumentConverterImpl.class)
	void deleteConfigurations()  {
		LOGGER.info("Delete configurations.");
		configurationService.removeConfigurations();
		LOGGER.info("Delete configurations finished.");
	}

}
