package com.soloalien.ninepicker;

import android.view.View;

public interface ItemClickListener {
    void OnItemClickListener(View view, int position);
    void OnItemCancelClickListener(View view, int position);
}
