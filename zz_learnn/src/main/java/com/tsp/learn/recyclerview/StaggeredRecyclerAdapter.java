package com.tsp.learn.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.tsp.learn.R;
import java.util.ArrayList;

public class StaggeredRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mDatas;
    private ArrayList<Integer> mHeights = new ArrayList<>();

    public StaggeredRecyclerAdapter(Context context, ArrayList<String> datas) {
        mContext = context;
        mDatas = datas;

        if (mDatas.size() > 0) {
            for (int i = 0; i < mDatas.size(); i++) {
                mHeights.add(getRandomHeight());
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new NormalHolder(inflater.inflate(R.layout.item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NormalHolder normalHolder = (NormalHolder) holder;
        normalHolder.mTV.setText(mDatas.get(position));

        ViewGroup.LayoutParams lp = normalHolder.mTV.getLayoutParams();
        lp.height = mHeights.get(position);
        normalHolder.mTV.setLayoutParams(lp);
    }

    private int getRandomHeight() {
        int randomHeight = 0;
        do {
            randomHeight = (int) (Math.random() * 300);
        } while (randomHeight == 0);
        return randomHeight;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder {
        public TextView mTV;

        public NormalHolder(View itemView) {
            super(itemView);

            mTV = (TextView) itemView.findViewById(R.id.item_tv);
            mTV.setOnClickListener(v -> Toast.makeText(mContext, mTV.getText(), Toast.LENGTH_SHORT).show());

        }
    }
}
