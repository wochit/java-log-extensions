/*
 * Copyright 2019 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.newrelic.logging.dropwizard;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.LayoutBase;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.common.ConsoleAppenderFactory;
import io.dropwizard.logging.common.async.AsyncAppenderFactory;
import io.dropwizard.logging.common.layout.LayoutFactory;

@JsonTypeName("newrelic-console")
public class NewRelicConsoleAppenderFactory extends ConsoleAppenderFactory<ILoggingEvent> {
    @Override
    protected Appender<ILoggingEvent> wrapAsync(Appender<ILoggingEvent> appender, AsyncAppenderFactory<ILoggingEvent> asyncAppenderFactory) {
        return super.wrapAsync(appender, new NewRelicAsyncAppenderFactory());
    }

    @Override
    protected Appender<ILoggingEvent> wrapAsync(Appender<ILoggingEvent> appender, AsyncAppenderFactory<ILoggingEvent> asyncAppenderFactory, Context context) {
        return super.wrapAsync(appender, new NewRelicAsyncAppenderFactory(), context);
    }

    @Override
    protected LayoutBase<ILoggingEvent> buildLayout(LoggerContext context, LayoutFactory<ILoggingEvent> defaultLayoutFactory) {
        if (layout instanceof LogFormatLayoutFactory) {
            ((LogFormatLayoutFactory)layout).setLogFormat(logFormat);
        }

        return super.buildLayout(context, defaultLayoutFactory);
    }
}
