package hu.beernotfoundexception.fontastic.view;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import hu.beernotfoundexception.fontastic.R;
import hu.beernotfoundexception.fontastic.data.ConsoleLine;
import hu.beernotfoundexception.fontastic.view.recycler.vh.ConsoleViewHolder;

public class ConsoleRecyclerAdapter extends RecyclerView.Adapter<ConsoleViewHolder> {

    public static final int MAX_CONSOLE_HISTORY = 10;

    private final List<ConsoleLine> lines = new LinkedList<>();

    public ConsoleRecyclerAdapter() {
    }

    public void log(String s) {
        addConsoleLine(0, new ConsoleLine(ConsoleLine.TYPE_STRING, s));
    }

    public void log(Bitmap bitmap) {
        addConsoleLine(0, new ConsoleLine(ConsoleLine.TYPE_IMAGE, bitmap));
    }

    public void trimConsole() {
        while (MAX_CONSOLE_HISTORY < lines.size())
            remove(lines.size()-1);
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
        lines.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        lines.remove(position);
        notifyItemRemoved(position);
    }

    public void addConsoleLine(int position, ConsoleLine consoleLine) {
        lines.add(position, consoleLine);
        notifyItemInserted(position);
        trimConsole();
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
