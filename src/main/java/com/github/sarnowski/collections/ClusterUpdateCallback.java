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
 * A callback which will be called on cluster updates.
 * 
 * @author Tobias Sarnowski
 * @since 1.0
 * @see Clustered
 */
public interface ClusterUpdateCallback {

    /**
     * A cluster update occured.
     */
    void clusterUpdated();

}
