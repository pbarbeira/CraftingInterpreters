cmake_minimum_required(VERSION 3.30)
project(cpp)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_SOURCE_DIR})

add_executable(jlox src/java/main.cpp)

add_executable(jlox-ast tools/java/GenerateAst.cpp)

add_executable(clox src/c/main.cpp)

