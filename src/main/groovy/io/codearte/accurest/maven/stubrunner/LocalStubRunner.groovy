/**
 *
 *  Copyright 2013-2016 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.codearte.accurest.maven.stubrunner

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.cloud.contract.stubrunner.StubConfiguration
import org.springframework.cloud.contract.stubrunner.StubRunner
import org.springframework.cloud.contract.stubrunner.StubRunnerOptions

import javax.inject.Named

@Named
@CompileStatic
@Slf4j
class LocalStubRunner {

    StubRunner run(String contractsDir, StubRunnerOptions options) {
        log.info("Launching StubRunner with contracts from ${contractsDir}")
        StubRunner stubRunner = new StubRunner(options, contractsDir, new StubConfiguration(contractsDir))
        stubRunner.runStubs()
        return stubRunner
    }
}
