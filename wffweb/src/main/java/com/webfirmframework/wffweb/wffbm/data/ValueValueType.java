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
import com.webfirmframework.wffweb.util.NumberUtil;

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
        return BMValueType.getInstanceByType(valueTypeByte);
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
        return switch (valueType) {
        case STRING -> value != null ? Double.parseDouble(String.valueOf(value)) : null;
        case NUMBER -> {
            if (value instanceof final Double number) {
                yield number;
            }
            if (value instanceof final Float number) {
                yield number.doubleValue();
            }
            if (value instanceof final Long number) {
                yield number.doubleValue();
            }
            if (value instanceof final Integer number) {
                yield number.doubleValue();
            }
            if (value instanceof final Number number) {
                yield number.doubleValue();
            }
            yield null;
        }
        case NULL -> null;
        default -> throw new NumberFormatException(
                "Unable to create Double from %s %s".formatted(value, valueType.name()));
        };
    }

    /**
     * @return the Float value.
     * @throws NumberFormatException if the value is not convertible to Float.
     * @since 12.0.3
     */
    public final Float valueAsFloat() throws NumberFormatException {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;

        return switch (valueType) {
        case STRING -> value != null ? Float.parseFloat(String.valueOf(value)) : null;
        case NUMBER -> {
            if (value instanceof final BigInteger number) {
                final float floatValue = number.floatValue();
                if (floatValue != number.longValue()) {
                    throw new NumberFormatException("Unable to create Float from ".concat(number.toString()));
                }
                yield floatValue;
            }
            if (value instanceof final BigDecimal number) {
                // Note: floatValue == doubleValue may be false for the same value in some cases
                // so BigDecimal conversion is required for equalizing
                final float floatValue = number.floatValue();
                if (!NumberUtil.isEqual(number, new BigDecimal(Float.toString(floatValue)))) {
                    throw new NumberFormatException("Unable to create Float from ".concat(String.valueOf(number)));
                }
                yield floatValue;
            }
            if (value instanceof final Double number) {
                // Note: floatValue == doubleValue may be false for the same value in some cases
                // so BigDecimal conversion is required for equalizing
                final float floatValue = number.floatValue();
                if (!NumberUtil.isEqual(new BigDecimal(number, MathContext.DECIMAL64),
                        new BigDecimal(Float.toString(floatValue)))) {
                    throw new NumberFormatException("Unable to create Float from ".concat(String.valueOf(number)));
                }
                yield floatValue;
            }
            if (value instanceof final Number number) {
                yield number.floatValue();
            }
            yield null;
        }
        case NULL -> null;
        default -> throw new NumberFormatException(
                "Unable to create Float from %s %s".formatted(value, valueType.name()));
        };
    }

    /**
     * @return the BigDecimal value.
     * @throws NumberFormatException if the value is not convertible to BigDecimal.
     * @since 12.0.3
     */
    public final BigDecimal valueAsBigDecimal() throws NumberFormatException {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;
        return switch (valueType) {
        case STRING -> value != null ? new BigDecimal(String.valueOf(value)) : null;
        case NUMBER -> {
            if (value instanceof final BigDecimal number) {
                yield number;
            }
            if (value instanceof final BigInteger number) {
                yield new BigDecimal(number, MathContext.DECIMAL64);
            }
            if (value instanceof final Double number) {
                yield new BigDecimal(number, MathContext.DECIMAL64);
            }
            if (value instanceof final Float number) {
                // may not reach to this line as the float is converted to double before saving
                // into WffBMObject.
                yield new BigDecimal(number, MathContext.DECIMAL32);
            }
            if (value instanceof final Long number) {
                yield new BigDecimal(number, MathContext.DECIMAL64);
            }
            if (value instanceof final Integer number) {
                yield new BigDecimal(number, MathContext.DECIMAL32);
            }
            if (value instanceof final Number number) {
                yield BigDecimal.valueOf(number.doubleValue());
            }
            yield null;
        }
        case NULL -> null;
        default -> throw new NumberFormatException(
                "Unable to create BigDecimal from %s %s".formatted(value, valueType.name()));
        };
    }

    /**
     * @return the BigInteger value.
     * @throws NumberFormatException the value is not convertible to BigInteger.
     * @since 12.0.3
     */
    public final BigInteger valueAsBigInteger() throws NumberFormatException {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;

        return switch (valueType) {
        case STRING -> value != null ? new BigInteger(String.valueOf(value)) : null;
        case NUMBER -> {
            if (value instanceof final BigInteger number) {
                yield number;
            }
            if (value instanceof final BigDecimal number) {
                if (number.longValue() != number.doubleValue()) {
                    throw new NumberFormatException(
                            "Unable to create BigInteger from ".concat(number.stripTrailingZeros().toPlainString()));
                }
                yield number.toBigInteger();
            }
            if (value instanceof final Double number) {
                final long longValue = number.longValue();
                if (longValue != number) {
                    throw new NumberFormatException(
                            "Unable to create BigInteger from ".concat(Double.toString(number)));
                }
                yield BigInteger.valueOf(longValue);
            }
            if (value instanceof final Float number) {
                // may not reach to this line as the float is converted to double before saving
                // into WffBMObject.
                final int intValue = number.intValue();
                if (intValue != number) {
                    throw new NumberFormatException("Unable to create BigInteger from ".concat(Float.toString(number)));
                }
                yield BigInteger.valueOf(intValue);
            }
            if (value instanceof final Long number) {
                yield BigInteger.valueOf(number);
            }
            if (value instanceof final Integer number) {
                yield BigInteger.valueOf(number.longValue());
            }
            if (value instanceof final Number number) {
                yield BigInteger.valueOf(number.longValue());
            }
            yield null;
        }
        case NULL -> null;
        default -> throw new NumberFormatException(
                "Unable to create BigInteger from %s %s".formatted(value, valueType.name()));
        };
    }

    /**
     * @return the Integer value.
     * @throws NumberFormatException the value is not convertible to Integer.
     * @since 12.0.3
     */
    public final Integer valueAsInteger() throws NumberFormatException {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;

        return switch (valueType) {
        case STRING -> value != null ? Integer.parseInt(String.valueOf(value)) : null;
        case NUMBER -> {
            if (value instanceof final BigInteger number) {
                yield Math.toIntExact(number.longValue());
            }
            if (value instanceof final BigDecimal number) {
                final long longValue = number.longValue();
                if (longValue != number.doubleValue()) {
                    throw new NumberFormatException(
                            "Unable to create Integer from ".concat(number.stripTrailingZeros().toPlainString()));
                }
                yield Math.toIntExact(longValue);
            }
            if (value instanceof final Double number) {
                final int intValue = number.intValue();
                if (intValue != number) {
                    throw new NumberFormatException("Unable to create Integer from ".concat(Double.toString(intValue)));
                }
                yield intValue;
            }
            if (value instanceof final Float number) {
                // may not reach to this line as the float is converted to double before saving
                // into WffBMObject.
                final int intValue = number.intValue();
                if (intValue != number) {
                    throw new NumberFormatException("Unable to create Integer from ".concat(Float.toString(number)));
                }
                yield intValue;
            }
            if (value instanceof final Long number) {
                final int intValue = number.intValue();
                if (intValue != number) {
                    throw new NumberFormatException("Unable to create Integer from ".concat(Long.toString(number)));
                }
                yield intValue;
            }
            if (value instanceof final Integer number) {
                yield number;
            }
            if (value instanceof final Number number) {
                yield number.intValue();
            }
            yield null;
        }
        case NULL -> null;
        default -> throw new NumberFormatException(
                "Unable to create Integer from %s %s".formatted(value, valueType.name()));
        };
    }

    /**
     * @return the Long value.
     * @throws NumberFormatException the value is not convertible to Long.
     * @since 12.0.3
     */
    public final Long valueAsLong() throws NumberFormatException {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;

        return switch (valueType) {
        case STRING -> value != null ? Long.parseLong(String.valueOf(value)) : null;
        case NUMBER -> {
            if (value instanceof final BigInteger number) {
                yield number.longValue();
            }
            if (value instanceof final BigDecimal number) {
                final long longValue = number.longValue();
                if (longValue != number.doubleValue()) {
                    throw new NumberFormatException(
                            "Unable to create Long from ".concat(number.stripTrailingZeros().toPlainString()));
                }
                yield longValue;
            }
            if (value instanceof final Double number) {
                final long longValue = number.longValue();
                if (longValue != number) {
                    throw new NumberFormatException("Unable to create Long from ".concat(Double.toString(number)));
                }
                yield longValue;
            }
            if (value instanceof final Float number) {
                final long longValue = number.longValue();
                if (longValue != number) {
                    throw new NumberFormatException("Unable to create Long from ".concat(Float.toString(number)));
                }
                yield longValue;
            }
            if (value instanceof final Long number) {
                yield number;
            }
            if (value instanceof final Integer number) {
                yield number.longValue();
            }
            if (value instanceof final Number number) {
                yield number.longValue();
            }
            yield null;
        }
        case NULL -> null;
        default -> throw new NumberFormatException(
                "Unable to create Long from %s %s".formatted(value, valueType.name()));
        };

    }

    /**
     * @return the value as a String.
     * @since 12.0.3
     */
    public final String valueAsString() {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;

        return switch (valueType) {
        case STRING -> value instanceof final String s ? s : null;
        case NUMBER -> {
            if (value instanceof final BigDecimal number) {
                yield number.stripTrailingZeros().toPlainString();
            }
            if (value instanceof final Number number) {
                yield number.toString();
            }
            yield null;
        }
        case NULL -> null;
        default -> {
            if (value != null) {
                yield String.valueOf(value);
            }
            yield null;
        }
        };
    }

    /**
     * @return the value as a Boolean.
     * @throws InvalidValueException if the value is not convertible to Boolean.
     * @since 12.0.3
     */
    public final Boolean valueAsBoolean() throws InvalidValueException {
        final BMValueType valueType = BMValueType.getInstanceByType(valueTypeByte);
        final Object value = this.value;

        return switch (valueType) {
        case STRING -> {
            if (value != null) {
                final String s = String.valueOf(value);
                if (s.equalsIgnoreCase("true")) {
                    yield Boolean.TRUE;
                }
                if (s.equalsIgnoreCase("false")) {
                    yield Boolean.FALSE;
                }
            }
            yield null;
        }
        case BOOLEAN -> value instanceof final Boolean b ? b : null;
        case NULL -> null;
        default -> throw new InvalidValueException(
                "Unable to create Boolean from %s %s".formatted(value, valueType.name()));
        };
    }

}
