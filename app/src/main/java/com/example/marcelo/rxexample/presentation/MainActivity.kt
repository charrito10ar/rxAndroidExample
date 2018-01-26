package com.example.marcelo.rxexample.presentation

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.marcelo.rxexample.R
import com.example.marcelo.rxexample.presentation.interfaces.MainActivityView
import kotlinx.android.synthetic.main.activity_main.*
import rx.Observable
import rx.Observable.OnSubscribe
import rx.Observable.create
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivity : AppCompatActivity(), MainActivityView {

    override fun showLoading() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress_bar.visibility = View.GONE
    }

    lateinit var operationObservable: Observable<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        operationObservable = create(object : OnSubscribe<String> {
            override fun call(subscriber: Subscriber<in String>?) {
                subscriber!!.onNext(longRunningOperation())
                subscriber!!.onCompleted()
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun clickButton(view: View){
        view.isEnabled = false
        operationObservable.subscribe(object : Subscriber<String>() {
            override fun onCompleted() {
                view.isEnabled = true
            }
            override fun onError(e: Throwable) {}

            override fun onNext(value: String) {
                Toast.makeText(view.context, value, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun longRunningOperation(): String {
        try {
            Thread.sleep(5000)
        } catch (e: InterruptedException) {
            // error
        }
        return "Complete!"
    }
}