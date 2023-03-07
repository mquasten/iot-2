package de.mq.iot2.main.support;

import java.io.File;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.support.DataAccessUtils;

class ExportImportBatchArgumentConverterImpl implements Converter<List<String>, Object[]> {

	@Override
	public Object[] convert(List<String> objects) {
		
		return new Object[] {new File(DataAccessUtils.requiredSingleResult(objects))};
	}

}
