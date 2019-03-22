package com.example.testapis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

class MyAdapter extends BaseAdapter {
        private Context context;
        private List<DataBean.SubjectsBean> list;
        public MyAdapter(Context context, List<DataBean.SubjectsBean> list) {
            this.context = context;
            this.list = list;
        }
        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder mHolder;
            if (convertView == null) {
                mHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.item, null, true);
                mHolder.categoryTv = convertView.findViewById(R.id.categoryTv);
                mHolder.nameTv = convertView.findViewById(R.id.nameTv);
                mHolder.iv = convertView.findViewById(R.id.image);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            String name = list.get(position).getOriginal_title();
            String category = list.get(position).getGenres().get(0);
            mHolder.nameTv.setText(name);
            mHolder.categoryTv.setText("类型["+category+"]");
            //显示图片
            Glide.with(context).load(list.get(position).getImages().getSmall()).into(mHolder.iv);
            return convertView;
        }

        class ViewHolder {
            private ImageView iv;
            private TextView nameTv,categoryTv;
        }
    }

