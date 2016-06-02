package com.fengwo.reading.view;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 
 *  @author lxq 加载图片 由于ListView与下拉刷新的Scroll事件冲突, 使用这个ScrollListener可以避免ListView滑动异常
 * 
 */
public class SwpipeListViewOnScrollListener implements
		AbsListView.OnScrollListener {

	private SwipeRefreshLayout mSwipeView;
	private AbsListView.OnScrollListener mOnScrollListener;

	public SwpipeListViewOnScrollListener(SwipeRefreshLayout swipeView) {
		super();
		this.mSwipeView = swipeView;
	}

	public SwpipeListViewOnScrollListener(SwipeRefreshLayout swipeView,
			OnScrollListener onScrollListener) {
		super();
		this.mSwipeView = swipeView;
		this.mOnScrollListener = onScrollListener;
	}

	@Override
	public void onScrollStateChanged(AbsListView absListView, int i) {
	}

	@Override
	public void onScroll(AbsListView absListView, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		View firstView = absListView.getChildAt(firstVisibleItem);

		// 当firstVisibleItem是第0位。如果firstView==null说明列表为空，需要刷新;或者top==0说明已经到达列表顶部,
		// 也需要刷新
		if (firstVisibleItem == 0
				&& (firstView == null || firstView.getTop() == 0)) {
			mSwipeView.setEnabled(true);
		} else {
			mSwipeView.setEnabled(false);
		}
		if (null != mOnScrollListener) {
			mOnScrollListener.onScroll(absListView, firstVisibleItem,
					visibleItemCount, totalItemCount);
		}
	}
}