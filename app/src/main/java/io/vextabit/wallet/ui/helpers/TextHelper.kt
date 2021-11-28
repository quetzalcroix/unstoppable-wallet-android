package io.vextabit.wallet.ui.helpers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.vextabit.wallet.R
import io.vextabit.wallet.core.App
import io.vextabit.wallet.core.FeeRatePriority
import io.vextabit.wallet.core.IClipboardManager
import io.horizontalsystems.views.helpers.LayoutHelper

object TextHelper : IClipboardManager {

    override val hasPrimaryClip: Boolean
        get() = clipboard?.hasPrimaryClip() ?: false

    override fun copyText(text: String) {
        copyTextToClipboard(io.vextabit.wallet.core.App.instance, text)
    }

    override fun getCopiedText(): String {
        return clipboard?.primaryClip?.itemCount?.let { count ->
            if (count > 0) {
                clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
            } else {
                null
            }
        } ?: ""
    }

    fun getQrCodeBitmap(address: String, size: Float = 150F): Bitmap? {
        val multiFormatWriter = MultiFormatWriter()
        return try {
            val imageSize = LayoutHelper.dp(size, io.vextabit.wallet.core.App.instance)
            val bitMatrix = multiFormatWriter.encode(address, BarcodeFormat.QR_CODE, imageSize, imageSize, hashMapOf(EncodeHintType.MARGIN to 0))
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    fun getFeeRatePriorityString(context: Context, priority: FeeRatePriority): String = when (priority) {
        FeeRatePriority.LOW -> context.getString(R.string.Send_TxSpeed_Low)
        FeeRatePriority.RECOMMENDED -> context.getString(R.string.Send_TxSpeed_Recommended)
        FeeRatePriority.HIGH -> context.getString(R.string.Send_TxSpeed_High)
        is FeeRatePriority.Custom -> context.getString(R.string.Send_TxSpeed_Custom)
    }

    fun getCleanedUrl(link: String): String{
        var cleanedUrl = link.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)".toRegex(),"")
        if (cleanedUrl.endsWith("/")) {
            cleanedUrl = cleanedUrl.substring(0, cleanedUrl.length - 1)
        }
        return cleanedUrl
    }

    private val clipboard: ClipboardManager?
        get() = io.vextabit.wallet.core.App.instance.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager

    private fun copyTextToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("text", text)
        clipboard?.setPrimaryClip(clip)
    }

}
