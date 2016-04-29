package jiaqi.android.testviewpager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jiaqi.android.testviewpager.utils.ContactUtils;

/**
 * Created by Jiaqi on 4/28/2016.
 */
public class ContactUtilsActivity extends AppCompatActivity {

    private static final String TAG = ContactUtilsActivity.class.getSimpleName();
    private static final int REQ_CODE_READ_CONTACTS = 0;
    private static final int REQ_CODE_WRITE_CONTACTS = 1;
    private static final int REQ_CODE_READ_WRITE_CONTACTS = 2;


    private Button btnReloadMaleNames;
    private EditText etNumContacts;
    private Button btnAddContacts;
    private Button btnDelContacts;
    private ImageView ivContactPhoto;
    private Button btnLoadSampleContactPhoto;
    private Button btnContactDetail;

    private List<Button> buttons;

    private Listener listener = new Listener();

    private ContactUtils contactUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_utils);

        contactUtils = ContactUtils.getContactUtils(this);

        btnReloadMaleNames = (Button) findViewById(R.id.btn_reload_male_names);
        btnReloadMaleNames.setOnClickListener(listener);

        etNumContacts = (EditText) findViewById(R.id.et_number_of_contacts);
        etNumContacts.setFilters(new InputFilter[]{new NumberInputFilterMinMax(0, 1000)});

        btnAddContacts = (Button) findViewById(R.id.btn_add_contacts);
        btnAddContacts.setEnabled(false);
        btnAddContacts.setOnClickListener(listener);

        btnDelContacts = (Button) findViewById(R.id.btn_del_all_contacts);
        btnDelContacts.setOnClickListener(listener);

        ivContactPhoto = (ImageView) findViewById(R.id.iv_contact_sample_photo);

        btnLoadSampleContactPhoto = (Button) findViewById(R.id.btn_load_sample_contact_photo);
        btnLoadSampleContactPhoto.setOnClickListener(listener);

        btnContactDetail = (Button) findViewById(R.id.btn_contact_detail);
        btnContactDetail.setOnClickListener(listener);

        buttons = new ArrayList<>();
        buttons.add(btnAddContacts);
        buttons.add(btnDelContacts);
        buttons.add(btnLoadSampleContactPhoto);
        buttons.add(btnReloadMaleNames);

        //check permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_DENIED)
            {
                disableAllButtons();
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, REQ_CODE_READ_WRITE_CONTACTS);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_READ_WRITE_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    enableButtons();
                }
        }
    }

    private void disableAllButtons() {
        for (Button button : buttons) {
            button.setEnabled(false);
        }
    }

    private void enableButtons() {
        for (Button button : buttons) {
            if (button.getId() != R.id.btn_add_contacts) {
                button.setEnabled(true);
            }
        }
    }

    class Listener implements View.OnClickListener {

        CharSequence oldCharSeq;
        CharSequence newCharSeq;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_reload_male_names:
                    new PrepareContactsTask().execute();
                    break;
                case R.id.btn_add_contacts:
                    int num = Integer.valueOf(etNumContacts.getText().toString());
                    new InsertContacts().execute(num);
                    break;
                case R.id.btn_del_all_contacts:
                    new DeleteAllContactsTask().execute();
                    break;
                case R.id.btn_load_sample_contact_photo:
                    contactUtils.loadSampleContactPhoto(ivContactPhoto);
                    break;
                case R.id.btn_contact_detail:
                    Intent intent = getIntent();
                    intent.setClass(ContactUtilsActivity.this, ContactDetailActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }


    class NumberInputFilterMinMax implements InputFilter {

        int min, max;

        public NumberInputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public NumberInputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) {
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

    class PrepareContactsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            contactUtils.prepareContactsFromFile();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            btnAddContacts.setEnabled(true);
            Toast.makeText(ContactUtilsActivity.this,
                    "contacts preparation is done", Toast.LENGTH_SHORT).show();
        }
    }

    class InsertContacts extends AsyncTask<Integer, Void, Void> {
        long timeConsumed = 0;

        @Override
        protected void onPreExecute() {
            for (Button button : buttons) {
                if (button.getId() != R.id.btn_load_sample_contact_photo) {
                    button.setEnabled(false);
                }
            }
        }

        @Override
        protected Void doInBackground(Integer... params) {
            timeConsumed = SystemClock.currentThreadTimeMillis();
            contactUtils.addContacts(params[0]);
            timeConsumed = SystemClock.currentThreadTimeMillis() - timeConsumed;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for (Button button : buttons) {
                button.setEnabled(true);
            }
            Toast.makeText(ContactUtilsActivity.this,
                    "contacts insertion finished in " + timeConsumed + " ms.", Toast.LENGTH_SHORT).show();
        }
    }

    class DeleteAllContactsTask extends AsyncTask<Void, Void, Void> {
        long timeConsumed = 0;

        @Override
        protected void onPreExecute() {
            for (Button button : buttons) {
                if (button.getId() != R.id.btn_load_sample_contact_photo) {
                    button.setEnabled(false);
                }
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            timeConsumed = SystemClock.currentThreadTimeMillis();
            contactUtils.deleteAllContacts();
            timeConsumed = SystemClock.currentThreadTimeMillis() - timeConsumed;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            for (Button button : buttons) {
                if (button.getId() == R.id.btn_add_contacts && contactUtils.isContactsNull()) {
                    btnAddContacts.setEnabled(false);
                    continue;
                }
                button.setEnabled(true);
            }
            Toast.makeText(ContactUtilsActivity.this,
                    "contacts deletion finished in " + timeConsumed + " ms.", Toast.LENGTH_SHORT).show();
        }
    }
}
