@echo off
"C:\\CookAndroid\\SDK\\cmake\\3.18.1\\bin\\cmake.exe" ^
  "-HC:\\CookAndroid\\Project\\RPM_APP\\sdk\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=26" ^
  "-DANDROID_PLATFORM=android-26" ^
  "-DANDROID_ABI=armeabi-v7a" ^
  "-DCMAKE_ANDROID_ARCH_ABI=armeabi-v7a" ^
  "-DANDROID_NDK=C:\\CookAndroid\\SDK\\ndk\\23.1.7779620" ^
  "-DCMAKE_ANDROID_NDK=C:\\CookAndroid\\SDK\\ndk\\23.1.7779620" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\CookAndroid\\SDK\\ndk\\23.1.7779620\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\CookAndroid\\SDK\\cmake\\3.18.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\CookAndroid\\Project\\RPM_APP\\sdk\\build\\intermediates\\cxx\\Debug\\g3c2j6i2\\obj\\armeabi-v7a" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\CookAndroid\\Project\\RPM_APP\\sdk\\build\\intermediates\\cxx\\Debug\\g3c2j6i2\\obj\\armeabi-v7a" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\CookAndroid\\Project\\RPM_APP\\sdk\\.cxx\\Debug\\g3c2j6i2\\armeabi-v7a" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
