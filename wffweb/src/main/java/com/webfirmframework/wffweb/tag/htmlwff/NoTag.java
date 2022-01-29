/*
 * Copyright 2014-2022 Web Firm Framework
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
package com.webfirmframework.wffweb.tag.htmlwff;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.webfirmframework.wffweb.MethodNotImplementedException;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.SharedTagContent;
import com.webfirmframework.wffweb.tag.html.TagEvent;
import com.webfirmframework.wffweb.tag.html.URIStateSwitch;
import com.webfirmframework.wffweb.tag.html.SharedTagContent.ContentFormatter;
import com.webfirmframework.wffweb.util.StringUtil;

/**
 * It's a tag which makes child content without any opening closing tag. <br>
 * <br>
 * NB: Nesting NoTag is not recommended and it may cause strange behavior. We
 * recommend TagContent enum for NoTag insertion in a tag instead of creating
 * new NoTag object. insertBefore method in NoTag may not work as expected for
 * now so its use is not recommended.
 *
 * @author WFF
 * @since 1.0.0
 */
public class NoTag extends AbstractHtml {

    // TODO This class needs to be tested properly

    @Serial
    private static final long serialVersionUID = 1_0_0L;

    {
        init();
    }

    /**
     *
     * @param base     i.e. parent tag of this tag
     * @param children An array of {@code AbstractHtml}
     *
     * @since 1.0.0
     */
    public NoTag(final AbstractHtml base, final AbstractHtml... children) {
        super(base, children);
    }

    /**
     *
     * @param base     i.e. parent tag of this tag
     * @param children An array of {@code AbstractHtml}
     *
     * @since 1.0.0
     */
    public NoTag(final AbstractHtml base, final Collection<? extends AbstractHtml> children) {
        super(base, children);
    }

    /**
     *
     * @param base         i.e. parent tag of this tag
     * @param childContent
     *
     * @since 1.0.0
     */
    public NoTag(final AbstractHtml base, final String childContent) {
        super(base, childContent, false);
    }

    /**
     *
     * @param base            i.e. parent tag of this tag
     * @param childContent
     * @param contentTypeHtml true if the given childContent is HTML. by default it
     *                        is false.
     * @since 3.0.2
     */
    public NoTag(final AbstractHtml base, final String childContent, final boolean contentTypeHtml) {
        super(base, childContent, contentTypeHtml);
    }

    /**
     * invokes only once per object
     *
     * @author WFF
     * @since 1.0.0
     */
    protected void init() {
        // to override and use this method
    }

    /**
     * adds {@code AbstractHtml}s as children.
     *
     * @param children
     * @since 1.0.0
     * @author WFF
     */
    public void addChildren(final List<AbstractHtml> children) {
        super.appendChildren(children);
    }

    /**
     * adds {@code AbstractHtml}s as children.
     *
     * @param child
     * @since 1.0.0
     * @author WFF
     */
    public void addChild(final AbstractHtml child) {
        super.appendChild(child);
    }

    /**
     * adds {@code AbstractHtml}s as children.
     *
     * @param children
     * @since 1.0.0
     * @author WFF
     */
    public void removeChildren(final List<AbstractHtml> children) {
        super.removeChildren(children);
    }

    /**
     * adds {@code AbstractHtml}s as children.
     *
     * @param child
     * @since 1.0.0
     * @author WFF
     */
    @Override
    public boolean removeChild(final AbstractHtml child) {
        return super.removeChild(child);
    }

    /**
     * removes the the child content.
     *
     * @param child
     * @since 1.0.0
     * @author WFF
     */
    public void removeChild(final String child) {
        final StringBuilder htmlMiddleSB = getHtmlMiddleSB();
        final String sb = htmlMiddleSB.toString();
        final String replaced = StringUtil.replace(sb, child, "");
        final int lastIndex = htmlMiddleSB.length() - 1;
        htmlMiddleSB.delete(0, lastIndex);
        htmlMiddleSB.append(replaced);
    }

    /**
     * adds {@code AbstractHtml}s as children.
     *
     * @param child
     * @since 1.0.0
     * @author WFF
     */
    public void addChild(final String child) {
        super.getChildren().add(new NoTag(this, child));
    }

    /**
     * gets child content
     *
     * @return
     * @since 1.0.0
     * @author WFF
     */
    public String getChildContent() {
        return getHtmlMiddleSB().toString();
    }

    /**
     * @return true if the child content is considered to be HTML
     * @since 3.0.2
     */
    public boolean isChildContentTypeHtml() {
        return noTagContentTypeHtml;
    }

    /**
     * @deprecated this method is not allowed in NoTag or Blank class.
     */
    @Deprecated
    @Override
    public boolean removeSharedTagContent(final boolean removeContent) {
        throw new MethodNotImplementedException(
                "sharedTagContent is not allowed in NoTag or Blank tag so calling removeSharedTagContent is invalid");
    }

    /**
     * @deprecated this method is not allowed in NoTag or Blank class.
     */
    @Deprecated
    @Override
    public <T> void subscribeTo(final boolean updateClient, final SharedTagContent<T> sharedTagContent,
            final ContentFormatter<T> formatter) {
        throw new MethodNotImplementedException("sharedTagContent is not allowed to apply in NoTag or Blank tag");
    }

    /**
     * @deprecated this method is not allowed in NoTag or Blank class.
     */
    @Deprecated
    @Override
    public <T> void addInnerHtml(final boolean updateClient, final SharedTagContent<T> sharedTagContent,
            final ContentFormatter<T> formatter) {
        throw new MethodNotImplementedException("sharedTagContent is not allowed to apply in NoTag or Blank tag");
    }

    /**
     * @deprecated this method is not allowed in NoTag or Blank class.
     */
    @Deprecated
    @Override
    public <T extends AbstractHtml> T give(final Consumer<T> consumer) {
        throw new MethodNotImplementedException("give is not allowed to use in NoTag or Blank tag");
    }

    /**
     * @deprecated this method is not allowed in NoTag or Blank class.
     */
    @Deprecated
    @Override
    public <R extends AbstractHtml, C> R give(final BiFunction<R, C, R> consumer, final C input) {
        throw new MethodNotImplementedException("give is not allowed to use in NoTag or Blank tag");
    }

    /**
     * @deprecated this method is not allowed in NoTag or Blank class.
     */
    @Deprecated(since = "12.0.0-beta.1")
    public URIStateSwitch whenURI(final Predicate<String> uriPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier) {
        throw new MethodNotImplementedException("This method is not allowed in NoTag");
    }

    /**
     * @deprecated this method is not allowed in NoTag or Blank class.
     */
    @Deprecated(since = "12.0.0-beta.1")
    public URIStateSwitch whenURI(final Predicate<String> uriPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final int index) {
        throw new MethodNotImplementedException("This method is not allowed in NoTag");
    }

    /**
     * @deprecated this method is not allowed in NoTag or Blank class.
     */
    @Deprecated(since = "12.0.0-beta.1")
    public URIStateSwitch whenURI(final Predicate<String> uriPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final Supplier<AbstractHtml[]> failTagsSupplier) {
        throw new MethodNotImplementedException("This method is not allowed in NoTag");
    }

    /**
     * @deprecated this method is not allowed in NoTag or Blank class.
     */
    @Deprecated(since = "12.0.0-beta.1")
    public URIStateSwitch whenURI(final Predicate<String> uriPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final Supplier<AbstractHtml[]> failTagsSupplier,
            final int index) {
        throw new MethodNotImplementedException("This method is not allowed in NoTag");
    }

    /**
     * @deprecated this method is not allowed in NoTag or Blank class.
     */
    @Deprecated(since = "12.0.0-beta.1")
    public URIStateSwitch whenURI(final Predicate<String> uriPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final Consumer<TagEvent> failConsumer,
            final int index) {
        throw new MethodNotImplementedException("This method is not allowed in NoTag");
    }

    /**
     * @deprecated this method is not allowed in NoTag or Blank class.
     */
    @Deprecated(since = "12.0.0-beta.1")
    public URIStateSwitch whenURI(final Predicate<String> uriPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final Consumer<TagEvent> failConsumer) {
        throw new MethodNotImplementedException("This method is not allowed in NoTag");
    }

    /**
     * @deprecated this method is not allowed in NoTag or Blank class.
     */
    @Deprecated(since = "12.0.0-beta.1")
    public URIStateSwitch whenURI(final Predicate<String> uriPredicate, final Consumer<TagEvent> successConsumer,
            final Supplier<AbstractHtml[]> failTagsSupplier, final int index) {
        throw new MethodNotImplementedException("This method is not allowed in NoTag");
    }

    /**
     * @deprecated this method is not allowed in NoTag or Blank class.
     */
    @Deprecated(since = "12.0.0-beta.1")
    public URIStateSwitch whenURI(final Predicate<String> uriPredicate, final Consumer<TagEvent> successConsumer,
            final Supplier<AbstractHtml[]> failTagsSupplier) {
        throw new MethodNotImplementedException("This method is not allowed in NoTag");
    }

}
