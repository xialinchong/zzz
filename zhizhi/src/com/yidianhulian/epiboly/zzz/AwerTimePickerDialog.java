package com.yidianhulian.epiboly.zzz;

import java.util.Calendar;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AwerTimePickerDialog extends TimePickerDialog {
	public AwerTimePickerDialog(Context context, OnTimeSetListener callBack,
			int hourOfDay, int minute, boolean is24HourView) {
		super(context, callBack, hourOfDay, minute, is24HourView);
	}

	public AwerTimePickerDialog(Context context, OnTimeSetListener callBack,
			Calendar c) {
		super(context, callBack, c.get(Calendar.HOUR_OF_DAY), c
				.get(Calendar.MINUTE), true);
	}

	@Override
	protected void onStop() {
		// super.onStop();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		this.setButton(BUTTON_NEGATIVE, "取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		super.show();
	}

}