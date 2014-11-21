/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.team8capstone.glasstestapplication;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;

import java.util.List;

/**
 * Adapter class that handles list of cards.
 */
public class CardAdapter extends CardScrollAdapter {

    final List<CardBuilder> mCards;

    public CardAdapter(List<CardBuilder> cards) {
        mCards = cards;
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
            case 3:
                ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.right_column);
                ImageView imageView = (ImageView) viewGroup.getChildAt(0);
                imageView.setImageResource(R.drawable.supplies);

                ViewGroup viewGroup2 = (ViewGroup) view.findViewById(R.id.left_column);
                TextView textView = (TextView) viewGroup2.getChildAt(0);
                textView.setText("Step 1: Gather Supplies\n" +
                        "• Stepstool\n" +
                        "• Acrylic yarn\n" +
                        "• Pulling comb\n" +
                        "• Rug Hook\n" +
                        "• Small bucket of clean water\n" +
                        "• Quic Braid (optional)");
                textView.setTextSize(15);

                ListView listView = (ListView) viewGroup2.getChildAt(1);

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
