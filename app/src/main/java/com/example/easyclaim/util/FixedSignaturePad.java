package com.example.easyclaim.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import com.github.gcacace.signaturepad.views.SignaturePad;

public class FixedSignaturePad extends SignaturePad {

    public FixedSignaturePad(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }
}



