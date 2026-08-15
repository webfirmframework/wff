/*
 * Copyright since 2014 Web Firm Framework
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
package com.webfirmframework.wffweb.common.test;

import com.webfirmframework.wffweb.common.EventInitiatorTest;
import com.webfirmframework.wffweb.concurrent.ReentrantStampedLockTest;
import com.webfirmframework.wffweb.css.*;
import com.webfirmframework.wffweb.css.css3.*;
import com.webfirmframework.wffweb.css.file.CssFileTest;
import com.webfirmframework.wffweb.internal.security.object.SecurityClassConstantsTest;
import com.webfirmframework.wffweb.internal.server.page.js.WffJsFileTest;
import com.webfirmframework.wffweb.js.JsUtilTest;
import com.webfirmframework.wffweb.json.*;
import com.webfirmframework.wffweb.lang.UnicodeStringTest;
import com.webfirmframework.wffweb.server.page.ExternalDriveByteArrayQueueTest;
import com.webfirmframework.wffweb.server.page.ExternalDriveClientTasksWrapperDequeTest;
import com.webfirmframework.wffweb.server.page.ExternalDriveClientTasksWrapperQueueTest;
import com.webfirmframework.wffweb.server.page.TaskTest;
import com.webfirmframework.wffweb.server.page.action.BrowserPageActionTest;
import com.webfirmframework.wffweb.settings.WffConfigurationTest;
import com.webfirmframework.wffweb.streamer.WffBinaryMessageOutputStreamerTest;
import com.webfirmframework.wffweb.tag.html.*;
import com.webfirmframework.wffweb.tag.html.attribute.*;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttributeTest;
import com.webfirmframework.wffweb.tag.html.attribute.core.AttributeIdGeneratorTest;
import com.webfirmframework.wffweb.tag.html.attribute.core.AttributeRegistryTest;
import com.webfirmframework.wffweb.tag.html.attribute.core.AttributeUtilTest;
import com.webfirmframework.wffweb.tag.html.attribute.global.ClassAttributeTest;
import com.webfirmframework.wffweb.tag.html.attribute.global.InertTest;
import com.webfirmframework.wffweb.tag.html.attribute.global.PopoverTest;
import com.webfirmframework.wffweb.tag.html.attribute.global.StyleTest;
import com.webfirmframework.wffweb.tag.html.attributewff.ImmutableCustomAttributeTest;
import com.webfirmframework.wffweb.tag.html.core.TagRegistryTest;
import com.webfirmframework.wffweb.tag.html.formsandinputs.FormTest;
import com.webfirmframework.wffweb.tag.html.formsandinputs.InputTest;
import com.webfirmframework.wffweb.tag.html.formsandinputs.TextAreaTest;
import com.webfirmframework.wffweb.tag.html.html5.attribute.AutoCompleteTest;
import com.webfirmframework.wffweb.tag.html.html5.attribute.CrossOriginTest;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.DataWffIdTest;
import com.webfirmframework.wffweb.tag.html.html5.attribute.global.InputModeTest;
import com.webfirmframework.wffweb.tag.html.images.ImgTest;
import com.webfirmframework.wffweb.tag.html.model.AbstractHtml5SharedObjectTest;
import com.webfirmframework.wffweb.tag.html.model.SharedObjectIdGeneratorTest;
import com.webfirmframework.wffweb.tag.htmlwff.BlankTest;
import com.webfirmframework.wffweb.tag.htmlwff.NoTagTest;
import com.webfirmframework.wffweb.tag.htmlwff.TagContentTest;
import com.webfirmframework.wffweb.tag.repository.TagRepositoryTest;
import com.webfirmframework.wffweb.util.*;
import com.webfirmframework.wffweb.wffbm.data.WffBMArrayTest;
import com.webfirmframework.wffweb.wffbm.data.WffBMObjectArrayTest;
import com.webfirmframework.wffweb.wffbm.data.WffBMObjectTest;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 *
 * @author WFF
 * @since 1.0.0
 */
@RunWith(Suite.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuiteClasses({ HtmlTest.class, TagPrintTest.class, CssPropertyEnumTests.class, AlignContentTest.class, StyleTest.class,
        CursorTest.class, ListStyleImageTest.class, WordSpacingTest.class, BorderBottomWidthTest.class,
        BorderLeftWidthTest.class, BorderRightWidthTest.class, BorderTopWidthTest.class, ColumnRuleWidthTest.class,
        WebkitColumnRuleWidthTest.class, MozColumnRuleWidthTest.class, ColumnWidthTest.class,
        WebkitColumnWidthTest.class, MozColumnWidthTest.class, ColumnRuleColorTest.class,
        WebkitColumnRuleColorTest.class, MozColumnRuleColorTest.class, ColumnRuleTest.class, MozColumnRuleTest.class,
        WebkitColumnRuleTest.class, BackgroundColorTest.class, BorderBottomColorTest.class, BorderTopColorTest.class,
        BorderLeftColorTest.class, BorderRightColorTest.class, BorderColorTest.class, ColorTest.class,
        OutlineColorTest.class, OutlineWidthTest.class, OutlineOffsetTest.class, BorderWidthTest.class,
        WidthCssTest.class, BorderColorCssValuesTest.class, HslCssValueTest.class, RgbCssValueTest.class,
        HslaCssValueTest.class, RgbaCssValueTest.class, BorderTest.class, PaddingTopTest.class, PaddingRightTest.class,
        PaddingBottomTest.class, PaddingLeftTest.class, PaddingTest.class, StringUtilTest.class, CssValueUtilTest.class,
        BorderTopTest.class, BorderRightTest.class, BorderBottomTest.class, BorderLeftTest.class, MarginTopTest.class,
        MarginRightTest.class, MarginBottomTest.class, MarginLeftTest.class, MarginTest.class, OutlineTest.class,
        TopTest.class, RightTest.class, BottomTest.class, LeftTest.class, ColumnGapTest.class, MozColumnGapTest.class,
        WebkitColumnGapTest.class, LetterSpacingTest.class, LineHeightTest.class, BorderSpacingTest.class,
        BackgroundSizeTest.class, WebkitBackgroundSizeTest.class, MozBackgroundSizeTest.class,
        OBackgroundSizeTest.class, OpacityTest.class, PerspectiveTest.class, PerspectiveOriginTest.class,
        BackgroundImageTest.class, IconTest.class, FlexBasisTest.class, WebkitFlexBasisTest.class,
        MozFlexBasisTest.class, AnimationIterationCountTest.class, FlexGrowTest.class, WebkitFlexGrowTest.class,
        MozFlexGrowTest.class, FlexShrinkTest.class, MozFlexShrinkTest.class, WebkitFlexShrinkTest.class,
        FontSizeAdjustTest.class, ColumnCountTest.class, MozColumnCountTest.class, WebkitColumnCountTest.class,
        FlexTest.class, WebkitFlexTest.class, MozFlexTest.class, MsFlexTest.class, FontFamilyTest.class,
        FontSizeTest.class, FontTest.class, ColumnsTest.class, CssLengthUtilTest.class, ObjectUtilTest.class,
        BorderImageRepeatTest.class, BorderImageWidthTest.class, BorderImageOutsetTest.class,
        BorderImageSliceTest.class, BorderImageSourceTest.class, WffBinaryMessageUtilTest.class, ColumnCountTest.class,
        WebkitColumnSpanTest.class, ClassAttributeTest.class, ImgTest.class, HrTest.class, InputTest.class,
        CssFileTest.class, WffBinaryMessageOutputStreamerTest.class, NoTagTest.class, BlankTest.class,
        TagRegistryTest.class, AttributeRegistryTest.class, AttributeUtilTest.class, AbstractHtmlTest.class,
        AbstractAttributeTest.class, JsUtilTest.class, TextAreaTest.class, SelectedTest.class, CheckedTest.class,
        FormTest.class, TagRepositoryTest.class, SrcCssPropertyTest.class, UrlCss3ValueTest.class,
        WffBMObjectArrayTest.class, RelTest.class, AbstractHtml5SharedObjectTest.class, HeightCssTest.class,
        AutoCompleteTest.class, AbstractHtmlRepositoryTest.class, CodePerformanceTest.class, HashUtilTest.class,
        AbstractHtml5SharedObjectTest.class, ByteBufferUtilTest.class, UnicodeRangeTest.class, DataWffIdTest.class,
        SharedTagContentTest.class, CssColorNameTest.class, TaskTest.class, WffJsFileTest.class,
        StringBuilderUtilTest.class, SecurityClassConstantsTest.class, ReentrantStampedLockTest.class,
        UnicodeStringTest.class, ExternalDriveByteArrayQueueTest.class, ExternalDriveClientTasksWrapperDequeTest.class,
        ExternalDriveClientTasksWrapperQueueTest.class, AttributeIdGeneratorTest.class,
        SharedObjectIdGeneratorTest.class, WhenURIUseCaseTest.class, URIUtilTest.class, EventInitiatorTest.class,
        ImmutableCustomAttributeTest.class, BrowserPageActionTest.class, WffConfigurationTest.class,
        TagContentTest.class, WffBMObjectTest.class, WffBMArrayTest.class, TagCompressedWffBMBytesParserTest.class,
        JsonCodePointUtilTest.class, JsonListTest.class, JsonMapTest.class, JsonParserTest.class, JsonValueTest.class,
        JsonMapNodeTest.class, JsonListNodeTest.class, JsonStringUtilTest.class, PopoverTest.class, PopoverTargetTest.class,
        PopoverTargetActionTest.class, CommandTest.class, CommandForTest.class, InterestForTest.class, CrossOriginTest.class,
        FetchPriorityTest.class, IntegrityTest.class, InertTest.class, InputModeTest.class})
public class AllTests {

}
