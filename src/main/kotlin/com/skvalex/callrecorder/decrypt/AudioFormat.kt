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

enum class AudioFormat(val id: Int, val extension: String) {
    FORMAT_WAV(0, "wav"),
    FORMAT_AMR_NB(1, "amr"),
    FORMAT_AAC(2, "aac"),
    FORMAT_3GP(3, "3gp"),
    FORMAT_MP3(4, "mp3"),
    FORMAT_CRAF(5, "craf"),
    FORMAT_FLAC(6, "flac"),
    FORMAT_OPUS(7, "opus"),
    FORMAT_M4A(8, "m4a"),
    FORMAT_3GPP(9, "3gpp"),
    FORMAT_AMR_WB(10, "amr");

    companion object {
        fun findById(id: Int) = values().first { it.id == id }
    }
}
