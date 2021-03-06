/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.phoenixnap.oss.ramlapisync.generation.rules.spring;

import com.phoenixnap.oss.ramlapisync.data.ApiActionMetadata;
import com.phoenixnap.oss.ramlapisync.data.ApiBodyMetadata;
import com.phoenixnap.oss.ramlapisync.generation.rules.Rule;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JType;

import java.util.List;

import static com.phoenixnap.oss.ramlapisync.generation.CodeModelHelper.findFirstClassBySimpleName;

/**
 * Creates an Object as a return type for an endpoint.
 * If the endpoint declares a response body the first type of the response body will used as return type instead.
 * If the endpoints response body is an "array" th
 *
 * #%RAML 0.8
 * title: myapi
 * mediaType: application/json
 * baseUri: /
 *
 * /base:
 *   get:
 *   /{id}:
 *     get:
 *       responses:
 *         200:
 *           body:
 *             application/json:
 *               schema: NamedResponseType
 *               ...
 *
 * OUTPUT:
 * NamedResponseType
 *
 * OR:
 * ArrayList{@literal <}NamedResponseType{@literal >} (if the NamedResponseType is an "array")
 *
 * @author yuranos
 */
public class SpringObjectReturnTypeRule implements Rule<JDefinedClass, JType, ApiActionMetadata> {

    @Override
    public JType apply(ApiActionMetadata endpointMetadata, JDefinedClass generatableType) {
        if (!endpointMetadata.getResponseBody().isEmpty()) {
            ApiBodyMetadata apiBodyMetadata =
                    endpointMetadata.getResponseBody().values().iterator().next();
            JClass returnType =
                    findFirstClassBySimpleName(apiBodyMetadata.getCodeModel(), apiBodyMetadata.getName());
            if (apiBodyMetadata.isArray()) {
                JClass arrayType = generatableType.owner().ref(List.class);
                return arrayType.narrow(returnType);
            }
            return returnType;

        }
        return generatableType.owner().ref(Object.class);
    }

}
