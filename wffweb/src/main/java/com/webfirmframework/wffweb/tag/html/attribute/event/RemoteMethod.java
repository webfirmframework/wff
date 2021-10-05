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

    @Override
    default WffBMObject asyncMethod(final WffBMObject data, final Event event) {
        return orderedRun(event);
    }
}
