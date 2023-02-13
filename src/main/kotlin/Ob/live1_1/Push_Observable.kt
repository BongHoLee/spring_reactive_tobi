package Ob.live1_1

import java.util.Observable
import java.util.Observer

class Push_Observable

@Suppress("DEPRECATION")
class IntObservable : Observable(), Runnable {
    // 이 Observable에다가 Observer들이 막 등록을 한다.(Observer에게 데이터를 얻고싶어하는 녀석들)
    override fun run() {
        for (i in 1..10) {
            setChanged()
            notifyObservers(i)
        }
    }
}

@Suppress("DEPRECATION")
fun main() {
    val observer = object : Observer {
        override fun update(o: Observable?, arg: Any?) {
            println(arg)
        }
    }

    val observable = IntObservable()
    observable.addObserver(observer)        // Observable에 Observer를 등록

    observable.run()
}
