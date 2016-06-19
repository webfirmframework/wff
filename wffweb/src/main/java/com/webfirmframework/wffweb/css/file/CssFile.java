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
package com.webfirmframework.wffweb.css.file;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.webfirmframework.wffweb.css.core.CssProperty;

/**
 *
 * @author WFF
 * @since 1.0.0
 */
public abstract class CssFile implements Serializable, Cloneable {

    private static final long serialVersionUID = 1_0_0L;

    private final Map<String, Set<AbstractCssFileBlock>> selectorCssFileBlocks = new LinkedHashMap<String, Set<AbstractCssFileBlock>>();

    private boolean optimizeCssString = true;

    private final Set<AbstractCssFileBlock> cssBlocks = new LinkedHashSet<AbstractCssFileBlock>() {

        private static final long serialVersionUID = 1_0_0L;

        private final StringBuilder toStringBuilder = new StringBuilder();

        @Override
        public boolean add(final AbstractCssFileBlock e) {
            setModified(super.add(e));
            e.addCssFile(CssFile.this);
            addToSelectorCssFileBlocks(e);
            return modified;
        }

        @Override
        public boolean addAll(
                final Collection<? extends AbstractCssFileBlock> c) {
            modified = super.addAll(c);
            for (final AbstractCssFileBlock abstractCssFileBlock : c) {
                abstractCssFileBlock.addCssFile(CssFile.this);
                addToSelectorCssFileBlocks(abstractCssFileBlock);
            }
            return modified;
        }

        @Override
        public boolean remove(final Object o) {
            modified = super.remove(o);
            if (o instanceof AbstractCssFileBlock) {
                final AbstractCssFileBlock cssFileBlock = (AbstractCssFileBlock) o;
                cssFileBlock.removeCssFile(CssFile.this);
                removeFromSelectorFileBlocks(cssFileBlock);
            }
            return modified;
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            setModified(super.removeAll(c));
            for (final Object obj : c) {
                if (obj instanceof AbstractCssFileBlock) {
                    final AbstractCssFileBlock cssFileBlock = (AbstractCssFileBlock) obj;
                    cssFileBlock.removeCssFile(CssFile.this);
                    removeFromSelectorFileBlocks(cssFileBlock);
                }
            }
            return modified;
        }

        @Override
        public void clear() {
            setModified(true);
            for (final AbstractCssFileBlock cssFileBlock : this) {
                cssFileBlock.removeCssFile(CssFile.this);
                removeFromSelectorFileBlocks(cssFileBlock);
            }
            super.clear();
        }

        @Override
        public String toString() {
            if (modified) {
                toStringBuilder.delete(0, toStringBuilder.length());

                if (isOptimizeCssString()) {
                    for (final Entry<String, Set<AbstractCssFileBlock>> entry : selectorCssFileBlocks
                            .entrySet()) {
                        final Set<AbstractCssFileBlock> cssFileBlocks = entry
                                .getValue();
                        if (cssFileBlocks.size() > 0) {
                            toStringBuilder.append(entry.getKey());
                            toStringBuilder.append('{');
                            final Map<String, CssProperty> cssProperties = new LinkedHashMap<String, CssProperty>();
                            for (final AbstractCssFileBlock cssFileBlock : cssFileBlocks) {
                                cssProperties.putAll(
                                        cssFileBlock.getCssPropertiesAsMap());
                            }

                            for (final CssProperty cssProperty : cssProperties
                                    .values()) {
                                toStringBuilder
                                        .append(cssProperty.getCssName());
                                toStringBuilder.append(':');
                                toStringBuilder
                                        .append(cssProperty.getCssValue());
                                toStringBuilder.append(';');
                            }
                            toStringBuilder.append('}');
                        }
                    }
                } else {
                    for (final AbstractCssFileBlock cssFileBlock : this) {
                        toStringBuilder.append(cssFileBlock.toCssString());
                    }
                }
                setModified(false);
            }
            return toStringBuilder.toString();
        }
    };

    private boolean modified;

    private boolean initialized;

    protected final void initCssFile() {
        if (!initialized) {
            updateCssBlocks();
            initialized = true;
        }
    }

    private void updateCssBlocks() {
        cssBlocks.clear();
        try {
            for (final Field field : this.getClass().getDeclaredFields()) {

                if (AbstractCssFileBlock.class.isAssignableFrom(field.getType())
                        && !field.isAnnotationPresent(ExcludeCssBlock.class)) {

                    final boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    final AbstractCssFileBlock abstractCssFileBlock = (AbstractCssFileBlock) field
                            .get(this);
                    cssBlocks.add(abstractCssFileBlock);
                    field.setAccessible(accessible);
                } else if (CssFile.class.isAssignableFrom(field.getType())
                        && field.isAnnotationPresent(ImportCssFile.class)) {
                    final boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    final CssFile cssFile = (CssFile) field.get(this);

                    // adding empty set will remove all of the objects.
                    if (cssFile.getCssBlocks().size() > 0) {
                        cssBlocks.addAll(cssFile.getCssBlocks());
                    }
                    field.setAccessible(accessible);
                }
            }
        } catch (final SecurityException e) {
            e.printStackTrace();
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the cssBlocks
     * @since 1.0.0
     * @author WFF
     */
    public Set<AbstractCssFileBlock> getCssBlocks() {
        initCssFile();
        return cssBlocks;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    // it's not a best practice to print css by toString method of this class.
    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * @return the css string. i.e. the contents in the css file as a string.
     * @since 1.0.0
     * @author WFF
     */
    public String toCssString() {
        initCssFile();
        return cssBlocks.toString();
    }

    /**
     * @param rebuild
     *            true to force to rebuild
     * @return the css string. i.e. the contents in the css file as a string.
     * @since 1.0.0
     * @author WFF
     */
    public String toCssString(final boolean rebuild) {
        initCssFile();
        modified = rebuild;
        return cssBlocks.toString();
    }

    /**
     * @param modified
     *            the modified to set
     * @since 1.0.0
     * @author WFF
     */
    void setModified(final boolean modified) {
        this.modified = modified;
    }

    private void addToSelectorCssFileBlocks(
            final AbstractCssFileBlock cssFileBlock) {
        Set<AbstractCssFileBlock> abstractCssFileBlocks = selectorCssFileBlocks
                .get(cssFileBlock.getSelectors());
        if (abstractCssFileBlocks == null) {
            abstractCssFileBlocks = new LinkedHashSet<AbstractCssFileBlock>();
            selectorCssFileBlocks.put(cssFileBlock.getSelectors(),
                    abstractCssFileBlocks);
        }
        abstractCssFileBlocks.add(cssFileBlock);
    }

    private void removeFromSelectorFileBlocks(
            final AbstractCssFileBlock cssFileBlock) {
        final Set<AbstractCssFileBlock> abstractCssFileBlocks = selectorCssFileBlocks
                .get(cssFileBlock.getSelectors());
        if (abstractCssFileBlocks != null) {
            abstractCssFileBlocks.remove(cssFileBlock);
        }
    }

    /**
     * @return true if the toCssString to be optimized.
     * @since 1.0.0
     * @author WFF
     */
    public final boolean isOptimizeCssString() {
        return optimizeCssString;
    }

    /**
     * optimizes the {@code toCssString} output. For instance the optimized
     * output for the following blocks in the {@code CssFile} extended class
     * will be
     * <code>.test4-class{list-style-position:outside;background-repeat:no-repeat;}</code>
     * .<br/>
     * <code>private CssBlock cssBlock1 = new CssBlock(".test4-class") </code>{
     * <br/>
     * <code>         @Override</code><br/>
     * <code>         protected void load(Set<CssProperty> cssProperties) {</code>
     * <br/>
     * <code>                 cssProperties.add(ListStylePosition.INSIDE);</code>
     * <br/>
     * <code>         }</code><br/>
     * <code> }; </code><br/>
     * <code>private CssBlock cssBlock2 = new CssBlock(".test4-class") </code>{
     * <br/>
     * <code>         @Override</code><br/>
     * <code>         protected void load(Set<CssProperty> cssProperties) {</code>
     * <br/>
     * <code>                 cssProperties.add(BackgroundRepeat.NO_REPEAT);</code>
     * <br/>
     * <code>                 cssProperties.add(ListStylePosition.OUTSIDE);</code>
     * <br/>
     * <code>         }</code><br/>
     * <code> }; </code>
     *
     * @param optimizeCssString
     *            the optimizeCssString to set. true to optimize the
     *            {@code toCssString} value and false to turn off optimization.
     *            The default value is true.
     * @since 1.0.0
     * @author WFF
     */
    public void setOptimizeCssString(final boolean optimizeCssString) {
        this.optimizeCssString = optimizeCssString;
    }

    /**
     * @return the selectorCssFileBlocks
     * @author WFF
     * @since 1.0.0
     */
    final Map<String, Set<AbstractCssFileBlock>> getSelectorCssFileBlocks() {
        return selectorCssFileBlocks;
    }

}
