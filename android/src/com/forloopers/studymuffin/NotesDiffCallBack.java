package com.forloopers.studymuffin;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.io.File;
import java.util.List;

public class NotesDiffCallBack extends DiffUtil.Callback {

    private List<File> oldList;
    private List<File> newList;

    public NotesDiffCallBack(List<File> oldList, List<File> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getName().equals(newList.get(newItemPosition).getName());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).lastModified() < (System.currentTimeMillis() - 5000);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }

}
