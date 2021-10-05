package com.webfirmframework.wffweb.tag.html.attribute.event;

import com.webfirmframework.wffweb.wffbm.data.WffBMObject;

@FunctionalInterface
public interface RemoteMethod extends ServerAsyncMethod {

    WffBMObject orderedRun(final Event event);

    default WffBMObject asyncMethod(WffBMObject data, final Event event) {
        return orderedRun(event);
    }
}
