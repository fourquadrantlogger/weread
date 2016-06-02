package com.fengwo.reading.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fengwo.reading.R;
import com.fengwo.reading.utils.localdata.SPUtils;

public class SetupFragment3 extends Fragment {


	private SetupFragment3() {
	}

	private static SetupFragment3 fragment = new SetupFragment3();

	public static SetupFragment3 getInstance() {
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_setup, container, false);

		((LinearLayout) view.findViewById(R.id.ll_activity_next))
				.setBackgroundResource(R.drawable.setup3);
		view.findViewById(R.id.bt_setup_add).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(fragment.getActivity(),
								LoginActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("key", 1);
						intent.putExtras(bundle);
						startActivity(intent);
						fragment.getActivity().finish();
						SPUtils.setAppFirst(fragment.getActivity());
					}
				});
		
		return view;
	}

}
