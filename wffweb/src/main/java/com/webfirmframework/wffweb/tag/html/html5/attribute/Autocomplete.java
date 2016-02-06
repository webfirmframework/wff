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
 * @author WFF
 */
package com.webfirmframework.wffweb.tag.html.html5.attribute;

import com.webfirmframework.wffweb.tag.html.attribute.AttributeNameConstants;
import com.webfirmframework.wffweb.tag.html.attribute.core.AbstractAttribute;
import com.webfirmframework.wffweb.tag.html.identifier.InputAttributable;

/**
 *
 * <code>align</code> attribute for the element.
 *
 * <pre>
 * This attribute indicates whether the value of the control can be automatically completed by the browser.
 * Possible values are:
 * off: The user must explicitly enter a value into this field for every use, or the document provides its own auto-completion method; the browser does not automatically complete the entry.
 * on: The browser is allowed to automatically complete the value based on values that the user has entered during previous uses, however on does not provide any further information about what kind of data the user might be expected to enter.
 * name: Full name
 * honorific-prefix: Prefix or title (e.g. "Mr.", "Ms.", "Dr.", "Mlle")
 * given-name (first name)
 * additional-name
 * family-name
 * honorific-suffix: Suffix (e.g. "Jr.", "B.Sc.", "MBASW", "II")
 * nickname
 * email
 * username
 * new-password: A new password (e.g. when creating an account or changing a password)
 * current-password
 * organization-title: Job title (e.g. "Software Engineer", "Senior Vice President", "Deputy Managing Director")
 * organization
 * street-address
 * address-line1, address-line2, address-line3, address-level4, address-level3, address-level2, address-level1
 * country
 * country-name
 * postal-code
 * cc-name: Full name as given on the payment instrument
 * cc-given-name
 * cc-additional-name
 * cc-family-name
 * cc-number: Code identifying the payment instrument (e.g. the credit card number)
 * cc-exp: Expiration date of the payment instrument
 * cc-exp-month
 * cc-exp-year
 * cc-csc: Security code for the payment instrument
 * cc-type: Type of payment instrument (e.g. Visa)
 * transaction-currency
 * transaction-amount
 * language: Preferred language; Valid BCP 47 language tag
 * bday
 * bday-day
 * bday-month
 * bday-year
 * sex: Gender identity (e.g. Female, Fa'afafine); Free-form text, no newlines
 * tel
 * url: Home page or other Web page corresponding to the company, person, address, or contact information in the other fields associated with this field
 * photo: Photograph, icon, or other image corresponding to the company, person, address, or contact information in the other fields associated with this field
 * </pre>
 *
 * If the autocomplete attribute is not specified on an input element, then the
 * browser uses the autocomplete attribute value of the {@code<input>} element's
 * form owner. The form owner is either the form element that this {@code
 * <input>} element is a descendant of, or the form element whose id is
 * specified by the form attribute of the input element. For more information,
 * see the autocomplete attribute in {@code<form>}.
 *
 * The autocomplete attribute also controls whether Firefox will, unlike other
 * browsers, persist the dynamic disabled state and (if applicable) dynamic
 * checkedness of an {@code<input>} across page loads. The persistence feature
 * is enabled by default. Setting the value of the autocomplete attribute to off
 * disables this feature; this works even when the autocomplete attribute would
 * normally not apply to the {@code<input>} by virtue of its type. See bug
 * 654072.
 *
 * For most modern browsers (including Firefox 38+, Google Chrome 34+, IE 11+)
 * setting the autocomplete attribute will not prevent a browser's password
 * manager from asking the user if they want to store login (username and
 * password) fields and, if they agree, from autofilling the login the next time
 * the user visits the page. See The autocomplete attribute and login fields.
 *
 * @author WFF
 * @since 1.0.0
 */
public class Autocomplete extends AbstractAttribute
        implements InputAttributable {

    private static final long serialVersionUID = 1_0_0L;

    {
        super.setAttributeName(AttributeNameConstants.AUTOCOMPLETE);
        init();
    }

    /**
     *
     * @param value
     *            the value for the attribute
     * @since 1.0.0
     */
    public Autocomplete(final String value) {
        setAttributeValue(value);
    }

    /**
     * sets the value for this attribute
     *
     * @param value
     *            the value for the attribute.
     * @since 1.0.0
     */
    protected void setValue(final String value) {
        super.setAttributeValue(value);
    }

    /**
     * gets the value of this attribute
     *
     * @return the value of the attribute
     * @since 1.0.0
     */
    public String getValue() {
        return super.getAttributeValue();
    }

    /**
     * invokes only once per object
     *
     * @since 1.0.0
     */
    protected void init() {
    }

}
