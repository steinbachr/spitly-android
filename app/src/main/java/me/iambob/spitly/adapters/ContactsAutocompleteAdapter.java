package me.iambob.spitly.adapters;

import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;

import org.w3c.dom.Text;

import java.util.ArrayList;

import me.iambob.spitly.models.Contact;
import me.iambob.spitly.R;


public class ContactsAutocompleteAdapter extends ArrayAdapter<Contact> {
    public ContactsAutocompleteAdapter(Context context, int resourceId, ArrayList<Contact> contacts) {
        super(context, resourceId, contacts);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.contact_autocomplete_item, null);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Contact contact = this.getItem(position);

        holder.contactNameTv = (TextView)convertView.findViewById(R.id.contactName);
        holder.contactNameTv.setText(contact.getName());
        holder.contactNumberTv = (TextView)convertView.findViewById(R.id.contactNumber);
        holder.contactNumberTv.setText(contact.getNumber());

        holder.starImg = (ImageView)convertView.findViewById(R.id.starContactImg);
        holder.starImg.setOnClickListener(new StarClickListener(contact));
        if (contact.isStarred()) {
            holder.starImg.setImageResource(R.drawable.star_selected);
        } else {
            holder.starImg.setImageResource(R.drawable.star_unselected);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView contactNameTv, contactNumberTv;
        ImageView starImg;
    }


    /**-- Listeners --**/
    class StarClickListener implements View.OnClickListener {
        Contact clicked;

        public StarClickListener(Contact clicked) {
            this.clicked = clicked;
        }

        @Override
        public void onClick(View v) {
            boolean isStarred = this.clicked.toggleStar(getContext());
            ImageView starImg = (ImageView)v;

            if (isStarred) {
                starImg.setImageResource(R.drawable.star_selected);
            } else {
                starImg.setImageResource(R.drawable.star_unselected);
            }
        }
    }

}
