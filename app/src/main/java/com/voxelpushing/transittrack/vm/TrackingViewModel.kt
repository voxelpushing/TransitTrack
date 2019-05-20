package com.voxelpushing.transittrack.vm

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.voxelpushing.transittrack.db.VehicleStatusDatabase
import com.voxelpushing.transittrack.models.ResponseState
import com.voxelpushing.transittrack.models.VehicleLocationResponse
import com.voxelpushing.transittrack.models.VehicleStatus
import com.voxelpushing.transittrack.models.VehicleStatusLiveDataContainer
import com.voxelpushing.transittrack.services.NextBusApiService
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class TrackingViewModel(application: Application) : AndroidViewModel(application) {

    private val nextBusApiService by lazy {
        NextBusApiService.create()
    }

    private val statusLiveData = MutableLiveData<VehicleStatusLiveDataContainer>()
    private var disposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    // Get vehicle status and location from vehicle number
    fun getVehicleStatus(vehicleNumber: String?) {
        if (!vehicleNumber.isNullOrEmpty()) {
            statusLiveData.value = VehicleStatusLiveDataContainer(ResponseState.LOADING, null)
            disposable.add(
                nextBusApiService.getVehicleLocation(
                    "ttc", vehicleNumber
                ).flatMap { vlr -> getVehicleRouteName(vlr) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ data ->
                        statusLiveData.value = VehicleStatusLiveDataContainer(ResponseState.SUCCESS, data)
                        saveVehicleResult(data)
                    }, {
                        statusLiveData.value = VehicleStatusLiveDataContainer(ResponseState.ERROR, null)
                    })
            )
        }
    }

    private fun getVehicleRouteName(vlr: VehicleLocationResponse.Result): Observable<VehicleStatus> {
        return if (vlr.vehicle.routeTag == 0) {
            Observable.just(VehicleStatus(vlr, null, System.currentTimeMillis()))
        } else {
            nextBusApiService.getRouteConfig("ttc", vlr.vehicle.routeTag.toString())
                .flatMap { result -> Observable.fromIterable(result.route.direction) }
                .filter { direction -> direction.tag == (vlr.vehicle.dirTag ?: "") }
                .toList()
                .flatMap { list ->
                    Single.just(
                        VehicleStatus(
                            vlr,
                            list.firstOrNull()?.title,
                            System.currentTimeMillis()
                        )
                    )
                }
                .toObservable()
        }
    }

    fun getStatus(): LiveData<VehicleStatusLiveDataContainer> = statusLiveData

    @SuppressLint("CheckResult")
    private fun saveVehicleResult(vehicleStatus: VehicleStatus) {
        Completable.fromAction {
            VehicleStatusDatabase.getVehicleStatusDatabase(getApplication())
                .vehicleStatusDao()
                .insertVehicleStatus(vehicleStatus)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

}