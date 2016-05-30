package com.example.armadillianonimi.emergencyphonenumbers;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class EmergencyTab extends Fragment {

    public static String ALREADY_ADDED = "contacts";

    public String fireNumber;
    public String policeNumber;
    public String medicalNumber;

    private TextView fireTextView;
    private TextView policeTextView;
    private TextView medicalTextView;
    private FloatingActionButton addContactButton;
    private HashMap<String, Contact> addedContacts = new HashMap<>();
    private final int CONTACT_PICK_REQUEST = 1000;
    private final int RESULT_CODE_OK = -1;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private SharedPreferences prefs;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.emergency_tab, container, false);
        System.out.println("Initializing fireTextView");
        fireTextView = (TextView) view.findViewById(R.id.firenum);
        if (fireNumber != null) {
            fireTextView.setText(fireNumber);
        }
        policeTextView = (TextView) view.findViewById(R.id.policenum);
        if (policeNumber != null) {
            policeTextView.setText(policeNumber);
        }
        medicalTextView = (TextView) view.findViewById(R.id.medicalnum);
        if (medicalNumber != null) {
            medicalTextView.setText(medicalNumber);
        }

        addContactButton = (FloatingActionButton)view.findViewById(R.id.btn);
        setupAddContactButton();

        LinearLayout l = (LinearLayout) view.findViewById(R.id.main);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (prefs != null) {
            Gson gson = new Gson();
            String json = prefs.getString("MyObject", "");
            if (!(json.equals(""))) {
                java.lang.reflect.Type type = new TypeToken<HashMap<String,Contact>>(){}.getType();
                //Type type = new TypeToken<Map<String, Contact>>(){}.getType();
                HashMap<String, Contact> obj = gson.fromJson(json, type);

                addedContacts = obj;
            }
        }

        if (!(addedContacts.isEmpty())) {
            for (Contact c : addedContacts.values()) {

                addContactCard(c, l);
            }
        }



        System.out.println("EmergencyFragment: onCreateView");
        return view;
    }

    private int inDP(int num) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, num, getResources().getDisplayMetrics());
    }

    public Drawable getSelectedItemDrawable() {
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray ta = getActivity().obtainStyledAttributes(attrs);
        Drawable selectedItemDrawable = ta.getDrawable(0);
        ta.recycle();
        return selectedItemDrawable;
    }

    private void addContactCard(Contact contact, LinearLayout layout) {
        CardView contactCard = new CardView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        contactCard.setRadius(inDP(2));
        layoutParams.gravity = Gravity.CENTER;
        contactCard.setLayoutParams(layoutParams);
        layout.addView(contactCard);
        contactCard.isClickable();
        contactCard.isFocusable();
        contactCard.setForeground(getSelectedItemDrawable());
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) contactCard.getLayoutParams();
        marginLayoutParams.setMargins(inDP(10), inDP(5), inDP(10), inDP(5));
        contactCard.setContentPadding(inDP(10), inDP(10), inDP(10), inDP(10));
        contactCard.setLayoutParams(marginLayoutParams);
        contactCard.setId(Integer.parseInt(contact.id));
        contactCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the clicked contact
                callContact(v);
            }
        });

        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        contactCard.addView(relativeLayout);

        ImageView img = new ImageView(getContext());
        relativeLayout.addView(img);
        img.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.phone_icon));
        RelativeLayout.LayoutParams d = (RelativeLayout.LayoutParams) img.getLayoutParams();
        d.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        d.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        img.getLayoutParams().height = inDP(40);
        img.getLayoutParams().width = inDP(40);
        img.setLayoutParams(d);
        ViewGroup.MarginLayoutParams ma = (ViewGroup.MarginLayoutParams) img.getLayoutParams();
        ma.setMargins(inDP(10), 0, inDP(10), 0);
        img.setContentDescription("Phone icon");
        img.setColorFilter(Color.parseColor("#616161"));
        img.setLayoutParams(ma);

        TextView contactName = new TextView(getContext());
        relativeLayout.addView(contactName);
        contactName.setText(contact.phone);
        contactName.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        RelativeLayout.LayoutParams da = (RelativeLayout.LayoutParams) contactName.getLayoutParams();
        da.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        da.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        contactName.setLayoutParams(da);
        contactName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        contactName.setTextColor(Color.parseColor("#616161"));
        contactName.setPadding(inDP(10), inDP(10), inDP(10), inDP(10));
        ViewGroup.MarginLayoutParams mar = (ViewGroup.MarginLayoutParams) contactName.getLayoutParams();
        mar.setMargins(0, 0, inDP(50), 0);
        contactName.setLayoutParams(mar);

        TextView contactPhone = new TextView(getContext());
        relativeLayout.addView(contactPhone);
        String name = contact.name;
        if (name.length() > 6) {
            name = name.substring(0, 6)+ "...";
        }
        contactPhone.setText(name);
        RelativeLayout.LayoutParams rel = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contactPhone.setLayoutParams(rel);
        RelativeLayout.LayoutParams das = (RelativeLayout.LayoutParams) contactPhone.getLayoutParams();
        das.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        contactPhone.setLayoutParams(das);
        contactPhone.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        contactPhone.setTextColor(Color.parseColor("#616161"));
        contactPhone.setPadding(inDP(10), inDP(10), inDP(10), inDP(10));
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONTACT_PICK_REQUEST && resultCode == RESULT_CODE_OK) {
            Bundle extras = data.getExtras();
            final HashMap<String, Contact> selectedContacts = (HashMap<String, Contact>) extras.get(ContactsPickerActivity.SELECTED);

            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.main);
            linearLayout.removeViews(3, addedContacts.size());
            // set field to new list of contacts
            addedContacts = selectedContacts;

            SharedPreferences.Editor prefsEditor = prefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(addedContacts);
            prefsEditor.putString("MyObject", json);
            prefsEditor.apply();

            for (Contact contact : addedContacts.values()) {
                addContactCard(contact, linearLayout);
            }
        }
        System.out.println("EmergencyFragment: onActivityResult");
    }

    public boolean checkPermission(String permission) {
        return (ContextCompat.checkSelfPermission(getContext(), permission)
                == PackageManager.PERMISSION_GRANTED);
    }

    public void request(String permission) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{permission},
                PERMISSION_REQUEST_CODE);
    }

    public void callContact(View view) {
        Contact contact = addedContacts.get(Integer.toString(view.getId()));
        if (checkPermission(Manifest.permission.CALL_PHONE)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            final Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + contact.phone));
            if (prefs.getBoolean("pref_call_confirmation", true)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Call " + contact.name + " (" + contact.phone +")?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(callIntent);
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            } else{
                startActivity(callIntent);
            }
        } else {
            request(Manifest.permission.CALL_PHONE);
            Toast.makeText(getContext(), "Please, allow call permission.\nIt's good for you!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupAddContactButton() {
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission(Manifest.permission.READ_CONTACTS)) {
                    Intent intentContactPick = new Intent(getContext(), ContactsPickerActivity.class);
                    intentContactPick.putExtra(ALREADY_ADDED, addedContacts);
                    startActivityForResult(intentContactPick, CONTACT_PICK_REQUEST);
                } else {
                    request(Manifest.permission.READ_CONTACTS);
                    Toast.makeText(getContext(), "Please, if you want to add a contact, allow contact permission.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateUI() {
        if (fireTextView != null) {
            fireTextView.setText(fireNumber);
        }

        if (policeTextView != null) {
            policeTextView.setText(policeNumber);
        }

        if (medicalTextView != null) {
            medicalTextView.setText(medicalNumber);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("EmergencyFragment: onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("EmergencyFragment: onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("EmergencyFragment: onDestroy");
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.out.println("EmergencyFragment: onLowMemory");
    }
}
