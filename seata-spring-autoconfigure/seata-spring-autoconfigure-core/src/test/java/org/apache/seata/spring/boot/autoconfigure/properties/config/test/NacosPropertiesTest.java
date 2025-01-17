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
package org.apache.seata.spring.boot.autoconfigure.properties.config.test;

import org.apache.seata.common.loader.EnhancedServiceLoader;
import org.apache.seata.config.Configuration;
import org.apache.seata.config.ExtConfigurationProvider;
import org.apache.seata.config.FileConfiguration;
import org.apache.seata.config.nacos.NacosConfiguration;
import org.apache.seata.spring.boot.autoconfigure.BasePropertiesTest;
import org.apache.seata.spring.boot.autoconfigure.properties.config.ConfigNacosProperties;
import org.apache.seata.spring.boot.autoconfigure.provider.SpringApplicationContextProvider;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 */
@org.springframework.context.annotation.Configuration
@Import(SpringApplicationContextProvider.class)
public class NacosPropertiesTest extends BasePropertiesTest {
    @Bean("testConfigNacosProperties")
    public ConfigNacosProperties configNacosProperties() {
        return new ConfigNacosProperties().setServerAddr(STR_TEST_AAA).setDataId(STR_TEST_BBB).setGroup(STR_TEST_CCC).setNamespace(STR_TEST_DDD).setUsername(STR_TEST_EEE).setPassword(STR_TEST_FFF);
    }

    @Test
    public void testConfigNacosProperties() {
        FileConfiguration configuration = mock(FileConfiguration.class);
        Configuration currentConfiguration = EnhancedServiceLoader.load(ExtConfigurationProvider.class).provide(configuration);

        assertEquals(STR_TEST_AAA, currentConfiguration.getConfig(NacosConfiguration.getNacosAddrFileKey()));
        assertEquals(STR_TEST_BBB, currentConfiguration.getConfig(NacosConfiguration.getNacosDataIdKey()));
        assertEquals(STR_TEST_CCC, currentConfiguration.getConfig(NacosConfiguration.getNacosGroupKey()));
        assertEquals(STR_TEST_DDD, currentConfiguration.getConfig(NacosConfiguration.getNacosNameSpaceFileKey()));
        assertEquals(STR_TEST_EEE, currentConfiguration.getConfig(NacosConfiguration.getNacosUserName()));
        assertEquals(STR_TEST_FFF, currentConfiguration.getConfig(NacosConfiguration.getNacosPassword()));
    }
}
