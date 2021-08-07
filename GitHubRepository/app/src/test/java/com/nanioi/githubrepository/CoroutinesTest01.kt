package com.nanioi.githubrepository

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.system.measureTimeMillis

class CoroutinesTest01 {

    @Test
    fun test01() = runBlocking {
        val time = measureTimeMillis {
            val name = getFirstName()
            val lastName = getLastName()
            print("Hello, $name $lastName")
        }
        print("measure time : $time")
    }

    @Test
    fun test02() = runBlocking {
        val time = measureTimeMillis {
            val name = async { getFirstName() }
            val lastName = async { getLastName() }
            print("Hello, ${name.await()} ${lastName.await()}")
        }
        print("measure time : $time")
    }
    //api 같은거 이용시 async 이용 시 순차적으로 호출안하고 비동기적으 동시에 여러개 호출, 반환 가능

    suspend fun getFirstName(): String {
        delay(1000)
        return "이"
    }

    suspend fun getLastName(): String {
        delay(1000)
        return "기정"
    }



}
