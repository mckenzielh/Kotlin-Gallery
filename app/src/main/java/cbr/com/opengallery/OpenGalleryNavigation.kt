package cbr.com.opengallery

import android.Manifest
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import io.reactivex.Observable
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.recyclerview.*
import java.io.File

/** Created by Dimitrios on 12/7/2017.*/
class OpenGalleryNavigationActivity : PermissionActivity(), AdapterSelectionListener {
    
    private val mAdapter = OpenGalleryAdapter(this@OpenGalleryNavigationActivity)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        setupUi()
    }
    
    private fun setupUi() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(GalleryDecorator(this))
        recyclerView.adapter = mAdapter
    }
    
    override fun onPostResume() {
        super.onPostResume()
        checkPermissions()
    }
    
    private fun checkPermissions() {
        if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.permission_rational_read_external), REQUEST_STORAGE_READ_ACCESS_PERMISSION)
            return
        } else if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(R.string.permission_rational_read_external), REQUEST_STORAGE_WRITE_ACCESS_PERMISSION)
            return
        } else if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            fetchAllMediaFiles()
        }
    }
    
    override fun onItemSelected(file: File) {
    
    }
    
    private fun fetchAllMediaFiles() {
        Observable.just(sortImagesByFolder(getAllImages(this)))
                .doOnSubscribe({ disposable -> compositeDisposable.add(disposable) })
                .subscribe(
                        { filesMap -> mAdapter.setItems(filesMap) },
                        { throwable: Throwable? -> handleError(throwable) }
                )
    }
}