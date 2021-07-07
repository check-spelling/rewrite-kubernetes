/*
 *  Copyright 2021 the original author or authors.
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  https://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.openrewrite.kubernetes.search

import org.junit.jupiter.api.Test
import org.openrewrite.kubernetes.KubernetesRecipeTest

class FindMissingDigestTest : KubernetesRecipeTest {

    @Test
    fun `must detect when digest is missing`() = assertChanged(
        recipe = FindMissingDigest(true),
        before = """
            apiVersion: v1
            kind: Pod
            spec:
                containers:             
                - image: image
            ---
            apiVersion: v1
            kind: Pod
            spec:
                containers:             
                - image: app:v1.2.3
                initContainers:             
                - image: account/image:latest@digest
        """.trimIndent(),
        after = """
            apiVersion: v1
            kind: Pod
            spec:
                containers:             
                - image: ~~(missing digest)~~>image
            ---
            apiVersion: v1
            kind: Pod
            spec:
                containers:             
                - image: ~~(missing digest)~~>app:v1.2.3
                initContainers:             
                - image: account/image:latest@digest
        """.trimIndent()
    )

}