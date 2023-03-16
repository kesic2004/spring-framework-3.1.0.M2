/*
 * Copyright 2002-2011 the original author or authors.
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

package org.springframework.web.method.annotation.support;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;

/**
 * A base abstract class to resolve method arguments annotated with @{@link CookieValue}. Subclasses must define how
 * to extract the cookie value from the request.
 * 
 * <p>An @{@link CookieValue} is a named value that is resolved from a cookie. It has a required flag and a 
 * default value to fall back on when the cookie does not exist. See the base class 
 * {@link AbstractNamedValueMethodArgumentResolver} for more information on how named values are processed.
 * 
 * <p>A {@link WebDataBinder} is invoked to apply type conversion to resolved cookie values that don't yet match 
 * the method parameter type.
 * 
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public abstract class AbstractCookieValueMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

	/**
	 * @param beanFactory a bean factory to use for resolving  ${...} placeholder and #{...} SpEL expressions 
	 * in default values, or {@code null} if default values are not expected to contain expressions
	 */
	public AbstractCookieValueMethodArgumentResolver(ConfigurableBeanFactory beanFactory) {
		super(beanFactory);
	}

	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CookieValue.class);
	}

	@Override
	protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
		CookieValue annotation = parameter.getParameterAnnotation(CookieValue.class);
		return new CookieValueNamedValueInfo(annotation);
	}

	@Override
	protected void handleMissingValue(String cookieName, MethodParameter parameter) {
		String paramTypeName = parameter.getParameterType().getName();
		throw new IllegalStateException(
				"Missing cookie named '" + cookieName + "' for method parameter type [" + paramTypeName + "]");
	}

	private static class CookieValueNamedValueInfo extends NamedValueInfo {

		private CookieValueNamedValueInfo(CookieValue annotation) {
			super(annotation.value(), annotation.required(), annotation.defaultValue());
		}
	}
}