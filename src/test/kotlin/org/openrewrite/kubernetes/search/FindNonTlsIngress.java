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

package org.openrewrite.kubernetes.search;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.kubernetes.tree.K8S;
import org.openrewrite.yaml.YamlIsoVisitor;
import org.openrewrite.yaml.search.YamlSearchResult;
import org.openrewrite.yaml.tree.Yaml;

public class FindNonTlsIngress extends Recipe {

    @Override
    public String getDisplayName() {
        return "null";
    }

    @Override
    public String getDescription() {
        return super.getDescription();
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        YamlSearchResult missingTls = new YamlSearchResult(this, "missing TLS");
        YamlSearchResult missingDisallowHttp = new YamlSearchResult(this, "missing disallow http");
        return new YamlIsoVisitor<>() {
            @Override
            public Yaml.Document visitDocument(Yaml.Document document, ExecutionContext ctx) {
                if (K8S.inKind("Ingress", getCursor())) {
                    Yaml.Document d = super.visitDocument(document, ctx);
                    if (!K8S.Ingress.isTlsConfigured(getCursor())) {
                        d = d.withMarkers(d.getMarkers().addIfAbsent(missingTls));
                    }
                    if (!K8S.Ingress.isDisallowHttpConfigured(getCursor())) {
                        d = d.withMarkers(d.getMarkers().addIfAbsent(missingDisallowHttp));
                    }
                    return d;
                }
                return super.visitDocument(document, ctx);
            }
        };
    }
}
