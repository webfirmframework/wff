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
import java.util.Objects;

import com.webfirmframework.wffweb.InvalidValueException;

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
    public boolean equals(final Object o) {
        if (!(o instanceof final ValueValueType that)) {
            return false;
        }
        return valueTypeByte == that.valueTypeByte && Objects.equals(name, that.name)
                && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, valueTypeByte);
    }

    /**
     * @return the Double value.
     * @throws NumberFormatException if the value is not convertible to Double.
     * @since 12.0.3
     */
    public final Double valueAsDouble() throws NumberFormatException {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;
        if (BMValueType.STRING.equals(valueType)) {
            if (value != null) {
                return Double.parseDouble(String.valueOf(value));
            }
            return null;
        } else if (BMValueType.NUMBER.equals(valueType)) {
            if (value instanceof final Double number) {
                return number;
            } else if (value instanceof final Float number) {
                return number.doubleValue();
            } else if (value instanceof final Long number) {
                return number.doubleValue();
            } else if (value instanceof final Integer number) {
                return number.doubleValue();
            } else if (value instanceof final Number number) {
                return number.doubleValue();
            }
            return null;
        } else if (BMValueType.NULL.equals(valueType)) {
            return null;
        }
        throw new NumberFormatException("Unable to create Double from %s %s".formatted(value, valueType.name()));
    }

    /**
     * @return the Float value.
     * @throws NumberFormatException if the value is not convertible to Float.
     * @since 12.0.3
     */
    public final Float valueAsFloat() throws NumberFormatException {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;
        if (BMValueType.STRING.equals(valueType)) {
            if (value != null) {
                return Float.parseFloat(String.valueOf(value));
            }
            return null;
        } else if (BMValueType.NUMBER.equals(valueType)) {
            if (value instanceof final Number number) {
                return number.floatValue();
            }
            return null;
        } else if (BMValueType.NULL.equals(valueType)) {
            return null;
        }
        throw new NumberFormatException("Unable to create Float from %s %s".formatted(value, valueType.name()));
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
            if (value != null) {
                return new BigDecimal(String.valueOf(value));
            }
            return null;
        } else if (BMValueType.NUMBER.equals(valueType)) {
            if (value instanceof final Double number) {
                return new BigDecimal(number, MathContext.DECIMAL64);
            } else if (value instanceof final Float number) {
                return new BigDecimal(number, MathContext.DECIMAL32);
            } else if (value instanceof final Long number) {
                return new BigDecimal(number, MathContext.DECIMAL64);
            } else if (value instanceof final Integer number) {
                return new BigDecimal(number, MathContext.DECIMAL32);
            } else if (value instanceof final Number number) {
                return BigDecimal.valueOf(number.doubleValue());
            }
            return null;
        } else if (BMValueType.NULL.equals(valueType)) {
            return null;
        }
        throw new NumberFormatException("Unable to create BigDecimal from %s %s".formatted(value, valueType.name()));
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
            if (value != null) {
                return new BigInteger(String.valueOf(value));
            }
            return null;
        } else if (BMValueType.NUMBER.equals(valueType)) {
            if (value instanceof final Double number) {
                final long longValue = number.longValue();
                if (longValue != number) {
                    throw new NumberFormatException(
                            "Unable to create BigInteger from ".concat(Double.toString(number)));
                }
                return BigInteger.valueOf(longValue);
            } else if (value instanceof final Float number) {
                final int intValue = number.intValue();
                if (intValue != number) {
                    throw new NumberFormatException("Unable to create BigInteger from ".concat(Float.toString(number)));
                }
                return BigInteger.valueOf(intValue);
            } else if (value instanceof final Long number) {
                return BigInteger.valueOf(number);
            } else if (value instanceof final Integer number) {
                return BigInteger.valueOf(number.longValue());
            } else if (value instanceof final Number number) {
                return BigInteger.valueOf(number.longValue());
            }
            return null;
        } else if (BMValueType.NULL.equals(valueType)) {
            return null;
        }
        throw new NumberFormatException("Unable to create BigInteger from %s %s".formatted(value, valueType.name()));
    }

    /**
     * @return the Integer value.
     * @throws NumberFormatException the value is not convertible to Integer.
     * @since 12.0.3
     */
    public final Integer valueAsInteger() throws NumberFormatException {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;
        if (BMValueType.STRING.equals(valueType)) {
            if (value != null) {
                return Integer.parseInt(String.valueOf(value));
            }
            return null;
        } else if (BMValueType.NUMBER.equals(valueType)) {
            if (value instanceof final Double number) {
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
            } else if (value instanceof final Long number) {
                final int intValue = number.intValue();
                if (intValue != number) {
                    throw new NumberFormatException("Unable to create Integer from ".concat(Long.toString(number)));
                }
                return intValue;
            } else if (value instanceof final Integer number) {
                return number;
            } else if (value instanceof final Number number) {
                return number.intValue();
            }
            return null;
        } else if (BMValueType.NULL.equals(valueType)) {
            return null;
        }
        throw new NumberFormatException("Unable to create Integer from %s %s".formatted(value, valueType.name()));
    }

    /**
     * @return the Long value.
     * @throws NumberFormatException the value is not convertible to Long.
     * @since 12.0.3
     */
    public final Long valueAsLong() throws NumberFormatException {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;
        if (BMValueType.STRING.equals(valueType)) {
            if (value != null) {
                return Long.parseLong(String.valueOf(value));
            }
            return null;
        } else if (BMValueType.NUMBER.equals(valueType)) {
            if (value instanceof final Double number) {
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
            } else if (value instanceof final Long number) {
                return number;
            } else if (value instanceof final Integer number) {
                return number.longValue();
            } else if (value instanceof final Number number) {
                return number.longValue();
            }
            return null;
        } else if (BMValueType.NULL.equals(valueType)) {
            return null;
        }
        throw new NumberFormatException("Unable to create Long from %s %s".formatted(value, valueType.name()));
    }

    /**
     * @return the value as a String.
     * @since 12.0.3
     */
    public final String valueAsString() {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;
        if (BMValueType.STRING.equals(valueType)) {
            if (value instanceof final String s) {
                return s;
            }
            return null;
        } else if (BMValueType.NULL.equals(valueType)) {
            return null;
        } else if (value != null) {
            return String.valueOf(value);
        }
        return null;
    }

    /**
     * @return the value as a Boolean.
     * @throws InvalidValueException if the value is not convertible to Boolean.
     * @since 12.0.3
     */
    public final Boolean valueAsBoolean() throws InvalidValueException {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;
        if (BMValueType.STRING.equals(valueType)) {
            if (value != null) {
                final String s = String.valueOf(value);
                if (s.equalsIgnoreCase("true")) {
                    return Boolean.TRUE;
                }
                if (s.equalsIgnoreCase("false")) {
                    return Boolean.FALSE;
                }
            }
            return null;
        } else if (BMValueType.BOOLEAN.equals(valueType)) {
            if (value instanceof final Boolean b) {
                return b;
            }
            return null;
        } else if (BMValueType.NULL.equals(valueType)) {
            return null;
        }
        throw new InvalidValueException("Unable to create Boolean from %s %s".formatted(value, valueType.name()));
    }
}
