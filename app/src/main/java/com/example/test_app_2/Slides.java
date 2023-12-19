package com.example.test_app_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class Slides extends AppCompatActivity {
    ImageButton okButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slides);

        ImageSlider imageSlider = findViewById(R.id.imageSlider);
        okButton = findViewById(R.id.ok);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.photo1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.photo2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.photo3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.photo4, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.photo5, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.photo6, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.photo7, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.photo8, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(Slides.this, AddSheetID.class);
                startActivity(intent);
            }
        });

    }
}