package hu.beernotfoundexception.fontastic.view;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import hu.beernotfoundexception.fontastic.R;
import hu.beernotfoundexception.fontastic.data.ConsoleLine;
import hu.beernotfoundexception.fontastic.view.recycler.vh.ConsoleViewHolder;

public class ConsoleRecyclerAdapter extends RecyclerView.Adapter<ConsoleViewHolder> {

    private final List<ConsoleLine> lines = new LinkedList<>();

    public ConsoleRecyclerAdapter() {
    }

    public void log(String s) {
        addConsoleLine(0, new ConsoleLine(ConsoleLine.TYPE_STRING, s));
    }

    public void log(Bitmap bitmap) {
        addConsoleLine(0, new ConsoleLine(ConsoleLine.TYPE_IMAGE, bitmap));
    }

    @Override
    public ConsoleViewHolder onCreateViewHolder(ViewGroup parent, @ConsoleLine.LineType int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ConsoleLine.TYPE_IMAGE:
                return new ViewHolderImage(inflater.inflate(R.layout.card_console_image, parent, false));
            case ConsoleLine.TYPE_STRING:
                return new ViewHolderMessage(inflater.inflate(R.layout.card_console_text, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ConsoleViewHolder holder, int position) {
        holder.setLine(lines.get(position));
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }

    @Override
    @ConsoleLine.LineType
    public int getItemViewType(int position) {
        return lines.get(position).type;
    }

    public void clear() {
        animateTo(Collections.<ConsoleLine>emptyList());
    }

    public synchronized void removeConsoleLine(ConsoleLine consoleLine) {
        int position = lines.indexOf(consoleLine);
        lines.remove(position);
        if (position != 0) notifyDataSetChanged();
        else notifyItemRemoved(position);
    }

    public void removeConsoleLine(Iterator iter, int pos) {
        iter.remove();
        notifyItemRemoved(pos);
    }

    public void addConsoleLine(ConsoleLine consoleLine) {
        lines.add(consoleLine);
        int position = lines.indexOf(consoleLine);
        notifyItemInserted(position);
    }

    public void addConsoleLine(int position, ConsoleLine consoleLine) {
        lines.add(position, consoleLine);
        notifyItemInserted(position);
    }

    public void moveTagCategories(int fromPosition, int toPosition) {
        final ConsoleLine consoleLine = lines.remove(fromPosition);
        lines.add(toPosition, consoleLine);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<ConsoleLine> tags) {
        applyAndAnimateRemovals(tags);
        applyAndAnimateAdditions(tags);
        applyAndAnimateMovedItems(tags);
    }

    private void applyAndAnimateRemovals(List<ConsoleLine> tags) {
        synchronized (ConsoleRecyclerAdapter.class) {
            Iterator<ConsoleLine> iter = lines.iterator();

            while (iter.hasNext()) {
                ConsoleLine consoleLine = iter.next();
                if (!tags.contains(consoleLine)) {
                    removeConsoleLine(iter, lines.indexOf(consoleLine));
                }
            }
        }
    }

    private void applyAndAnimateAdditions(List<ConsoleLine> tags) {
        for (ConsoleLine consoleLine :
                tags) {
            if (!lines.contains(consoleLine)) addConsoleLine(consoleLine);
        }
    }

    private void applyAndAnimateMovedItems(List<ConsoleLine> tags) {
        for (ConsoleLine consoleLine :
                tags) {
            int fromPosition = lines.indexOf(consoleLine);
            int toPosition = tags.indexOf(consoleLine);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveTagCategories(fromPosition, toPosition);
            }
        }
    }

    public static class ViewHolderMessage extends ConsoleViewHolder {
        TextView txt;

        public ViewHolderMessage(View itemView) {
            super(itemView);
            txt = (TextView) itemView.findViewById(android.R.id.text1);
        }

        @Override
        public void setLine(ConsoleLine line) {
            txt.setText((String) line.data);
        }
    }

    public static class ViewHolderImage extends ConsoleViewHolder {
        ImageView img;

        public ViewHolderImage(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.imgCard);
        }

        @Override
        public void setLine(ConsoleLine line) {
            img.setImageBitmap((Bitmap) line.data);
        }
    }
}
