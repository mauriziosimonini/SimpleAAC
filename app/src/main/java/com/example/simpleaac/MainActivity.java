package com.example.simpleaac;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements OnInitListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;

    private TextToSpeech tts;
    private boolean isTtsReady = false;
    private DatabaseHelper dbHelper;
    private GridAdapter adapter;
    private GridView gridView;
    private ImageButton fabAdd;
    private String currentPhotoPath;
    private ImageView dialogImageView;

    @Override

    EspeakTTS tts = new EspeakTTS();
    tts.init(context);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate started");

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views and setup listeners
        setupViews();

        // Initialize Text-to-Speech
        setupTTS();

        // Load items from database
        loadItems();
    }

    private void setupViews() {
        Log.d(TAG, "Setting up views");

        // Initialize GridView
        gridView = (GridView) findViewById(R.id.gridView);

        // Initialize FAB
        fabAdd = (ImageButton) findViewById(R.id.fab_add);

        // Set up GridView click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item) parent.getItemAtPosition(position);
                Log.d(TAG, "Grid item clicked: " + item.getText());
                speak(item.getText());
            }
        });

        // Set up long click listener for deletion
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Item item = (Item) parent.getItemAtPosition(position);
                showDeleteDialog(item);
                return true;
            }
        });

        // Set up add button
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog();
            }
        });
    }

    private void setupTTS() {
        Log.d(TAG, "Setting up TTS");
        try {
            // Initialize TTS engine with the system default engine
            tts = new TextToSpeech(getApplicationContext(), this);
        } catch (Exception e) {
            Log.e(TAG, "TTS initialization failed", e);
            Toast.makeText(this, "Inizializzazione Text-to-Speech fallita",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInit(int status) {
        Log.d(TAG, "TTS onInit status: " + status);

        if (status == TextToSpeech.SUCCESS) {
            try {
                // Try Italian first
                int result = tts.setLanguage(Locale.ITALIAN);

                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.d(TAG, "Italian not available, trying English");
                    // Try English as fallback
                    result = tts.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Try US English as last resort
                        Log.d(TAG, "English not available, trying US English");
                        result = tts.setLanguage(Locale.US);
                    }
                }

                // If we got here, some language was set
                tts.setSpeechRate(0.8f);
                isTtsReady = true;
                Log.d(TAG, "TTS initialization successful");

                // Speak a short test message
                speak("Ok");

            } catch (Exception e) {
                Log.e(TAG, "Error setting up TTS language", e);
                // Try to continue anyway
                isTtsReady = true;
            }
        } else {
            Log.e(TAG, "TTS initialization failed with status: " + status);
            Toast.makeText(this,
                    "Inizializzazione Text-to-Speech fallita",
                    Toast.LENGTH_SHORT).show();
        }
    }



    private void loadItems() {
        Log.d(TAG, "Loading items from database");
        List<Item> items = dbHelper.getAllItems();
        Log.d(TAG, "Found " + items.size() + " items");

        adapter = new GridAdapter(this, items);
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void showAddItemDialog() {
        Log.d(TAG, "Showing add item dialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);

        final EditText editText = (EditText) dialogView.findViewById(R.id.edit_text);
        dialogImageView = (ImageView) dialogView.findViewById(R.id.image_preview);

        dialogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceDialog();
            }
        });

        builder.setView(dialogView)
                .setTitle("Add New Item")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        if (!text.isEmpty()) {
                            dbHelper.addItem(text, currentPhotoPath);
                            loadItems();
                            Log.d(TAG, "Added new item: " + text);
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void showDeleteDialog(final Item item) {
        Log.d(TAG, "Showing delete dialog for item: " + item.getText());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteItem(item.getId());
                        loadItems();
                        Log.d(TAG, "Deleted item: " + item.getText());
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showImageSourceDialog() {
        Log.d(TAG, "Showing image source dialog");

        final CharSequence[] items = {"Take Photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Image")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            dispatchTakePictureIntent();
                        } else {
                            dispatchPickImageIntent();
                        }
                    }
                });
        builder.show();
    }

    private void dispatchTakePictureIntent() {
        Log.d(TAG, "Dispatching take picture intent");

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void dispatchPickImageIntent() {
        Log.d(TAG, "Dispatching pick image intent");

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    private File createImageFile() {
        Log.d(TAG, "Creating image file");

        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            Log.d(TAG, "Created image file: " + currentPhotoPath);
            return image;
        } catch (Exception e) {
            Log.e(TAG, "Error creating image file", e);
        }
        return null;
    }

    private void speak(String text) {
        if (tts == null) {
            Log.e(TAG, "TTS is null");
            return;
        }

        if (!isTtsReady) {
            Log.e(TAG, "TTS not ready");
            Toast.makeText(this, "Text-to-Speech non pronto",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Log.d(TAG, "Speaking: " + text);
            // Simple speak call for older Android versions
            tts.stop(); // Stop any current speech
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        } catch (Exception e) {
            Log.e(TAG, "Error speaking text: " + text, e);
            Toast.makeText(this, "Errore sintesi vocale",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Log.d(TAG, "Received camera result");
                dialogImageView.setImageURI(Uri.fromFile(new File(currentPhotoPath)));
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null) {
                Log.d(TAG, "Received gallery result");
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn,
                        null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    currentPhotoPath = cursor.getString(columnIndex);
                    cursor.close();
                    dialogImageView.setImageURI(selectedImage);
                    Log.d(TAG, "Selected image path: " + currentPhotoPath);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-initialize TTS if needed
        if (tts == null || !isTtsReady) {
            setupTTS();
        }
    }

    @Override
    protected void onPause() {
        // Stop speaking if app is paused
        if (tts != null && tts.isSpeaking()) {
            tts.stop();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (tts != null) {
            try {
                tts.stop();
                tts.shutdown();
            } catch (Exception e) {
                Log.e(TAG, "Error shutting down TTS", e);
            }
        }
        super.onDestroy();
    }
}