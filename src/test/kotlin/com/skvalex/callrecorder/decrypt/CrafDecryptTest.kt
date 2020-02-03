/*
 * Copyright 2020 Alexander Skvortsov (skvalex)
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

package com.skvalex.callrecorder.decrypt

import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class CrafDecryptTest {
    @Test
    fun testDecrypt() {
        val crafDecrypt = CrafDecrypt()
        val outputFile = crafDecrypt.decrypt("callrecorder.key".asResourceFile(), "test", "encrypted.craf".asResourceFile(), false)
        assertTrue(outputFile.exists(), "output file $outputFile does not exist")
        val sameAsOriginal = FileUtils.contentEquals("original.opus".asResourceFile(), outputFile)
        assertTrue(sameAsOriginal, "resulting file is not equal to the original")
    }

    private fun String.asResourceFile(): File {
        val url = CrafDecryptTest::class.java.classLoader.getResource(this)
        return if (url != null) File(url.toURI()) else File(this)
    }
}
