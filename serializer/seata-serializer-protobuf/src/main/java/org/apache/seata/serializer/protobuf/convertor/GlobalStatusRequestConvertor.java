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

import org.apache.seata.serializer.protobuf.generated.AbstractGlobalEndRequestProto;
import org.apache.seata.serializer.protobuf.generated.AbstractMessageProto;
import org.apache.seata.serializer.protobuf.generated.AbstractTransactionRequestProto;
import org.apache.seata.serializer.protobuf.generated.GlobalStatusRequestProto;
import org.apache.seata.serializer.protobuf.generated.MessageTypeProto;
import org.apache.seata.core.protocol.transaction.GlobalStatusRequest;

/**
 */
public class GlobalStatusRequestConvertor implements PbConvertor<GlobalStatusRequest, GlobalStatusRequestProto> {
    @Override
    public GlobalStatusRequestProto convert2Proto(GlobalStatusRequest globalStatusRequest) {
        final short typeCode = globalStatusRequest.getTypeCode();

        final AbstractMessageProto abstractMessage = AbstractMessageProto.newBuilder().setMessageType(
            MessageTypeProto.forNumber(typeCode)).build();

        final AbstractTransactionRequestProto abstractTransactionRequestProto = AbstractTransactionRequestProto
            .newBuilder().setAbstractMessage(abstractMessage).build();

        final String extraData = globalStatusRequest.getExtraData();
        AbstractGlobalEndRequestProto abstractGlobalEndRequestProto = AbstractGlobalEndRequestProto.newBuilder()
            .setAbstractTransactionRequest(abstractTransactionRequestProto).setXid(globalStatusRequest.getXid())
            .setExtraData(extraData == null ? "" : extraData).build();

        GlobalStatusRequestProto result = GlobalStatusRequestProto.newBuilder().setAbstractGlobalEndRequest(
            abstractGlobalEndRequestProto).build();

        return result;
    }

    @Override
    public GlobalStatusRequest convert2Model(GlobalStatusRequestProto globalStatusRequestProto) {
        GlobalStatusRequest branchCommitRequest = new GlobalStatusRequest();
        branchCommitRequest.setExtraData(globalStatusRequestProto.getAbstractGlobalEndRequest().getExtraData());
        branchCommitRequest.setXid(globalStatusRequestProto.getAbstractGlobalEndRequest().getXid());
        return branchCommitRequest;
    }
}
