package com.hazelwood.partypal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Hazelwood on 11/3/14.
 */
public class Form_Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Parse.initialize(this, "xZ8EWHn0jDK7lACkdvPorPp3u6RjP62M5AjgB5iz", "MOYDodRkqbuFCP3t1RVhDCpXuTCn4YNhUkd6G6KU");

        setContentView(R.layout.activity_form);
        getActionBar().setTitle("Create a Party");

        FragmentManager manager = getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.form_container, FormFragment.newInstance("This"), FormFragment.TAG)
                .commit();
    }

    public static class FormFragment extends Fragment {
        public static final String TAG = "FORM_TAG";
        private static final String ARG_SECTION_NUMBER = "section_number";
        static final int REQUEST_CAMERA = 0x01001;
        static final int REQUEST_GALLERY = 0x01002;
        Uri file;
        byte[] bytes;
        String imageName, newPath;
        ImageView imageView;
        int year_, month_, day_,hour_, minute_;

        public static FormFragment newInstance(String text) {
            FormFragment fragment = new FormFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_NUMBER, text);
            fragment.setArguments(args);
            return fragment;
        }

        public FormFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_form, container, false);
            registerForContextMenu(rootView);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            final CharSequence[] items = {"Gallery", "Camera", "cancel"};

            final Spinner state_spinner = (Spinner) getActivity().findViewById(R.id.state_spinner);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.state_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            state_spinner.setAdapter(adapter);

            final EditText name = (EditText) getActivity().findViewById(R.id.form_party_name);
            final EditText host = (EditText) getActivity().findViewById(R.id.form_party_host);
            final EditText address = (EditText) getActivity().findViewById(R.id.form_party_address);
            final EditText price = (EditText) getActivity().findViewById(R.id.form_party_price);
            final EditText description = (EditText) getActivity().findViewById(R.id.form_party_descr);
            final EditText city = (EditText) getActivity().findViewById(R.id.form_party_city);

            Button submitForm = (Button) getActivity().findViewById(R.id.form_submit);
            Button cancelForm = (Button) getActivity().findViewById(R.id.form_cancel);

            final EditText start = (EditText) getActivity().findViewById(R.id.time_start);
            final EditText end = (EditText) getActivity().findViewById(R.id.time_end);
            final EditText date = (EditText) getActivity().findViewById(R.id.form_party_date);

            end.setFocusable(false);
            end.setClickable(true);
            start.setFocusable(false);
            start.setClickable(true);
            date.setClickable(true);
            date.setFocusable(false);

            final ArrayList<String> hosts = new ArrayList<String>();
            host.setFocusable(false);
            host.setClickable(true);

            host.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setMessage("Type in a host");

                    final EditText input = new EditText(getActivity());
                    input.setHint("name or @name");
                    input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    alert.setView(input);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();
                            hosts.add(value);
                            Log.d(TAG, hosts.size() + " size");
                            String sent = "";

                            for (String string : hosts){
                                sent += string + ", ";
                                host.setText(sent);
                                Log.d(TAG, hosts.size() + " size");
                            }
                            // Do something with value!
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });

                    alert.show();

                }
            });

            imageView = (ImageView) getActivity().findViewById(R.id.form_party_image);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item){

                                //Gallery
                                case 0:
                                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(i, REQUEST_GALLERY);
                                    break;

                                //Camera
                                case 1:
                                    final Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    file = getOutputUri();
                                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, file);
                                    startActivityForResult(takePicture, REQUEST_CAMERA);
                                    break;
                                case 2:
                                default:

                            }
                            Toast.makeText(getActivity().getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();



                }
            });

            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTime(start);
                }
            });

            end.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTime(end);
                }
            });

            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDate(date);
                }
            });

            cancelForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            submitForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setProgressStyle((ProgressDialog.STYLE_HORIZONTAL));
                    progressDialog.setIndeterminate(true);
                    progressDialog.setProgressNumberFormat("Submitting party.");
                    progressDialog.setProgressPercentFormat(null);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    final ParseObject partyInfo = new ParseObject("PartyInfo");
                    partyInfo.put("name", name.getText().toString());
                    partyInfo.put("address", address.getText().toString()
                            + ", " + city.getText().toString()
                            +", " + state_spinner.getSelectedItem().toString());
                    partyInfo.put("host", host.getText().toString());
                    partyInfo.put("price", price.getText().toString());
                    partyInfo.put("description", description.getText().toString());
                    partyInfo.put("startTime", start.getText().toString());
                    partyInfo.put("endTime", end.getText().toString());
                    partyInfo.put("date", date.getText().toString());
                    partyInfo.put("voteYes", 0);
                    partyInfo.put("voteNo", 0);

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year_, month_, day_,
                            hour_, minute_, 0);
                    long startTime = calendar.getTimeInMillis();

                    partyInfo.put("endDate", startTime);


                    ParseFile photoFile = new ParseFile("image.png", bytes);
                    photoFile.saveInBackground();

                    partyInfo.put("imageName", "Party Logo");
                    partyInfo.put("imageFile", photoFile);

                    partyInfo.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            progressDialog.dismiss();
                            getActivity().finish();

                        }
                    });
                }
            });



        }

        public void getDate(final EditText editText){
            Calendar calendar = Calendar.getInstance();
            int thisYear = calendar.get(Calendar.YEAR);
            final int thisDay = calendar.get(Calendar.DAY_OF_MONTH);
            final int thisMonth = calendar.get(Calendar.MONTH);
            DatePickerDialog datePickerDialog;
            datePickerDialog  = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    view.setMinDate(System.currentTimeMillis() - 5000);

                    SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");

                    editText.setText(format.format(new Date(year - 1900, monthOfYear, dayOfMonth)));

                    day_ = dayOfMonth;
                    month_ = monthOfYear;
                    year_ = year;

                }
            },thisYear, thisMonth, thisDay);

            datePickerDialog.setTitle("Select Date");
            datePickerDialog.show();

        }

        public void getTime (final EditText v){
            Calendar mCurrentTime = Calendar.getInstance();
            final int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mCurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    String timeSet = "";
                    if (selectedHour > 12) {
                        selectedHour -= 12;
                        timeSet = "PM";
                    } else if (selectedHour == 0) {
                        selectedHour += 12;
                        timeSet = "AM";
                    } else if (selectedHour == 12)
                        timeSet = "PM";
                    else
                        timeSet = "AM";

                    String min = "";
                    if (selectedMinute < 10)
                        min = "0" + selectedMinute ;
                    else
                        min = String.valueOf(selectedMinute);

                    String aTime = new StringBuilder().append(selectedHour).append(':')
                            .append(min ).append(" ").append(timeSet).toString();

                    v.setText(aTime);

                    if (v.getId() == R.id.time_end){
                        hour_ = selectedHour;
                        minute_ = selectedMinute;
                    }
                }
            }, hour, minute, false);

            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CAMERA && resultCode != Activity.RESULT_CANCELED){
                Uri selectedImage = file;
                bytes = null;
                try {
                    ContentResolver cr = getActivity().getBaseContext().getContentResolver();
                    InputStream inputStream = cr.openInputStream(selectedImage);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Bitmap out = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    out.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    bytes = baos.toByteArray();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }

            else if (requestCode == REQUEST_GALLERY && resultCode != Activity.RESULT_CANCELED){
                Uri selectedImage = data.getData();
                bytes = null;
                try {
                    ContentResolver cr = getActivity().getBaseContext().getContentResolver();
                    InputStream inputStream = cr.openInputStream(selectedImage);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    bytes = baos.toByteArray();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                imageName = picturePath;
            }
        }

        private Uri getOutputUri(){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyy_HHmmss");
            Date today = new Date(System.currentTimeMillis());
            String imageName = simpleDateFormat.format(today);

            File imgDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File appDir = new File(imgDir, "MapApp");
            appDir.mkdirs();

            File image = new File(appDir, imageName + ".jpg");
            newPath = image.getAbsolutePath();

            try{
                image.createNewFile();
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
            return Uri.fromFile(image);
        }

        public void addImageToGallery(Uri image){
            Intent scan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scan.setData(image);
            getActivity().sendBroadcast(scan);
        }
    }
}