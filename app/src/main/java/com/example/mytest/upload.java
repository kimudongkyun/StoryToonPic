package com.example.mytest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class upload extends Fragment {
    private static final int REQUEST_PICK_IMAGE = 102;
    private static final int MAX_IMAGES = 4;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private ImageView[] uploadedImageViews = new ImageView[MAX_IMAGES];
    private TextView[] uploadTextViews = new TextView[MAX_IMAGES];
    private TextView uploadStatusTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        // Initialize image views and text views
        for (int i = 0; i < MAX_IMAGES; i++) {
            uploadedImageViews[i] = view.findViewById(getResources().getIdentifier("uploaded_image" + (i + 1), "id", getActivity().getPackageName()));
            uploadTextViews[i] = view.findViewById(getResources().getIdentifier("upload_text" + (i + 1), "id", getActivity().getPackageName()));
        }

        uploadStatusTextView = view.findViewById(R.id.upload_status);

        view.findViewById(R.id.upload_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        return view;
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PICK_IMAGE && data != null) {
            if (data.getClipData() != null) {
                int count = Math.min(data.getClipData().getItemCount(), MAX_IMAGES);
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImageUris.add(imageUri);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                        int nextIndex = getNextAvailableImageViewIndex();
                        if (nextIndex != -1) {
                            uploadedImageViews[nextIndex].setImageBitmap(bitmap);
                            uploadedImageViews[nextIndex].setVisibility(View.VISIBLE);
                            uploadTextViews[nextIndex].setVisibility(View.GONE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                showUploadStatusPopup();
            }
        }
    }

    private int getNextAvailableImageViewIndex() {
        for (int i = 0; i < uploadedImageViews.length; i++) {
            if (uploadedImageViews[i].getDrawable() == null) {
                return i;
            }
        }
        return -1; // No available ImageView found
    }

    private void showUploadStatusPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.upload_status_dialog, null);
        ImageView imageView = dialogView.findViewById(R.id.upload_status_image);
        imageView.setImageResource(R.drawable.uploadsuccess);

        builder.setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        resetImages();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Set dialog background color and rounded corners
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#95FAAB")));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    private void resetImages() {
        for (int i = 0; i < MAX_IMAGES; i++) {
            uploadedImageViews[i].setImageDrawable(null);
            uploadedImageViews[i].setVisibility(View.GONE);
            uploadTextViews[i].setVisibility(View.VISIBLE);
        }
        selectedImageUris.clear();
    }
}
