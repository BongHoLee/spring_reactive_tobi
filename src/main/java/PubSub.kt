import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.stream.Collectors.toList
import java.util.stream.Stream

/**
 * Reactive Streams - 표준 API, 스펙
 *
 */
class PubSub {

}

fun main() {
    val iterPublisher = iterPublisher()
    iterPublisher.subscribe(logSub())
}

fun iterPublisher() = object : Publisher<Int> {
    private val iter: Iterable<Int> = Stream.iterate(1) { a -> a + 1 }.limit(10).collect(toList())

    override fun subscribe(subscriber: Subscriber<in Int>?) {
        subscriber!!.onSubscribe(object : Subscription {
            override fun request(n: Long) {
                try {
                    iter.forEach { subscriber.onNext(it) }
                    subscriber.onComplete()
                } catch (t: Throwable) {
                    subscriber.onError(t)
                }
            }

            override fun cancel() {
                // TODO
            }
        })
    }
}

fun logSub() = object : Subscriber<Int> {
    override fun onSubscribe(s: Subscription?) {
        println("onSubscribe")
        s?.request(Long.MAX_VALUE)
    }

    override fun onNext(t: Int?) {
        println("onNext: $t")
    }

    override fun onError(t: Throwable?) {
        println("onError: $t")

    }

    override fun onComplete() {
        println("onComplete")
    }
}


