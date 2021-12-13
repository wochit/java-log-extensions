/*
 * Copyright 2019. New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.newrelic.logging.core;

import java.util.Arrays;

public class StackTraceTest {

    static class ShortStackException extends Throwable {
        @Override
        public StackTraceElement[] getStackTrace() {
            return Arrays.copyOfRange(super.getStackTrace(), 0, 1);
        }
    }
}
