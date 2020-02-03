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

import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

data class CrafInfo(
        val version: Byte,
        val audioFormat: AudioFormat,
        val channels: Byte,
        val sampleRate: Int,
        val duration: Int,
        val maxLength: Long
) {

    lateinit var metadata: ByteArray
        private set

    companion object {
        private const val TAG_ID_V1 = 1609
        private const val TAG_ID_V2 = 1610
        private const val LENGTH = 15

        @JvmStatic
        fun readFromFile(crafFile: File): CrafInfo {
            RandomAccessFile(crafFile, "r").use { file ->
                file.seek(crafFile.length() - 8)
                val infoLength = file.readInt()
                val tag = file.readInt()
                if (tag == TAG_ID_V1 || tag == TAG_ID_V2) {
                    val maxLength = crafFile.length() - infoLength - LENGTH
                    file.seek(maxLength)
                    val c = file.readByte()
                    val r = file.readByte()
                    val a = file.readByte()
                    val f = file.readByte()
                    if (c == 'C'.toByte() && r == 'R'.toByte() && a == 'A'.toByte() && f == 'F'.toByte()) {
                        val version = file.readByte()
                        val audioFormat = file.readByte()
                        val channels = file.readByte()
                        val sampleRate = file.readInt()
                        val duration = file.readInt()

                        val crafInfo = CrafInfo(version, AudioFormat.findById(audioFormat.toInt()), channels, sampleRate, duration, maxLength)
                        crafInfo.metadata = ByteArray(infoLength)
                        file.readFully(crafInfo.metadata)

                        return crafInfo
                    }
                }
            }
            throw IOException("no craf info found in $crafFile")
        }
    }
}
