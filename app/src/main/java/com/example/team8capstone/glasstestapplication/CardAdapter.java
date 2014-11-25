package com.example.team8capstone.glasstestapplication;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter class that handles list of cards.
 */
public class CardAdapter extends CardScrollAdapter {

    final List<CardBuilder> mCards;

    Context c;

    public CardAdapter(List<CardBuilder> cards, Context context) {
        mCards = cards;
        c = context;
    }

    @Override
    public int getCount() {
        return mCards.size();
    }

    @Override
    public Object getItem(int position) {
        return mCards.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mCards.get(position).getView(convertView, parent);

        switch (position)
        {
            case 0:
                ViewGroup rightColumn0 = (ViewGroup) view.findViewById(R.id.right_column);
                ImageView imageView0 = (ImageView) rightColumn0.findViewById(R.id.image);
                imageView0.setImageResource(R.drawable.beach);

                ViewGroup leftColumn0 = (ViewGroup) view.findViewById(R.id.left_column);
                TextView textViewHeader0 = (TextView) leftColumn0.findViewById(R.id.header);
                textViewHeader0.setText("Test");
                textViewHeader0.setTextSize(30);

                return view;
            case 3:
                ViewGroup rightColumn3 = (ViewGroup) view.findViewById(R.id.right_column);
                ImageView imageView3 = (ImageView) rightColumn3.findViewById(R.id.image);
                imageView3.setImageResource(R.drawable.supplies);

                ViewGroup leftColumn3 = (ViewGroup) view.findViewById(R.id.left_column);
                TextView textViewHeader3 = (TextView) leftColumn3.findViewById(R.id.header);
                textViewHeader3.setText("Step 1: Gather Supplies");
                textViewHeader3.setTextSize(16);

                TextView textViewContent3 = (TextView) leftColumn3.findViewById(R.id.content);
                textViewContent3.setText("• Stepstool\n" +
                        "• Acrylic yarn\n" +
                        "• Pulling comb\n" +
                        "• Rug Hook\n" +
                        "• Small bucket of clean water\n" +
                        "• Quic Braid (optional)");
                textViewContent3.setTextSize(14);

                return view;
            default:
                return view;

        }

    }

    @Override
    public int getViewTypeCount() {
        return CardBuilder.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position){
        return mCards.get(position).getItemViewType();
    }

    @Override
    public int getPosition(Object item) {
        for (int i = 0; i < mCards.size(); i++) {
            if (getItem(i).equals(item)) {
                return i;
            }
        }
        return AdapterView.INVALID_POSITION;
    }
}
