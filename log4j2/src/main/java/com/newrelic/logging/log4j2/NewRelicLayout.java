/*
 * Copyright 2019 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.newrelic.logging.log4j2;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.newrelic.logging.core.ElementName;
import com.newrelic.logging.core.ExceptionUtil;
import com.newrelic.logging.core.IPResolveHelper;
import com.newrelic.logging.core.MessageParser;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * A {@link Layout} that writes the New Relic JSON format.
 *
 * This layout must be added to an {@link AbstractAppender} via {@link AbstractAppender.Builder#withLayout(Layout)} or XML.
 * The New Relic layout has specific fields and field names and has no customizable elements. To configure,
 * update your logging config xml like this:
 *
 * <pre>{@code
 *     <File name="MyFile" fileName="logs/app-log-file.log">
 *         <NewRelicLayout/>
 *     </File>
 * }</pre>
 *
 * @see <a href="https://logging.apache.org/log4j/2.x/manual/appenders.html#FileAppender">The FileAppender, for example</a>
 */
@Plugin(name = NewRelicLayout.PLUGIN_NAME, category = "Core", elementType = Layout.ELEMENT_TYPE)
public class NewRelicLayout extends AbstractStringLayout {
    static final String PLUGIN_NAME = "NewRelicLayout";
    private ObjectMapper mapper = new ObjectMapper();

    @PluginFactory
    public static NewRelicLayout factory() {
        return new NewRelicLayout(StandardCharsets.UTF_8);
    }

    private NewRelicLayout(Charset charset) {
        super(charset);
    }

    @Override
    public String toSerializable(LogEvent event) {
        StringWriter sw = new StringWriter();

        try (JsonGenerator generator = new JsonFactory().createGenerator(sw)) {
            writeToGenerator(event, generator);
        } catch (IOException e) {
            return e.toString();
        }

        return sw.toString() + "\n";
    }

    private void writeToGenerator(LogEvent event, JsonGenerator generator) throws IOException {
        generator.writeStartObject();
        generator.writeObjectField(ElementName.MESSAGE, event.getMessage().getFormattedMessage());
        generator.writeObjectField(ElementName.TIMESTAMP, event.getTimeMillis());
        generator.writeObjectField(ElementName.THREAD_NAME, event.getThreadName());
        generator.writeObjectField(ElementName.LOG_LEVEL, event.getLevel().toString());
        generator.writeObjectField(ElementName.LOGGER_NAME, event.getLoggerName());
        generator.writeObjectField(ElementName.MACHINE_IP, IPResolveHelper.getMachineIp());
        StringWriter writer = new StringWriter();
        mapper.writeValue(writer, MessageParser.getMessageParameters(event.getMessage().getFormattedMessage()));
        generator.writeObjectField(ElementName.MESSAGE_PARAMETERS, writer.toString());
        if (event.isIncludeLocation() && event.getSource() != null) {
            generator.writeObjectField(ElementName.CLASS_NAME, event.getSource().getClassName());
            generator.writeObjectField(ElementName.METHOD_NAME, event.getSource().getMethodName());
            generator.writeObjectField(ElementName.LINE_NUMBER, event.getSource().getLineNumber());
        }

        Map<String, String> map = event.getContextData().toMap();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getKey().startsWith(NewRelicContextDataProvider.NEW_RELIC_PREFIX)) {
                    String key = entry.getKey().substring(NewRelicContextDataProvider.NEW_RELIC_PREFIX.length());
                    generator.writeStringField(key , entry.getValue());
                }
            }
        }

        Throwable throwable = event.getThrown();
        if (throwable != null) {
            generator.writeObjectField(ElementName.ERROR_CLASS, throwable.getClass().getName());
            generator.writeObjectField(ElementName.ERROR_MESSAGE, throwable.getMessage());
            generator.writeObjectField(ElementName.ERROR_STACK, ExceptionUtil.getErrorStack(throwable));
        }

        generator.writeEndObject();
    }
}
