/*
 * Copyright 2015 Ghent University, Bayer CropScience.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jamesframework.ext.analysis;

import org.jamesframework.core.exceptions.JamesRuntimeException;

/**
 * Exception thrown when unknown problem or search IDs are encountered in an analysis.
 * 
 * @author <a href="mailto:herman.debeukelaer@ugent.be">Herman De Beukelaer</a>
 */
public class UnknownIDException extends JamesRuntimeException {

    /**
     * Creates a new instance without detail message.
     */
    public UnknownIDException() {
    }

    /**
     * Constructs an instance with the specified detail message.
     * @param msg the detail message
     */
    public UnknownIDException(String msg) {
        super(msg);
    }
    
}
