package com.gattal.asta.myediary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.gattal.asta.myediary.dataBase.DiaryEntry;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class myDiaryAdapter extends RecyclerView.Adapter<myDiaryAdapter.myDiaryViewHolder> {

    private static final String TAG = "myDiaryAdapter";
    private int mNumberOfItems;
    final private ListItemClickListener mOnClickListener;
    private List<DiaryEntry> mDiaryEntries;

    public List<DiaryEntry> getmDiaryEntries() {
        return mDiaryEntries;
    }

    public void setmDiaryEntries(List<DiaryEntry> diaryEntries) {

        this.mDiaryEntries = diaryEntries;
        notifyDataSetChanged();
    }

    public myDiaryAdapter(int numberOfItems, ListItemClickListener listener){
        mNumberOfItems = numberOfItems;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public myDiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG,"viewType: "+viewType);
        Context context = parent.getContext();
        int layoutIdForListItem ;

        if(getItemCount()==0) layoutIdForListItem = R.layout.diary_entry_empty;
        else  layoutIdForListItem = R.layout.diary_entry;

        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(layoutIdForListItem,parent,false);

        return new myDiaryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myDiaryViewHolder holder, int position) {

        Log.d(TAG,"#"+position);
        if(getItemCount()!=0)
        holder.bind(mDiaryEntries.get(position));
    }

    @Override
    public int getItemCount() {
        if(mDiaryEntries == null) mNumberOfItems = 0;
        else mNumberOfItems = mDiaryEntries.size();
        Log.d(TAG,"numberOfItems : "+mNumberOfItems);
        return mNumberOfItems;
    }

    public class myDiaryViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        TextView _diaryDate;
        TextView _diaryTitle;

        myDiaryViewHolder(View itemView) {
            super(itemView);

            _diaryDate = itemView.findViewById(R.id.diary_date);
            _diaryTitle = itemView.findViewById(R.id.diary_title);
            itemView.setOnClickListener(this);
        }

        void bind (DiaryEntry diaryEntry){

            _diaryTitle.setText(String.valueOf(diaryEntry.getTitle()));

            SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("dd - MMM - yyyy");
            dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+1"));
            _diaryDate.setText(dateTimeInGMT.format(diaryEntry.getDate()));
        }

        @Override
        public void onClick(View view) {

            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    public interface  ListItemClickListener{
        void onListItemClick(int clickedItemIndex);

    }

}
