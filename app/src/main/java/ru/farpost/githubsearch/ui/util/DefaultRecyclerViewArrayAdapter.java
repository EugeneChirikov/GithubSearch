package ru.farpost.githubsearch.ui.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eugene on 12/1/16.
 */

public abstract class DefaultRecyclerViewArrayAdapter<T,
        HolderT extends DefaultRecyclerViewArrayAdapter.DefaultViewHolder> extends RecyclerView.Adapter<HolderT> {
    public interface OnItemClickListener<T> {
        void onItemClicked(View v, T item);
    }

    protected List<T> mItems = new ArrayList<>();
    protected Context mContext;
    protected LayoutInflater mInflater;
    private OnItemClickListener<T> mOnItemClickListener;

    public DefaultRecyclerViewArrayAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setItems(List<T> items) {
        if (items == null) {
            mItems.clear();
        } else {
            mItems = items;
        }
        notifyDataSetChanged();
    }

    public void addItems(List<T> items) {
        if (items != null) {
            int nextItemPosition = mItems.size();
            mItems.addAll(items);
            notifyItemRangeInserted(nextItemPosition, items.size());
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        mOnItemClickListener = listener;
    }

    public class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public DefaultViewHolder(View view) {
            super(view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == itemView.getId() && mOnItemClickListener != null) {
                try {
                    mOnItemClickListener.onItemClicked(v, mItems.get(getAdapterPosition()));
                } catch (ArrayIndexOutOfBoundsException ignored) {}
            }
        }
    }
}
