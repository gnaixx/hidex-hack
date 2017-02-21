cmake_minimum_required(VERSION 3.4.1)

add_library(hidex-load-lib SHARED src/main/cpp/hidex-load-lib.cpp )

find_library(log-lib log)

target_link_libraries(hidex-load-lib ${log-lib} )
