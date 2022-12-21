package de.mq.iot2.support;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.stereotype.Component;

@Component
public interface LocaleContextRepository {

	@Lookup
	LocaleContext localeContext();
}
