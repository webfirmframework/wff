package com.webfirmframework.wffweb.tag.html.attribute.event;

import com.webfirmframework.wffweb.wffbm.data.WffBMObject;

/**
 * Replacement functional interface for ServerAsyncMethod.
 * 
 * @author WFF
 * @since 3.0.19
 *
 */
@FunctionalInterface
public interface RemoteMethod extends ServerAsyncMethod {

    WffBMObject orderedRun(final Event event);

    /**
     * @deprecated This method doesn't have any role in RemoteMethod implementation,
     *             it's a dead method.
     */
    @Override
    @Deprecated
    default WffBMObject asyncMethod(final WffBMObject data, final Event event) {
        return null;
    }
}
