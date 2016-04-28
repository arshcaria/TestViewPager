package jiaqi.android.testviewpager.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

public class Contact {
    private String firstName;
    private String lastName;
    private String displayName;
    private String phoneNumber;
    private String emailAddress;
    private Bitmap photo;

    public Contact(@NonNull String firstName, String lastName, String phoneNumber, String emailAddress, Bitmap photo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.photo = photo;
        updateDisplayName();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateDisplayName();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateDisplayName();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    private void updateDisplayName() {
        if (lastName == null) {
            displayName = firstName;
        } else {
            displayName = firstName + " " + lastName;
        }
    }
}
