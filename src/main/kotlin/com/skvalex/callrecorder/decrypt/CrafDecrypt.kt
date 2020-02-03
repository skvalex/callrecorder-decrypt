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

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.crypto.CipherInputStream

class CrafDecrypt {

    fun decrypt(keyFile: File, password: String, input: File, noMetadata: Boolean): File {
        val crypto = Crypto(keyFile, password)
        val crafInfo = CrafInfo.readFromFile(input)
        val outputFile = File(input.absolutePath.replaceAfterLast(".", crafInfo.audioFormat.extension))
        val maxLength = crafInfo.maxLength
        CipherInputStream(FileInputStream(input), crypto.decryptCipher).use { inputStream ->
            BufferedOutputStream(FileOutputStream(outputFile)).use { outputStream ->
                val buffer = ByteArray(8192)
                var read: Int
                var total: Long = 0
                while (true) {
                    read = if (total + buffer.size < maxLength) {
                        inputStream.read(buffer, 0, buffer.size)
                    } else {
                        inputStream.read(buffer, 0, (maxLength - total).toInt())
                    }
                    if(read <= 0) break
                    if(read + total == maxLength) {
                        val padding = buffer[read - 1]
                        read -= padding
                        total += padding
                    }
                    outputStream.write(buffer, 0, read)
                    total += read
                }
                if (!noMetadata) {
                    outputStream.write(crafInfo.metadata)
                }
                outputStream.flush()
                println("\rsuccess")
            }
        }
        return outputFile
    }
}
