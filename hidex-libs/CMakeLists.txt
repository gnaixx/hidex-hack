
cmake_minimum_required(VERSION 3.4.1)

add_library(hidex-lib SHARED src/main/cpp/hidex.cpp src/main/cpp/sha1.cpp)

find_library(log-lib log)

find_library(android-lib android)

target_link_libraries(hidex-lib ${log-lib} ${android-lib})
