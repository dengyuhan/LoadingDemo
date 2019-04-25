package com.digizen.loadingdemo

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Environment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.dyhdyh.widget.loadingbar2.LoadingBar
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.SpriteFactory
import com.github.ybq.android.spinkit.Style
import com.github.ybq.android.spinkit.sprite.Sprite
import com.wang.avi.AVLoadingIndicatorView
import com.wang.avi.Indicator
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


/**
 * @author  dengyuhan
 * created 2019/3/25 18:00
 */
class LoadingAdapter : RecyclerView.Adapter<LoadingAdapter.IndicatorHolder> {

    private val INDICATORS = arrayOf(
        "BallPulseIndicator",
        "BallGridPulseIndicator",
        "BallClipRotateIndicator",
        "BallClipRotatePulseIndicator",
        "SquareSpinIndicator",
        "BallClipRotateMultipleIndicator",
        "BallPulseRiseIndicator",
        "BallRotateIndicator",
        "CubeTransitionIndicator",
        "BallZigZagIndicator",
        "BallZigZagDeflectIndicator",
        "BallTrianglePathIndicator",
        "BallScaleIndicator",
        "LineScaleIndicator",
        "LineScalePartyIndicator",
        "BallScaleMultipleIndicator",
        "BallPulseSyncIndicator",
        "BallBeatIndicator",
        "LineScalePulseOutIndicator",
        "LineScalePulseOutRapidIndicator",
        "BallScaleRippleIndicator",
        "BallScaleRippleMultipleIndicator",
        "BallSpinFadeLoaderIndicator",
        "LineSpinFadeLoaderIndicator",
        "TriangleSkewSpinIndicator",
        "PacmanIndicator",
        "BallGridBeatIndicator",
        "SemiCircleSpinIndicator"
    )

    private val Styles = Style.values()


    private var mColor: Int = Color.WHITE

    constructor(color: Int) : super() {
        this.mColor = color
    }

    fun setColor(color: Int) {
        this.mColor = color
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): IndicatorHolder {
        val h = IndicatorHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_indicator, p0, false))
        h.setIsRecyclable(false)
        return h
    }

    override fun getItemCount(): Int {
        return INDICATORS.size + Styles.size
    }

    override fun onBindViewHolder(holder: IndicatorHolder, position: Int) {
        holder.tv_index.text = (position + 1).toString()
        val isIndicators = position < INDICATORS.size
        if (isIndicators) {
            holder.indicatorView.visibility = View.VISIBLE
            holder.kitview.visibility = View.GONE
            holder.indicatorView.setIndicatorColor(mColor)
            holder.indicatorView.setIndicator(INDICATORS[position])
        } else {
            holder.indicatorView.visibility = View.GONE
            holder.kitview.visibility = View.VISIBLE
            val style = Styles[position - INDICATORS.size]
            val drawable = SpriteFactory.create(style)
            holder.kitview.setColor(mColor)
            holder.kitview.setIndeterminateDrawable(drawable)
        }
        holder.itemView.setOnClickListener {
            val view = LayoutInflater.from(it.context).inflate(R.layout.dialog_export, null)
            AlertDialog.Builder(it.context)
                .setTitle("导出序列帧")
                .setView(view)
                .setNegativeButton("取消", null)
                .setPositiveButton("导出", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val ed_width = view.findViewById<EditText>(R.id.ed_width).text.toString()
                        val ed_frame = view.findViewById<EditText>(R.id.ed_frame).text.toString()
                        val outputSize = if (TextUtils.isEmpty(ed_width)) 400 else ed_width.toInt()
                        val outputFrame = if (TextUtils.isEmpty(ed_frame)) 60 else ed_frame.toInt()
                        outputView(outputSize, outputFrame, holder, isIndicators, position)
                    }

                })
                .show()
        }
    }

    fun outputView(width: Int, frameCount: Int, holder: IndicatorHolder, isIndicator: Boolean, position: Int) {
        var drawable: Drawable?=null
        if (isIndicator) {
            val indicator = INDICATORS[position]
            drawable = holder.indicatorView.newIndicator(indicator)
        } else {
            val style = Styles[position - INDICATORS.size]
            drawable = SpriteFactory.create(style)
        }
        if (drawable is Indicator) {
            drawable.color = mColor
        } else if (drawable is Sprite) {
            drawable.color = mColor
        }
        drawable!!.setBounds(0, 0, width, width)
        val format = SimpleDateFormat("yyyyMMdd_HHmmss")
        val dir = File(Environment.getExternalStorageDirectory(), "LoadingDemo/${format.format(System.currentTimeMillis())}")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val count = frameCount.toLong()
        Observable.interval(0, 4000L / count, TimeUnit.MILLISECONDS)//延迟0，间隔1s，单位秒
            .take(count)//限制发射次数（因为倒计时要显示 3 2 1 0 四个数字）
            .map(object : Function<Long, File> {
                override fun apply(t: Long): File {
                    val bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    drawable.draw(canvas)
                    val save = saveFile(bitmap, dir)
                    return save
                }

            }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<File> {
                override fun onSubscribe(d: Disposable) {
                    LoadingBar.dialog(holder.itemView.context)
                        .extras(arrayOf(dir.absolutePath)).show()
                    if (drawable is Animatable) {
                        drawable.start()
                    }
                }

                override fun onNext(t: File) {
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(holder.itemView.context, "导出失败", Toast.LENGTH_LONG).show()
                }

                override fun onComplete() {
                    LoadingBar.dialog(holder.itemView.context).cancel()
                    Toast.makeText(holder.itemView.context, dir.absolutePath, Toast.LENGTH_LONG).show()
                }

            })
    }

    fun saveFile(bitmap: Bitmap, dir: File): File {
        val file = File(dir, "${dir.list().size}.png");
        Files.getFile(file, bitmap)
        return file
    }

    class IndicatorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var indicatorView: AVLoadingIndicatorView
        var kitview: SpinKitView
        var tv_index: TextView


        init {
            indicatorView = itemView.findViewById(R.id.indicator) as AVLoadingIndicatorView
            kitview = itemView.findViewById(R.id.kitview) as SpinKitView
            tv_index = itemView.findViewById(R.id.tv_index) as TextView
        }
    }
}