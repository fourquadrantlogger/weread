package com.fengwo.reading.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

public class PicUtil {
	/**
	 * 将图片截取为圆角图片
	 * 
	 * @param bitmap
	 *            原图片
	 * @param ratio
	 *            截取比例，如果是8，则圆角半径是宽高的1/8，如果是2，则是圆形图片
	 * @return 圆角矩形图片
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float ratio) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, bitmap.getWidth() / ratio,
				bitmap.getHeight() / ratio, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 缩放图片
	 * 
	 * @param imageView
	 * @param picUrl
	 * @param context
	 */
	public static void changePic(final ImageView imageView, String picUrl,
			Context context) {
		BitmapUtils bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(imageView, picUrl, new BitmapLoadCallBack<View>() {
			@Override
			public void onLoadCompleted(View view, String s, Bitmap bitmap,
					BitmapDisplayConfig bitmapDisplayConfig,
					BitmapLoadFrom bitmapLoadFrom) {
				int height = bitmap.getHeight();
				int width = bitmap.getWidth();
				int width1 = imageView.getWidth();
				float x = width1 / width;
				int height1 = (int) (height * x);
				bitmap = Bitmap.createScaledBitmap(bitmap, width1, height1,
						true);
				imageView.setImageBitmap(bitmap);
			}

			@Override
			public void onLoadFailed(View view, String s, Drawable drawable) {

			}
		});
	}

}
