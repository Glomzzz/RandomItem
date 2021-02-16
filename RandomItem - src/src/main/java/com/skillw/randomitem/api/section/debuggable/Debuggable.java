package com.skillw.randomitem.api.section.debuggable;

import java.util.List;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.section.debuggable.Debuggable
 * @date 2021-02-10 19:35:35
 * Copyright  2020 user. All rights reserved.
 */
public interface Debuggable {
    /**
     * To get the debug messages.
     *
     * @return debug messages
     */
    List<String> getDebugMessages();
}
