/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.diagnostics;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link FailureAnalysisReporter} that logs the failure analysis.
 *
 * @author Andy Wilkinson
 * @since 1.4.0
 */
public final class LoggingFailureAnalysisReporter implements FailureAnalysisReporter {

	private static final Log logger = LogFactory
			.getLog(LoggingFailureAnalysisReporter.class);

	@Override
	public void report(FailureAnalysis failureAnalysis) {
		if (logger.isDebugEnabled()) {
			logger.debug("Application failed to start due to an exception",
					failureAnalysis.getCause());
		}
		if (logger.isErrorEnabled()) {
			logger.error(failureAnalysis.buildMessage());
		}
	}

}