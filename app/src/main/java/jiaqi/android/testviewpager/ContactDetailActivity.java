package jiaqi.android.testviewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

import jiaqi.android.testviewpager.utils.Contact;
import jiaqi.android.testviewpager.utils.ContactUtils;


public class ContactDetailActivity extends AppCompatActivity {

    private ViewPager contactDetailPager;
    private List<Contact> contacts;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        contacts = ContactUtils.getContactUtils(this).getContacts();

        contactDetailPager = (ViewPager) findViewById(R.id.contact_detail_pager);
        contactDetailPager.setAdapter(new ContactDetailPagerAdapter(getSupportFragmentManager()));
    }

    class ContactDetailPagerAdapter extends FragmentStatePagerAdapter {

        public ContactDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new ContactDetailFragment(contacts.get(position));
            return fragment;
        }

        @Override
        public int getCount() {
            return contacts.size();
        }
    }
}
