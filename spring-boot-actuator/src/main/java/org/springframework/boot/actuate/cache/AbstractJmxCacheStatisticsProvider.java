/*
 * Copyright 2012-2015 the original author or authors.
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

package org.springframework.boot.actuate.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * Base {@link CacheStatisticsProvider} implementation that uses JMX to retrieve the cache
 * statistics.
 *
 * @param <C> The cache type
 * @author Stephane Nicoll
 * @since 1.3.0
 */
public abstract class AbstractJmxCacheStatisticsProvider<C extends Cache>
		implements CacheStatisticsProvider<C> {

	protected static final Logger logger = LoggerFactory
			.getLogger(AbstractJmxCacheStatisticsProvider.class);

	private Map<String, ObjectNameWrapper> caches = new ConcurrentHashMap<String, ObjectNameWrapper>();

	@Override
	public CacheStatistics getCacheStatistics(CacheManager cacheManager, C cache) {
		try {
			ObjectName objectName = internalGetObjectName(cache);
			return (objectName == null ? null : getCacheStatistics(objectName));
		}
		catch (MalformedObjectNameException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Return the {@link ObjectName} of the MBean that is managing the specified cache or
	 * {@code null} if none is found.
	 * @param cache the cache to handle
	 * @return the object name of the cache statistics MBean
	 * @throws MalformedObjectNameException if the {@link ObjectName} for that cache is
	 * invalid
	 */
	protected abstract ObjectName getObjectName(C cache)
			throws MalformedObjectNameException;

	/**
	 * Return the current {@link CacheStatistics} snapshot from the MBean identified by
	 * the specified {@link ObjectName}.
	 * @param objectName the object name of the cache statistics MBean
	 * @return the current cache statistics
	 */
	protected abstract CacheStatistics getCacheStatistics(ObjectName objectName);

	private ObjectName internalGetObjectName(C cache)
			throws MalformedObjectNameException {
		String cacheName = cache.getName();
		ObjectNameWrapper value = this.caches.get(cacheName);
		if (value != null) {
			return value.objectName;
		}
		ObjectName objectName = getObjectName(cache);
		this.caches.put(cacheName, new ObjectNameWrapper(objectName));
		return objectName;
	}

	private static class ObjectNameWrapper {

		private final ObjectName objectName;

		ObjectNameWrapper(ObjectName objectName) {
			this.objectName = objectName;
		}

	}

}
