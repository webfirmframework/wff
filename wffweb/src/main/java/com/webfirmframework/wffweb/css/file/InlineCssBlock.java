/*
 * Copyright 2014-2019 Web Firm Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * @author WFF
 */
package com.webfirmframework.wffweb.css.file;

import java.util.Set;
import java.util.function.Consumer;

import com.webfirmframework.wffweb.css.core.CssProperty;

/**
 *
 * @author WFF
 * @since 3.0.8
 */
public final class InlineCssBlock extends AbstractCssFileBlock {

    private static final long serialVersionUID = 1_0_0L;

    private Consumer<Set<CssProperty>> consumer;

    public InlineCssBlock(final String name) {
        super(name);
    }

    public InlineCssBlock setExclude(final boolean excludeCssBlock) {
        super.setExcludeCssBlock(excludeCssBlock);
        return this;
    }

    @Override
    protected void load(final Set<CssProperty> cssProperties) {
        if (consumer != null) {
            consumer.accept(cssProperties);
        }
    }

    public static InlineCssBlock load(final String selectors,
            final Consumer<Set<CssProperty>> consumer) {
        final InlineCssBlock inlineCssBlock = new InlineCssBlock(selectors);
        inlineCssBlock.consumer = consumer;
        return inlineCssBlock;
    }

}
