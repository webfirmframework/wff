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
 */
package com.webfirmframework.wffweb.wffbm.data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import com.webfirmframework.wffweb.util.WffBinaryMessageUtil;

public class ValueValueType implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;

    private Object value;

    private byte valueTypeByte;

    public ValueValueType(final String name, final byte valueTypeByte, final Object value) {
        super();
        this.name = name;
        this.valueTypeByte = valueTypeByte;
        this.value = value;
    }

    public ValueValueType(final String name, final BMValueType valueType, final Object value) {
        super();
        this.name = name;
        valueTypeByte = valueType.getType();
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    public BMValueType getValueType() {
        return BMValueType.values()[valueTypeByte];
    }

    public byte getValueTypeByte() {
        return valueTypeByte;
    }

    public void setValueTypeByte(final byte valueTypeByte) {
        this.valueTypeByte = valueTypeByte;
    }

    public void setValueType(final BMValueType valueType) {
        valueTypeByte = valueType.getType();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        final ValueValueType other = (ValueValueType) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    /**
     * @return the BigDecimal value.
     * @throws NumberFormatException if the value is not convertible to BigDecimal.
     * @since 12.0.3
     */
    public final BigDecimal valueAsBigDecimal() throws NumberFormatException {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;
        if (BMValueType.STRING.equals(valueType)) {
            if (value instanceof final String s) {
                return new BigDecimal(s);
            }
            return null;
        } else if (BMValueType.NUMBER.equals(valueType)) {
            if (value instanceof final Integer number) {
                return new BigDecimal(number, MathContext.DECIMAL32);
            } else if (value instanceof final Long number) {
                return new BigDecimal(number, MathContext.DECIMAL64);
            } else if (value instanceof final Double number) {
                return new BigDecimal(number, MathContext.DECIMAL64);
            } else if (value instanceof final Float number) {
                return new BigDecimal(number, MathContext.DECIMAL32);
            }
            return null;
        } else if (BMValueType.NULL.equals(valueType)) {
            return null;
        }
        throw new NumberFormatException("Unable to create BigDecimal from ".concat(valueType.name()));
    }

    /**
     * @return the BigInteger value.
     * @throws NumberFormatException the value is not convertible to BigInteger.
     * @since 12.0.3
     */
    public final BigInteger valueAsBigInteger() throws NumberFormatException {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;
        if (BMValueType.STRING.equals(valueType)) {
            if (value instanceof final String s) {
                return new BigInteger(s);
            }
            return null;
        } else if (BMValueType.NUMBER.equals(valueType)) {
            if (value instanceof final Integer number) {
                return new BigInteger(WffBinaryMessageUtil.getOptimizedBytesFromInt(number));
            } else if (value instanceof final Long number) {
                return new BigInteger(WffBinaryMessageUtil.getOptimizedBytesFromLong(number));
            } else if (value instanceof final Double number) {
                final long longValue = number.longValue();
                if (longValue != number) {
                    throw new NumberFormatException(
                            "Unable to create BigInteger from ".concat(Double.toString(number)));
                }
                return new BigInteger(WffBinaryMessageUtil.getOptimizedBytesFromLong(longValue));
            } else if (value instanceof final Float number) {
                final int intValue = number.intValue();
                if (intValue != number) {
                    throw new NumberFormatException("Unable to create BigInteger from ".concat(Float.toString(number)));
                }
                return new BigInteger(WffBinaryMessageUtil.getOptimizedBytesFromInt(intValue));
            }
            return null;
        } else if (BMValueType.NULL.equals(valueType)) {
            return null;
        }
        throw new NumberFormatException("Unable to create BigInteger from ".concat(valueType.name()));
    }

    /**
     * @return the Integer value.
     * @throws NumberFormatException the value is not convertible to Integer.
     * @since 12.0.3
     */
    public final Integer valueAsInteger() throws NumberFormatException {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;
        if (BMValueType.NUMBER.equals(valueType)) {
            if (value instanceof final Integer number) {
                return number;
            } else if (value instanceof final Long number) {
                final int intValue = number.intValue();
                if (intValue != number) {
                    throw new NumberFormatException("Unable to create Integer from ".concat(Long.toString(number)));
                }
                return intValue;
            } else if (value instanceof final Double number) {
                final int intValue = number.intValue();
                if (intValue != number) {
                    throw new NumberFormatException("Unable to create Integer from ".concat(Double.toString(intValue)));
                }
                return intValue;
            } else if (value instanceof final Float number) {
                final int intValue = number.intValue();
                if (intValue != number) {
                    throw new NumberFormatException("Unable to create Integer from ".concat(Float.toString(number)));
                }
                return intValue;
            }
            return null;
        } else if (BMValueType.NULL.equals(valueType)) {
            return null;
        }
        throw new NumberFormatException("Unable to create Integer from ".concat(valueType.name()));
    }

    /**
     * @return the Long value.
     * @throws NumberFormatException the value is not convertible to Long.
     * @since 12.0.3
     */
    public final Long valueAsLong() throws NumberFormatException {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;
        if (BMValueType.NUMBER.equals(valueType)) {
            if (value instanceof final Integer number) {
                return number.longValue();
            } else if (value instanceof final Long number) {
                return number;
            } else if (value instanceof final Double number) {
                final long longValue = number.longValue();
                if (longValue != number) {
                    throw new NumberFormatException("Unable to create Long from ".concat(Double.toString(number)));
                }
                return longValue;
            } else if (value instanceof final Float number) {
                final long longValue = number.longValue();
                if (longValue != number) {
                    throw new NumberFormatException("Unable to create Long from ".concat(Float.toString(number)));
                }
                return longValue;
            }
            return null;
        } else if (BMValueType.NULL.equals(valueType)) {
            return null;
        }
        throw new NumberFormatException("Unable to create Long from ".concat(valueType.name()));
    }
}
