cmake_minimum_required(VERSION 3.4.1)

add_library(hidex_load SHARED src/main/cpp/HidexLoad.cpp src/main/cpp/Common.cpp)

find_library(log-lib log)

target_link_libraries(hidex_load ${log-lib} )
