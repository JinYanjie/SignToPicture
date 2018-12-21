package com.example.jyj.myapplication

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.davemorrissey.labs.subscaleview.ImageSource
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    val key: String = "hello  ll llll mm nn   nnnnn"
    var isEnable=false
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
        setContentView(R.layout.activity_main)
        btn.setOnClickListener {
            var createBitmap = signView.bitmap
            var mergeBitmap = mergeBitmap(createBitmap)
            imageView.setImage(ImageSource.bitmap(mergeBitmap))
            btn.isEnabled=false
//            downLoadImg(mergeBitmap)
        }
        btnClear.setOnClickListener {
            signView.clear()
            btn.isEnabled=true
        }
    }

    fun mergeBitmap(bmp2: Bitmap): Bitmap {
        var bmp1 = BitmapFactory.decodeResource(resources, R.mipmap.img1)
        var resultBmp = Bitmap.createBitmap(bmp1.width, bmp1.height, Bitmap.Config.RGB_565)
        var canves = Canvas(resultBmp)

        var rect1 = Rect(0, 0, bmp1.width, bmp1.height)
        canves.drawBitmap(bmp1, null, rect1, null)

        val bitmapPool = Glide.get(this).bitmapPool
        val bitmap1 = TransformationUtils.fitCenter(bitmapPool, bmp2, 400, 100)
        Log.e("AAA", "getBitmap: bitmap后~~~  getWidth   " + bitmap1.width)
        Log.e("AAA", "getBitmap: bitmap后~~~  getHeight   " + bitmap1.height)
        canves.drawBitmap(bitmap1, 928f,4345f,null)
        return resultBmp
    }

    /**
     * 检查权限
     */
    @AfterPermissionGranted(100)
    fun checkPermissions() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this,"权限",100, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun downLoadImg(bitmap: Bitmap) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                saveBitmap(bitmap)
            }
        } else {
            saveBitmap(bitmap)
        }
    }

    fun saveBitmap(bitmap: Bitmap) {
        Thread(Runnable {
            try {
                val name = SimpleDateFormat("yyyyMMddHHmmss").format(Date()) + ".jpg"
                val path = File(Environment.getExternalStorageDirectory().toString() + "/DCIM", "images")
                val output = File(path, name)
                val imagePath = output.getPath()
                if (!output.getParentFile().exists()) output.getParentFile().mkdirs()
                val f = File(imagePath)

                val os = FileOutputStream(f)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                os.close()
                val uri = Uri.fromFile(f)
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                runOnUiThread { Toast.makeText(this, "下载成功，请到相册中查看", Toast.LENGTH_SHORT) }
            } catch (e: Exception) {

            }
        }).start()
    }
}
