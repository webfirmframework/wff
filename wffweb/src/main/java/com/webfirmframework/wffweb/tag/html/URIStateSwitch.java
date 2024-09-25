/*
 * Copyright 2014-2024 Web Firm Framework
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
 */
package com.webfirmframework.wffweb.tag.html;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.webfirmframework.wffweb.common.URIEvent;
import com.webfirmframework.wffweb.server.page.BrowserPage;

/**
 * @since 12.0.0-beta.1
 *
 */
public sealed interface URIStateSwitch permits AbstractHtml {

    /**
     * Replaces the children of this tag with the tags supplied by
     * {@code successTagsSupplier} if the predicate test returns true otherwise
     * replaces the children with tags supplied by {@code failTagsSupplier} if no
     * further {@code whenURI} conditions exist and if the {@code failTagsSupplier}
     * is null the existing children of this tag will be removed. To remove the
     * whenURI actions from this tag, call
     * {@link AbstractHtml#removeURIChangeActions()} method. To get the current uri
     * inside the supplier object call {@link BrowserPage#getURI()}. This action
     * will be performed after initial client ping. You can call {@code whenURI}
     * multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     * <br>
     * Note: This method uses {@code null} for {@code failTagsSupplier}.
     *
     * @param uriEventPredicate   the predicate object to test, the argument of the
     *                            test method is the changed uri details, if the
     *                            test method returns true then the tags given by
     *                            {@code successTagsSupplier} will be added as inner
     *                            html to this tag. If test returns false, the tags
     *                            given by {@code failTagsSupplier} will be added as
     *                            * inner html to this tag and if the
     *                            {@code failTagsSupplier} is null the existing
     *                            children will be removed from this tag.
     * @param successTagsSupplier the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns true. If
     *                            {@code successTagsSupplier.get()} method returns
     *                            null, no action will be done on the tag.
     *
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier);

    /**
     * Replaces the children of this tag with the tags supplied by
     * {@code successTagsSupplier} if the predicate test returns true otherwise
     * replaces the children with tags supplied by {@code failTagsSupplier} if no
     * further {@code whenURI} conditions exist and if the {@code failTagsSupplier}
     * is null the existing children of this tag will be removed. To remove the
     * whenURI actions from this tag, call
     * {@link AbstractHtml#removeURIChangeActions()} method. To get the current uri
     * inside the supplier object call {@link BrowserPage#getURI()}. This action
     * will be performed after initial client ping. You can call {@code whenURI}
     * multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     * <br>
     * Note: This method uses {@code null} for {@code failTagsSupplier}.
     *
     * @param uriEventPredicate   the predicate object to test, the argument of the
     *                            test method is the changed uri details, if the
     *                            test method returns true then the tags given by
     *                            {@code successTagsSupplier} will be added as inner
     *                            html to this tag. If test returns false, the tags
     *                            given by {@code failTagsSupplier} will be added as
     *                            * inner html to this tag and if the
     *                            {@code failTagsSupplier} is null the existing
     *                            children will be removed from this tag.
     * @param successTagsSupplier the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns true. If
     *                            {@code successTagsSupplier.get()} method returns
     *                            null, no action will be done on the tag.
     *
     * @param index               the index to replace the existing action with
     *                            this. A value less than zero will add this
     *                            condition to the last.
     *
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final int index);

    /**
     * Replaces the children of this tag with the tags supplied by
     * {@code successTagsSupplier} if the predicate test returns true otherwise
     * replaces the children with tags supplied by {@code failTagsSupplier} if no
     * further {@code whenURI} conditions exist and if the {@code failTagsSupplier}
     * is null the existing children of this tag will be removed. To remove the
     * whenURI actions from this tag, call
     * {@link AbstractHtml#removeURIChangeActions()} method. To get the current uri
     * inside the supplier object call {@link BrowserPage#getURI()}. This action
     * will be performed after initial client ping. You can call {@code whenURI}
     * multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     *
     * @param uriEventPredicate   the predicate object to test, the argument of the
     *                            test method is the changed uri details, if the
     *                            test method returns true then the tags given by
     *                            {@code successTagsSupplier} will be added as inner
     *                            html to this tag. If test returns false, the tags
     *                            given by {@code failTagsSupplier} will be added as
     *                            * inner html to this tag and if the
     *                            {@code failTagsSupplier} is null the existing
     *                            children will be removed from this tag.
     * @param successTagsSupplier the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns true. If
     *                            {@code successTagsSupplier.get()} method returns
     *                            null, no action will be done on the tag.
     * @param failTagsSupplier    the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns false. If
     *                            {@code failTagsSupplier.get()} * method returns
     *                            null, no action will be done on the tag.
     *
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final Supplier<AbstractHtml[]> failTagsSupplier);

    /**
     * Replaces the children of this tag with the tags supplied by
     * {@code successTagsSupplier} if the predicate test returns true otherwise
     * replaces the children with tags supplied by {@code failTagsSupplier} if no
     * further {@code whenURI} conditions exist and if the {@code failTagsSupplier}
     * is null the existing children of this tag will be removed. To remove the
     * whenURI actions from this tag, call
     * {@link AbstractHtml#removeURIChangeActions()} method. To get the current uri
     * inside the supplier object call {@link BrowserPage#getURI()}. This action
     * will be performed after initial client ping. You can call {@code whenURI}
     * multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     *
     * @param uriEventPredicate   the predicate object to test, the argument of the
     *                            test method is the changed uri details, if the
     *                            test method returns true then the tags given by
     *                            {@code successTagsSupplier} will be added as inner
     *                            html to this tag. If test returns false, the tags
     *                            given by {@code failTagsSupplier} will be added as
     *                            * inner html to this tag and if the
     *                            {@code failTagsSupplier} is null the existing
     *                            children will be removed from this tag.
     * @param successTagsSupplier the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns true. If
     *                            {@code successTagsSupplier.get()} method returns
     *                            null, no action will be done on the tag.
     * @param failTagsSupplier    the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns false. If
     *                            {@code failTagsSupplier.get()} * method returns
     *                            null, no action will be done on the tag.
     * @param index               the index to replace the existing action with
     *                            this. A value less than zero will add this
     *                            condition to the last.
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final Supplier<AbstractHtml[]> failTagsSupplier,
            final int index);

    /**
     * Replaces the children of this tag with the tags supplied by
     * {@code successTagsSupplier} if the predicate test returns true otherwise
     * invokes {@code failConsumer} if no further {@code whenURI} conditions exist
     * and if the {@code failConsumer} is null the existing children of this tag
     * will be removed. To remove the whenURI actions from this tag, call
     * {@link AbstractHtml#removeURIChangeActions()} method. To get the current uri
     * inside the supplier object call {@link BrowserPage#getURI()}. This action
     * will be performed after initial client ping. You can call {@code whenURI}
     * multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     *
     * @param uriEventPredicate   the predicate object to test, the argument of the
     *                            test method is the changed uri details, if the
     *                            test method returns true then the tags given by
     *                            {@code successTagsSupplier} will be added as inner
     *                            html to this tag. If test returns false, the tags
     *                            given by {@code failTagsSupplier} will be added as
     *                            * inner html to this tag and if the
     *                            {@code failTagsSupplier} is null the existing
     *                            children will be removed from this tag.
     * @param successTagsSupplier the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns true. If
     *                            {@code successTagsSupplier.get()} method returns
     *                            null, no action will be done on the tag.
     * @param failConsumer        the consumer to execute if
     *                            {@code uriEventPredicate.test()} returns false.
     *                            {@code null} can be passed if there is no
     *                            {@code failConsumer}.
     * @param index               the index to replace the existing action with
     *                            this. A value less than zero will add this
     *                            condition to the last.
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final Consumer<TagEvent> failConsumer, final int index);

    /**
     * Replaces the children of this tag with the tags supplied by
     * {@code successTagsSupplier} if the predicate test returns true otherwise
     * invokes {@code failConsumer} if no further {@code whenURI} conditions exist
     * and if the {@code failConsumer} is null the existing children of this tag
     * will be removed. To remove the whenURI actions from this tag, call
     * {@link AbstractHtml#removeURIChangeActions()} method. To get the current uri
     * inside the supplier object call {@link BrowserPage#getURI()}. This action
     * will be performed after initial client ping. You can call {@code whenURI}
     * multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     *
     * @param uriEventPredicate   the predicate object to test, the argument of the
     *                            test method is the changed uri details, if the
     *                            test method returns true then the tags given by
     *                            {@code successTagsSupplier} will be added as inner
     *                            html to this tag. If test returns false, the tags
     *                            given by {@code failTagsSupplier} will be added as
     *                            * inner html to this tag and if the
     *                            {@code failTagsSupplier} is null the existing
     *                            children will be removed from this tag.
     * @param successTagsSupplier the supplier object for child tags if
     *                            {@code uriEventPredicate} test returns true. If
     *                            {@code successTagsSupplier.get()} method returns
     *                            null, no action will be done on the tag.
     * @param failConsumer        the consumer to execute if
     *                            {@code uriEventPredicate.test()} returns false.
     *                            {@code null} can be passed if there is no
     *                            {@code failConsumer}.
     *
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate,
            final Supplier<AbstractHtml[]> successTagsSupplier, final Consumer<TagEvent> failConsumer);

    /**
     * Invokes {@code successConsumer} if the predicate test returns true otherwise
     * replaces the children of this tag with the tags supplied by
     * {@code failTagsSupplier} if no further {@code whenURI} conditions exist and
     * if the {@code successConsumer} is null the existing children of this tag will
     * be removed if predicate test returns true. To remove the whenURI actions from
     * this tag, call {@link AbstractHtml#removeURIChangeActions()} method. To get
     * the current uri inside the supplier object call {@link BrowserPage#getURI()}.
     * This action will be performed after initial client ping. You can call
     * {@code whenURI} multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     *
     * @param uriEventPredicate the predicate object to test, the argument of the
     *                          test method is the changed uri details, if the test
     *                          method returns true then the tags given by
     *                          {@code successTagsSupplier} will be added as inner
     *                          html to this tag. If test returns false, the tags
     *                          given by {@code failTagsSupplier} will be added as *
     *                          inner html to this tag and if the
     *                          {@code failTagsSupplier} is null the existing
     *                          children will be removed from this tag.
     * @param successConsumer   the consumer object to invoke if
     *                          {@code uriEventPredicate} test returns true, no
     *                          changes will be done on the tag.
     * @param failTagsSupplier  the supplier object to supply child tags for the tag
     *                          if {@code uriEventPredicate.test()} returns false.
     *                          {@code null} can be passed if there is no
     *                          {@code failTagsSupplier} in such case the existing
     *                          children will be removed.
     *
     * @param index             the index to replace the existing action with this.
     *                          A value less than zero will add this condition to
     *                          the last.
     *
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate, final Consumer<TagEvent> successConsumer,
            final Supplier<AbstractHtml[]> failTagsSupplier, final int index);

    /**
     * Invokes {@code successConsumer} if the predicate test returns true otherwise
     * replaces the children of this tag with the tags supplied by
     * {@code failTagsSupplier} if no further {@code whenURI} conditions exist and
     * if the {@code successConsumer} is null the existing children of this tag will
     * be removed if predicate test returns true. To remove the whenURI actions from
     * this tag, call {@link AbstractHtml#removeURIChangeActions()} method. To get
     * the current uri inside the supplier object call {@link BrowserPage#getURI()}.
     * This action will be performed after initial client ping. You can call
     * {@code whenURI} multiple times to set multiple actions,
     * {@link AbstractHtml#removeURIChangeAction(int)} may be used to remove each
     * action at the given index. If multiple actions are added by this method, only
     * the first {@code uriEventPredicate} test passed action will be performed on
     * uri change. The main intention of this method is to set children tags for
     * this tag when the given {@code uriEventPredicate} test passes on URI change.
     *
     * @param uriEventPredicate the predicate object to test, the argument of the
     *                          test method is the changed uri details, if the test
     *                          method returns true then the tags given by
     *                          {@code successTagsSupplier} will be added as inner
     *                          html to this tag. If test returns false, the tags
     *                          given by {@code failTagsSupplier} will be added as *
     *                          inner html to this tag and if the
     *                          {@code failTagsSupplier} is null the existing
     *                          children will be removed from this tag.
     * @param successConsumer   the consumer object to invoke if
     *                          {@code uriEventPredicate} test returns true, no
     *                          changes will be done on the tag.
     * @param failTagsSupplier  the supplier object to supply child tags for the tag
     *                          if {@code uriEventPredicate.test()} returns false.
     *                          {@code null} can be passed if there is no
     *                          {@code failTagsSupplier} in such case the existing
     *                          children will be removed.
     *
     *
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate, final Consumer<TagEvent> successConsumer,
            final Supplier<AbstractHtml[]> failTagsSupplier);

    /**
     *
     * @param uriEventPredicate
     * @param successConsumer   the consumer to execute if
     *                          {@code uriEventPredicate.test()} returns true
     * @param failConsumer      the consumer to execute if
     *                          {@code uriEventPredicate.test()} returns false.
     *                          {@code null} can be passed if there is no
     *                          {@code failConsumer}.
     * @param index             the position at which this action be the index to
     *                          replace the existing action with this. A value less
     *                          than zero will add this condition to the last.
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate, final Consumer<TagEvent> successConsumer,
            final Consumer<TagEvent> failConsumer, final int index);

    /**
     * @param uriEventPredicate
     * @param successConsumer   the consumer to execute if
     *                          {@code uriEventPredicate.test()} returns true
     * @param failConsumer      the consumer to execute if
     *                          {@code uriEventPredicate.test()} returns false.
     *                          {@code null} can be passed if there is no
     *                          {@code failConsumer}.
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate, final Consumer<TagEvent> successConsumer,
            final Consumer<TagEvent> failConsumer);

    /**
     *
     * @param uriEventPredicate
     * @param successConsumer   the consumer to execute if
     *                          {@code uriEventPredicate.test()} returns true *
     * @param index             the position at which this action be the index to
     *                          replace the existing action with this. A value less
     *                          than zero will add this condition to the last.
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate, final Consumer<TagEvent> successConsumer,
            final int index);

    /**
     * @param uriEventPredicate
     * @param successConsumer   the consumer to execute if
     *                          {@code uriEventPredicate.test()} returns true
     * @return URIStateSwitch
     * @since 12.0.0-beta.1
     */
    URIStateSwitch whenURI(final Predicate<URIEvent> uriEventPredicate, final Consumer<TagEvent> successConsumer);

    /**
     * Removes all whenURI actions.
     *
     * @since 12.0.0-beta.1
     */
    void removeURIChangeActions();

    /**
     * @param index the action to remove at the given index
     * @since 12.0.0-beta.1
     */
    void removeURIChangeAction(final int index);

    /**
     * @return the object of WhenURIProperties only if it is called inside the
     *         uriEventPredicate, success object's method or fail object's method.
     * @since 12.0.0
     */
    public WhenURIProperties getCurrentWhenURIProperties();

    /**
     * @param index the index at which the WhenURIProperties should be obtained.
     * @return the object of WhenURIProperties only if it is available at specified
     *         index.
     * @since 12.0.0
     */
    public WhenURIProperties getWhenURIProperties(final int index);

    /**
     * @param <T> the Tag class
     * @return the current tag object
     */
    @SuppressWarnings("unchecked")
    default <T extends AbstractHtml> T currentAs() {
        return (T) this;
    }
}
