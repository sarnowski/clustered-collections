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

/**
 * Defines a high level abstraction for JGroups events. A {@link ClusterManager}
 * will handle the instance.
 *
 * @author Tobias Sarnowski
 * @since 1.0
 * @see ClusterManager
 * @param <A> a serializable action identifier 
 * @param <P> a serializable payload container
 * @param <S> a serializable state transfer container
 */
interface ClusterManaged<A,P,S> {

    /**
     * Will be triggered as soon as an update from the cluster receives.
     *
     * @param action the update action
     * @param payload the update payload
     */
    void handleUpdate(A action, P payload);

    /**
     * Will be triggered as soon as a new state from the cluster receives.
     *
     * @param state the new state
     */
    void updateClusterState(S state);

    /**
     * Instance has to provide the current state because another cluster member
     * asked for it.
     *
     * @return the current state
     */
    S provideClusterState();

}
