package com.fengwo.reading.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.fengwo.reading.R;
import com.fengwo.reading.application.MyApplication;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 
 * @author lxq 加载图片
 * 
 */
public class DisplayImageUtils {

	/**
	 * ImageLoader加载图片---displayImage
	 */
	public static void displayImage(String url, ImageView imageView,
			int cornerRadiusPixels, int imageRes) {
		// 设置RoundedBitmapDisplayer---必须设置imageView的长宽？？
		// 设置图片Uri为空或是错误的时候显示的图片
		// 设置图片加载或解码过程中发生错误显示的图片
		// 设置下载的图片是否缓存在内存中
		// 设置成圆角图片
		// 设置图片的解码类型
		// 创建配置过得DisplayImageOption对象
		if (cornerRadiusPixels == -1) {
			MyApplication.getImageLoader().displayImage(
					url,
					imageView,
					new DisplayImageOptions.Builder()
							.showImageForEmptyUri(imageRes)
							.showImageOnFail(imageRes).cacheInMemory(true)
							.bitmapConfig(Bitmap.Config.RGB_565).build());
		} else {
			MyApplication.getImageLoader().displayImage(
					url,
					imageView,
					new DisplayImageOptions.Builder()
							.showImageForEmptyUri(imageRes)
							.showImageOnFail(imageRes)
							.cacheInMemory(true)
							.displayer(
									new RoundedBitmapDisplayer(
											cornerRadiusPixels))
							.bitmapConfig(Bitmap.Config.RGB_565).build());
		}
	}

	/**
	 * ImageLoader加载图片---loadImage
	 */
	public static void loadImage(String url, final ImageView imageView,
			Context context) {
		// ImageLoader.getInstance().loadImage(url,
		// new SimpleImageLoadingListener() {
		// @Override
		// public void onLoadingComplete(String imageUri, View view,
		// Bitmap loadedImage) {
		// // super.onLoadingComplete(imageUri, view, loadedImage);
		// System.out.println("==========okoko");
		// imageView
		// .setImageBitmap(getBitmapFromBitmapMiddle(loadedImage));
		// }
		// });

		BitmapUtils bitmapUtils = new BitmapUtils(context);
		// bitmapUtils.display(imageView, url);
		// bitmapUtils.configDefaultLoadingImage(R.color.img_bg);
		// bitmapUtils.configDefaultLoadFailedImage(R.color.img_bg);
		bitmapUtils.display(imageView, url,
				new BitmapLoadCallBack<ImageView>() {

					@Override
					public void onLoading(ImageView container, String uri,
							BitmapDisplayConfig config, long total, long current) {
						super.onLoading(container, uri, config, total, current);
					}

					@Override
					public void onLoadStarted(ImageView container, String uri,
							BitmapDisplayConfig config) {
						super.onLoadStarted(container, uri, config);
					}

					@Override
					public void onPreLoad(ImageView container, String uri,
							BitmapDisplayConfig config) {
						super.onPreLoad(container, uri, config);
					}
					
					@Override
					public void onLoadCompleted(ImageView imageView, String s,
							Bitmap bitmap,
							BitmapDisplayConfig bitmapDisplayConfig,
							BitmapLoadFrom bitmapLoadFrom) {
						imageView
								.setImageBitmap(getBitmapFromBitmapMiddle(bitmap));
					}

					@Override
					public void onLoadFailed(ImageView imageView, String s,
							Drawable drawable) {
						imageView.setImageResource(R.drawable.cover);
					}
				});
	}

	public static Bitmap getBitmapFromBitmapMiddle(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		// 得到图片的长和宽
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		float percent = 1;

		int size = (int) (width > height ? height * percent : width * percent);

		if (width < size && height < size) {
			return bitmap;
		}
		if (width < size && height >= size) {
			return Bitmap.createBitmap(bitmap, width, (height - size) / 2,
					size, size);
		}
		if (width >= size && height < size) {
			return Bitmap.createBitmap(bitmap, (width - size) / 2, height,
					size, size);
		}
		return Bitmap.createBitmap(bitmap, (width - size) / 2,
				(height - size) / 2, size, size);
	}

	// 测试
	private static void loadImage(String url, final ImageView imageView,
			final int imageResStarted, final int imageResFailed,
			final int imageResCancelled) {
		ImageLoader.getInstance().loadImage(
				url,
				new DisplayImageOptions.Builder()
						.showImageForEmptyUri(imageResStarted)
						.showImageOnFail(imageResFailed).cacheInMemory(true)
						.bitmapConfig(Bitmap.Config.RGB_565).build(),
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						imageView.setImageResource(imageResStarted);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						imageView.setImageResource(imageResFailed);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// imageView.setImageBitmap(loadedImage);
						setImageBitmapAnim(imageView, loadedImage);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						imageView.setImageResource(imageResCancelled);
					}
				});
		// ImageLoader.getInstance().loadImage(uri, targetImageSize, options,
		// listener, progressListener)
	}

	private static void setImageBitmapAnim(final ImageView imageView,
			final Bitmap bm) {
		getAlphaOut().setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// imageView.setImageDrawable(new
				// BitmapDrawable(context.getResources(), bm));
				imageView.setImageBitmap(bm);
				imageView.startAnimation(getAlphaIn());
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		imageView.startAnimation(getAlphaOut());
	}

	private static AlphaAnimation getAlphaOut() {
		AlphaAnimation alphaOut = new AlphaAnimation(1.0f, 0f);
		alphaOut.setFillAfter(true);
		alphaOut.setDuration(200);
		return alphaOut;
	}

	private static AlphaAnimation getAlphaIn() {
		AlphaAnimation alphaIn = new AlphaAnimation(0f, 1.0f);
		alphaIn.setFillAfter(true);
		alphaIn.setDuration(200);
		return alphaIn;
	}

}