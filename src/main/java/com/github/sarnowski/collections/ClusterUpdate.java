/**
 * Copyright 2011 Tobias Sarnowski
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
package com.github.sarnowski.collections;

import java.io.Serializable;

/**
 * The transport object of the {@link ClusterManager} within the cluster.
 *
 * @author Tobias Sarnowski
 * @since 1.0
 * @see ClusterManager
 * @param <A> generic action type
 * @param <P> generic payload type
 */
final class ClusterUpdate<A, P> implements Serializable {
    
    private static final long serialVersionUID = 4851107754378987870L;
    
    private final A actionIdentifier;
    private final P payload;

    ClusterUpdate(A actionIdentifier, P payload) {
        this.actionIdentifier = actionIdentifier;
        this.payload = payload;
    }

    public A getActionIdentifier() {
        return actionIdentifier;
    }

    public P getPayload() {
        return payload;
    }
    
}
