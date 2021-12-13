/*
 * Copyright 2019 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.newrelic.logging.core;

public class ElementName {
    public static final String MESSAGE = "message";
    public static final String MESSAGE_PARAMETERS = "message.parameters";
    public static final String MACHINE_IP = "machine.ip";
    public static final String TIMESTAMP = "timestamp";
    public static final String THREAD_NAME = "thread.name";
    public static final String LOG_LEVEL = "log.level";
    public static final String LOGGER_NAME = "logger.name";
    public static final String CLASS_NAME = "class.name";
    public static final String METHOD_NAME = "method.name";
    public static final String LINE_NUMBER = "line.number";
    public static final String ERROR_MESSAGE = "error.message";
    public static final String ERROR_CLASS = "error.class";
    public static final String ERROR_STACK = "error.stack";

    private ElementName() {}
}
