/*
 * Copyright 2014-2016 Web Firm Framework
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
package com.webfirmframework.wffweb.page;

import java.io.UnsupportedEncodingException;
import java.util.Stack;

import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;

class DataWffIdUtil {

    private DataWffIdUtil() {
        throw new AssertionError();
    }

    static byte[] getDataWffIdBytes(final String dataWffId)
            throws UnsupportedEncodingException {

        // the first byte represents C for Client and S for Server and the
        // remaining string is an integer value
        final byte sOrC = dataWffId.getBytes("UTF-8")[0];

        final byte[] intBytes = WffBinaryMessageUtil.getOptimizedBytesFromInt(
                Integer.parseInt(dataWffId.substring(1)));

        final byte[] dataWffIdBytes = new byte[1 + intBytes.length];
        dataWffIdBytes[0] = sOrC;
        System.arraycopy(intBytes, 0, dataWffIdBytes, 1, intBytes.length);

        return dataWffIdBytes;
    }

    /**
     * @param abstractHtml
     * @return array containing tagName bytes and dataWffIdBytes of the given
     *         argument or its parent.
     * @throws UnsupportedEncodingException
     * @since 1.2.0
     * @author WFF
     */
    static byte[][] getTagNameAndWffId(final AbstractHtml abstractHtml)
            throws UnsupportedEncodingException {

        final Stack<AbstractHtml> parentStack = new Stack<AbstractHtml>();
        parentStack.push(abstractHtml);

        while (parentStack.size() > 0) {

            final AbstractHtml parent = parentStack.pop();

            if (parent.getTagName() != null && !parent.getTagName().isEmpty()) {
                final AbstractAttribute attribute = parent
                        .getAttributeByName("data-wff-id");

                final byte[] dataWffIdBytes = DataWffIdUtil
                        .getDataWffIdBytes(attribute.getAttributeValue());

                return new byte[][] { parent.getTagName().getBytes("UTF-8"),
                        dataWffIdBytes };
            }

            final AbstractHtml parentOfParent = parent.getParent();
            if (parentOfParent != null) {

                parentStack.push(parentOfParent);
            }
        }

        return null;
    }
}
