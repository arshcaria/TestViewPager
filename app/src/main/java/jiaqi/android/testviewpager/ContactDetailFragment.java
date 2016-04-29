package jiaqi.android.testviewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import jiaqi.android.testviewpager.utils.Contact;

/**
 * Created by Jiaqi on 4/29/2016.
 */
public class ContactDetailFragment extends Fragment {

    private ImageView ivContactPhoto;
    private TextView tvContactDisplayName;

    private ImageButton ibContactDialNumber;
    private TextView tvContactNumber;
    private ImageButton ibContactSendMessage;

    private ImageButton ibContactSendEmail;
    private TextView tvContactEmail;

    private Contact contact;

    public ContactDetailFragment(Contact contact) {
        this.contact = contact;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_detail, container, false);

        ivContactPhoto = (ImageView) view.findViewById(R.id.iv_contact_photo);
        ivContactPhoto.setImageBitmap(contact.getPhoto());
        tvContactDisplayName = (TextView) view.findViewById(R.id.tv_contact_display_name);
        tvContactDisplayName.setText(contact.getDisplayName());

        ibContactDialNumber = (ImageButton) view.findViewById(R.id.ib_contact_dial_number);
        tvContactNumber = (TextView) view.findViewById(R.id.tv_contact_number);
        tvContactNumber.setText(contact.getPhoneNumber());
        ibContactSendMessage = (ImageButton) view.findViewById(R.id.ib_contact_send_message);

        ibContactSendEmail = (ImageButton) view.findViewById(R.id.ib_contact_send_email);
        tvContactEmail = (TextView) view.findViewById(R.id.tv_contact_email);
        tvContactEmail.setText(contact.getEmailAddress());

        return view;
    }
}
