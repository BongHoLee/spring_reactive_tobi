package Ob.live1_2

import java.util.concurrent.Executors
import java.util.concurrent.Flow.*
import java.util.concurrent.TimeUnit

class PubSub

fun main() {
    val listOf: Iterable<Int> = listOf(1, 2, 3, 4, 5)
    val es = Executors.newCachedThreadPool()

    // Publisher는 데이터를 주는쪽 : 구독 방식이기 때문에 subscribe라는 오퍼레이션
    val publisher = object : Publisher<Any> {
        override fun subscribe(subscriber: Subscriber<in Any>) {
            val iter = listOf.iterator()
            // Publisher.subscribe(Subscriber) 오퍼레이션 호출되면
            // Subscriber.onSubscribe(Subscription) 메서드가 호출되어야함.
            // call back 처럼 subscriber에게 메시지를 보내는 형국..
            subscriber.onSubscribe(
                object : Subscription {
                    override fun request(n: Long) {
                        es.submit {
                            var wants = n
                            try {
                                while (wants-- > 0) {
                                    when {
                                        iter.hasNext() -> {
                                            subscriber.onNext(iter.next())
                                        }
                                        else -> {
                                            subscriber.onComplete()
                                            break
                                        }
                                    }
                                }
                            } catch (e: RuntimeException) {
                                subscriber.onError(e)
                            }
                        }
                    }

                    override fun cancel() {
                    }
                }
            )
        }
    }

    // Subscriber는 데이터를 받는 쪽. 스펙대로 4개의 오퍼레이션이 존재한다.
    val subscriber = object : Subscriber<Any> {
        lateinit var subscription: Subscription
        override fun onSubscribe(subscription: Subscription) {
            println("onSubscribe")
            println("${Thread.currentThread().name} onSubscribe")

            // Publisher에게 "나는 데이터를 어떠어떠하게 받겠어" 라고 Subscription을 통해 요구해야함.
            // back pressure 개념 - publisher와 subscriber 사이의 속도 차이 등
            // 최초의 Subscription.request(갯수)를 onSubscribe 메서드 내에서 해야한다.
            this.subscription = subscription
            subscription.request(1)
        }

        // Observer 패턴의 update와 비슷
        // 데이터를 하나씩 끌어 오는 것
        override fun onNext(item: Any) {
            println("${Thread.currentThread().name} onNext ${item}")
            subscription.request(1)
        }

        // Error가 발생했을 때 Exception을 넘겨줘
        // Publisher에서 예외가 발생하더라도 onError를 타고 넘어오게
        override fun onError(throwable: Throwable) {
            println("onError")
        }

        // Publisher가 더이상 줄 데이터가 없을 떄 호출
        override fun onComplete() {
            println("onComplete")
        }
    }

    // publisher 구독
    publisher.subscribe(subscriber)

    es.awaitTermination(10, TimeUnit.SECONDS)
    es.shutdown()
}
