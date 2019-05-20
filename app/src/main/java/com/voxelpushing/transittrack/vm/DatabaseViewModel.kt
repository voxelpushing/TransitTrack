package com.voxelpushing.transittrack.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import com.voxelpushing.transittrack.db.VehicleStatusDatabase
import com.voxelpushing.transittrack.models.VehicleStatus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DatabaseViewModel(application: Application) : AndroidViewModel(application) {

    private val pastVehicleResults by lazy {
        LiveDataReactiveStreams.fromPublisher(
            VehicleStatusDatabase
                .getVehicleStatusDatabase(application.applicationContext)
                .vehicleStatusDao()
                .getAllStatuses()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        )
    }

    private val disposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun getAllVehicleResults(): LiveData<List<VehicleStatus>> {
        return pastVehicleResults
    }

}