/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("jps-plugin/testData/incremental/js/friendsModuleDisabled")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class IncrementalJsCompilerRunnerWithFriendModulesDisabledTestGenerated extends AbstractIncrementalJsCompilerRunnerWithFriendModulesDisabledTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    public void testAllFilesPresentInFriendsModuleDisabled() throws Exception {
        KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("jps-plugin/testData/incremental/js/friendsModuleDisabled"), Pattern.compile("^([^\\.]+)$"), null, false);
    }

    @TestMetadata("internalInlineFunctionIsChanged")
    public void testInternalInlineFunctionIsChanged() throws Exception {
        runTest("jps-plugin/testData/incremental/js/friendsModuleDisabled/internalInlineFunctionIsChanged/");
    }
}
