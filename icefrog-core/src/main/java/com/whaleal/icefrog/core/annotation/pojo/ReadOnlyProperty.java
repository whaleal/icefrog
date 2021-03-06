/*
 * Copyright 2014-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.whaleal.icefrog.core.annotation.pojo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Marks a field to be {@literal read-only} for the mapping framework and therefore will not be persisted.
 * <p>
 * 用于标记字段为只读字段，因此在 映射框架中它也不会被持久化。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {FIELD, METHOD, ANNOTATION_TYPE})
public @interface ReadOnlyProperty {
}
