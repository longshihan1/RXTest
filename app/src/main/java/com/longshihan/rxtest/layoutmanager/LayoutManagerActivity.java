package com.longshihan.rxtest.layoutmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.longshihan.rxtest.R;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LayoutManagerActivity extends AppCompatActivity {

    @BindView(R.id.layoutmanager_recyclerview)
    RecyclerView layoutmanagerRecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_manager);
        ButterKnife.bind(this);
        layoutmanagerRecyclerview.setLayoutManager(new Test1LayoutManager());
        List<String> mockDatas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mockDatas.add(i + "");
        }
        layoutmanagerRecyclerview.setAdapter(new CommonAdapter<String>(this,
                                                           R.layout.item,
                                                           mockDatas) {
            @Override
            protected void convert(ViewHolder holder, String o, int position) {
                if (position > 0 && position % 7 == 0) {
                    holder.setVisible(R.id.id_tv_title, false);
                    holder.setVisible(R.id.id_tv_desc, false);
                } else {
                    holder.setVisible(R.id.id_tv_title, true);
                    holder.setVisible(R.id.id_tv_desc, true);
                }
            }
        });
    }
}
