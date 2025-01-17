/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.seata.serializer.protobuf.convertor;

import org.apache.seata.serializer.protobuf.generated.RegisterRMRequestProto;
import org.apache.seata.core.protocol.RegisterRMRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 */
public class RegisterRMRequestConvertorTest {

    @Test
    public void convert2Proto() {
        RegisterRMRequest registerRMRequest = new RegisterRMRequest();
        registerRMRequest.setResourceIds("res1");
        registerRMRequest.setVersion("123");
        registerRMRequest.setTransactionServiceGroup("group");
        registerRMRequest.setExtraData("extraData");
        registerRMRequest.setApplicationId("appId");

        RegisterRMRequestConvertor convertor = new RegisterRMRequestConvertor();
        RegisterRMRequestProto proto = convertor.convert2Proto(registerRMRequest);
        RegisterRMRequest real = convertor.convert2Model(proto);

        assertThat((real.getTypeCode())).isEqualTo(registerRMRequest.getTypeCode());
        assertThat((real.getResourceIds())).isEqualTo(registerRMRequest.getResourceIds());
        assertThat((real.getVersion())).isEqualTo(registerRMRequest.getVersion());
        assertThat((real.getTransactionServiceGroup())).isEqualTo(registerRMRequest.getTransactionServiceGroup());
        assertThat((real.getExtraData())).isEqualTo(registerRMRequest.getExtraData());
        assertThat((real.getApplicationId())).isEqualTo(registerRMRequest.getApplicationId());

    }
}
