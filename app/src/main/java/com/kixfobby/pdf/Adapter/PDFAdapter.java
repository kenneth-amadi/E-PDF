package com.kixfobby.pdf.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kixfobby.pdf.ItemAnimation;
import com.kixfobby.pdf.PDFDoc;
import com.kixfobby.pdf.R;

import java.util.ArrayList;
import java.util.List;

public class PDFAdapter extends RecyclerView.Adapter<PDFAdapter.PDFHolder> {
    private Context context;
    private List<PDFDoc> pdfs = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;

    public interface OnItemClickListener {
        void onItemClick(View view, PDFDoc obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public PDFAdapter(Context context, List<PDFDoc> pdfs, int animation_type) {
        this.pdfs = pdfs;
        this.context = context;
        this.animation_type = animation_type;
    }

    public class PDFHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public View parent;

        public PDFHolder(View view) {
            super(view);

            name = view.findViewById(R.id.name);
            image = view.findViewById(R.id.image);
            parent = view.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public PDFHolder onCreateViewHolder(ViewGroup vGroup, int viewType) {
        View v = LayoutInflater.from(vGroup.getContext()).inflate(R.layout.item_pdf, vGroup, false);
        return new PDFHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PDFHolder holder, final int position) {
        Log.e("onBindViewHolder", "onBindViewHolder : " + position);

        holder.name.setText(position + 1 + " ~ " + pdfs.get(position).getName());
        holder.image.setImageResource(R.drawable.pdf_icon);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, pdfs.get(position), position);
                }
            }
        });
        setAnimation(holder.itemView, position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView rView) {
        rView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView rV, int newState) {
                on_attach = false;
                super.onScrollStateChanged(rV, newState);
            }
        });
        super.onAttachedToRecyclerView(rView);
    }

    @Override
    public int getItemCount() {
        return pdfs.size();
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

}
