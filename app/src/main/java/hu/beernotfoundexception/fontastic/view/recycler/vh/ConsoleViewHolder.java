package hu.beernotfoundexception.fontastic.view.recycler.vh;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import hu.beernotfoundexception.fontastic.data.ConsoleLine;

public abstract class ConsoleViewHolder extends RecyclerView.ViewHolder {

    public ConsoleViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void setLine(ConsoleLine line);
}
