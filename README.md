# callrecorder-decrypt
This is a simple command line tool to decrypt Call Recorder's .craf files  

## Download
You can download precompiled jar [here](https://github.com/skvalex/callrecorder-decrypt/releases)

## How to run
Precompiled .jar:
`java -jar craf_decrypt.jar -k [PATH_TO_KEY] password craf_file` 

From sources:
`./gradlew run --args="-k [PATH_TO_KEY] password craf_file"`

To build .jar:
`./gradlew assemble`

## Changelog
### version 1.0
 - init
 
## License
     Copyright 2020 Alexander Skvortsov (skvalex)
 
     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at
 
         http://www.apache.org/licenses/LICENSE-2.0
 
     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.