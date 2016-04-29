package jiaqi.android.testviewpager.utils;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class ContactUtils {
    private static final String TAG = ContactUtils.class.getSimpleName();

    private static ContactUtils instance;
    private ArrayList<Contact> contacts;
    private Context context;

    private ContactUtils(Context context) {
        this.context = context;
    }

    public static ContactUtils getContactUtils(Context context) {
        if (instance != null) {
            return instance;
        } else {
            instance = new ContactUtils(context);
            return instance;
        }
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public boolean isContactsNull() {
        return (contacts == null);
    }

    public void prepareContactsFromFile() {

        ArrayList<Contact> contacts = new ArrayList<>();

        try {
            BufferedReader reader = getAssetBufferedReader("Contacts.txt");
            String line;
            String[] info;
            String firstName, lastName, emailAddress;
            while ((line = reader.readLine()) != null) {
                info = line.split(" ");
                firstName = info[0];
                lastName = info[1];
                emailAddress = info[2];
                contacts.add(new Contact(firstName, lastName, String.valueOf(new Random().nextInt(1000000) + 1000000), emailAddress, null));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            AssetManager assetManager = context.getAssets();
            String[] photoNames = assetManager.list("avatars");

            for (int i = 0; i < contacts.size(); i++) {
                InputStream is = assetManager.open("avatars/" + photoNames[new Random().nextInt(70) % photoNames.length]);
                Bitmap photoBitmap = BitmapFactory.decodeStream(is);
                contacts.get(i).setPhoto(photoBitmap);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.contacts = contacts;
    }


    @NonNull
    private BufferedReader getAssetBufferedReader(String assetName) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream is = assetManager.open(assetName);
        InputStreamReader isr = new InputStreamReader(is);
        return new BufferedReader(isr);
    }

    public void loadSampleContactPhoto(ImageView v) {
        try {
            AssetManager assetManager = context.getAssets();
            String[] photoNames = assetManager.list("avatars");
            InputStream is = assetManager.open("avatars/" + photoNames[0]);
            Bitmap photoBitmap = BitmapFactory.decodeStream(is);
            v.setImageBitmap(photoBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addContacts(int num) {
        int counter = 0;

        for (Contact contact : contacts) {
            insertContact(contact);
            if ((counter++) > num) {
                break;
            }
        }
    }

    private void insertContact(Contact contact) {
        String displayName = contact.getDisplayName();
        String phoneNumber = contact.getPhoneNumber();
        String emailAddress = contact.getEmailAddress();
        Bitmap photo = contact.getPhoto();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = ops.size();

        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        ops.add(ContentProviderOperation
                .newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName)
                .build());

        ops.add(ContentProviderOperation
                .newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                .build());

        ops.add(ContentProviderOperation
                .newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, emailAddress)
                .build());

        ops.add(ContentProviderOperation
                .newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                .withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, byteArray)
                .build());

        try {
            ContentProviderResult[] results = context.getContentResolver()
                    .applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }
    }

    public void deleteAllContacts() {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
            contentResolver.delete(uri, null, null);
        }
        cursor.close();
    }
}
