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
package com.webfirmframework.wffweb.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.core.AttributeUtil;
import com.webfirmframework.wffweb.tag.html.core.PreIndexedTagName;
import com.webfirmframework.wffweb.tag.html.core.TagRegistry;
import com.webfirmframework.wffweb.tag.htmlwff.CustomTag;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;
import com.webfirmframework.wffweb.util.data.NameValue;

/**
 * @since 12.0.3
 */
public enum TagCompressedWffBMBytesParser {

    VERSION_3 {
        private String parseTagName(final byte[] tagNameRowBytes, final Charset charset) {
            if (tagNameRowBytes.length == 1) {
                final int tagNameIndex = WffBinaryMessageUtil.getIntFromOptimizedBytes(tagNameRowBytes);
                return PreIndexedTagName.getPreIndexedTagName(tagNameIndex).tagName();
            }
            final int lengthOfOptimizedIndexBytesOfTagName = tagNameRowBytes[0];
            if (lengthOfOptimizedIndexBytesOfTagName > 0) {
                final byte[] tagNameIndexOptimizedBytes = new byte[lengthOfOptimizedIndexBytesOfTagName];
                System.arraycopy(tagNameRowBytes, 1, tagNameIndexOptimizedBytes, 0,
                        lengthOfOptimizedIndexBytesOfTagName);
                final int tagNameIndex = WffBinaryMessageUtil.getIntFromOptimizedBytes(tagNameIndexOptimizedBytes);
                return PreIndexedTagName.getPreIndexedTagName(tagNameIndex).tagName();
            }

            final int requiredBytesLength = tagNameRowBytes.length - 1;
            final byte[] tagNameBytes = new byte[requiredBytesLength];
            System.arraycopy(tagNameRowBytes, 1, tagNameBytes, 0, requiredBytesLength);
            return new String(tagNameBytes, charset);
        }

        @Override
        public AbstractHtml parse(final byte[] bmBytes, final boolean exactTag, final Charset charset) {

            final List<NameValue> nameValuesAsList = WffBinaryMessageUtil.VERSION_1.parse(bmBytes);

            final NameValue[] nameValues = nameValuesAsList.toArray(new NameValue[nameValuesAsList.size()]);

            final NameValue superParentNameValue = nameValues[0];
            final byte[][] superParentValues = superParentNameValue.getValues();

            final AbstractHtml[] allTags = new AbstractHtml[nameValues.length];

            AbstractHtml parent = null;

            String tagName = parseTagName(superParentValues[0], charset);

            // # short for #text
            // @ short for html content
            // $ short for int bytes content with noTagContentTypeHtml = true.
            // % short for int bytes content with noTagContentTypeHtml = false.

            boolean atTagName = "@".equals(tagName);
            boolean dollarTagName = "$".equals(tagName);
            boolean percentTagName = "%".equals(tagName);
            boolean noTagContentTypeHtml = atTagName || dollarTagName;
            boolean containsIntBytes = dollarTagName || percentTagName;
            if (noTagContentTypeHtml || containsIntBytes || "#".equals(tagName)) {
                final String tagContent = containsIntBytes
                        ? String.valueOf(WffBinaryMessageUtil.getIntFromOptimizedBytes(superParentValues[1]))
                        : new String(superParentValues[1], charset);
                parent = new NoTag(null, tagContent, noTagContentTypeHtml);
            } else {
                final byte[][] attributeHtmlCompressedByIndexArray = new byte[superParentValues.length - 1][0];
                System.arraycopy(superParentValues, 1, attributeHtmlCompressedByIndexArray, 0,
                        attributeHtmlCompressedByIndexArray.length);

                final AbstractAttribute[] attributes = AttributeUtil.parseExactAttributeHtmlBytesCompressedByIndexV2(
                        attributeHtmlCompressedByIndexArray, StandardCharsets.UTF_8);

                if (exactTag) {
                    final AbstractHtml newTagInstance = TagRegistry.getNewTagInstanceOrNullIfFailed(tagName, null,
                            attributes);
                    if (newTagInstance != null) {
                        parent = newTagInstance;
                    } else {
                        parent = new CustomTag(tagName, null, attributes);
                    }
                } else {
                    parent = new CustomTag(tagName, null, attributes);
                }
            }
            allTags[0] = parent;

            for (int i = 1; i < nameValues.length; i++) {

                final NameValue nameValue = nameValues[i];
                final int indexOfParent = WffBinaryMessageUtil.getIntFromOptimizedBytes(nameValue.getName());

                final byte[][] values = nameValue.getValues();

                tagName = parseTagName(values[0], charset);

                // # short for #text
                // @ short for html content
                // $ short for int bytes content with noTagContentTypeHtml = true.
                // % short for int bytes content with noTagContentTypeHtml = false.

                atTagName = "@".equals(tagName);
                dollarTagName = "$".equals(tagName);
                percentTagName = "%".equals(tagName);
                noTagContentTypeHtml = atTagName || dollarTagName;
                containsIntBytes = dollarTagName || percentTagName;

                AbstractHtml child;
                if (noTagContentTypeHtml || containsIntBytes || "#".equals(tagName)) {
                    final String tagContent = containsIntBytes
                            ? String.valueOf(WffBinaryMessageUtil.getIntFromOptimizedBytes(values[1]))
                            : new String(values[1], charset);
                    child = new NoTag(allTags[indexOfParent], tagContent, noTagContentTypeHtml);
                } else {

                    final byte[][] attributeHtmlCompressedByIndexArray = new byte[values.length - 1][0];
                    System.arraycopy(values, 1, attributeHtmlCompressedByIndexArray, 0,
                            attributeHtmlCompressedByIndexArray.length);

                    final AbstractAttribute[] attributes = AttributeUtil
                            .parseExactAttributeHtmlBytesCompressedByIndexV2(attributeHtmlCompressedByIndexArray,
                                    StandardCharsets.UTF_8);

                    if (exactTag) {
                        final AbstractHtml newTagInstance = TagRegistry.getNewTagInstanceOrNullIfFailed(tagName,
                                allTags[indexOfParent], attributes);
                        if (newTagInstance != null) {
                            child = newTagInstance;
                        } else {
                            child = new CustomTag(tagName, allTags[indexOfParent], attributes);
                        }
                    } else {
                        child = new CustomTag(tagName, allTags[indexOfParent], attributes);
                    }
                }
                allTags[i] = child;
            }

            return parent;
        }
    };

    private TagCompressedWffBMBytesParser() {
    }

    public AbstractHtml parse(final byte[] bmBytes, final boolean exactTag, final Charset charset) {
        throw new AssertionError();
    }

}
