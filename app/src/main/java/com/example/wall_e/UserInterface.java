package com.example.wall_e;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class UserInterface extends AppCompatActivity {
    TextView name,balance,debit;
    int bal=0,deb=0,cred,debt;
    Button b_add,b_sub,b_mrep,b_yrep;
    EditText et_add,et_sub;
    int pageHeight = 1120;
    int pagewidth = 792;
    private OutputStream os;
    private static final int PERMISSION_REQUEST_CODE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);
        Intent i=getIntent();
        name=findViewById(R.id.tv_welcome);
        balance=findViewById(R.id.tv_bal);
        debit=findViewById(R.id.tv_debit);
        b_add=findViewById(R.id.b_add);
        b_sub=findViewById(R.id.b_add_debit);
        et_add=findViewById(R.id.et_add);
        et_sub=findViewById(R.id.et_add_debit);
        b_mrep=findViewById(R.id.b_mrep);
        b_yrep=findViewById(R.id.b_yrep);
        String s=i.getExtras().getString("name");
        name.setText("Welcome "+s+"!");
        balance.setText("Balance: "+bal);
        debit.setText("Debit: "+deb);
        b_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s1=et_add.getText().toString();
                cred=Integer.parseInt(s1);
                bal+=cred;
                balance.setText("Balance: "+bal);
            }
        });
        b_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s2=et_sub.getText().toString();
                debt=Integer.parseInt(s2);
                bal-=debt;
                deb+=debt;
                balance.setText("Balance: "+bal);
                debit.setText("Debit: "+deb);
            }
        });
        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }
        b_mrep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePDF();
            }
        });
    }
    private void generatePDF(){
        PrintAttributes printAttrs = new PrintAttributes.Builder().
                setColorMode(PrintAttributes.COLOR_MODE_COLOR).
                setMediaSize(PrintAttributes.MediaSize.NA_LETTER).
                setResolution(new PrintAttributes.Resolution("zooey", PRINT_SERVICE, 300, 300)).
                setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                build();
        PdfDocument pdfDocument = new PrintedPdfDocument(this, printAttrs);
        Paint title = new Paint();
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(15);
        title.setColor(ContextCompat.getColor(this, R.color.purple_200));
        Canvas canvas = myPage.getCanvas();
        canvas.drawText("Balance: "+bal, 10, 10, title);
        canvas.drawText("Expenditure: "+deb, 10, 20, title);
        pdfDocument.finishPage(myPage);
//        File file = new File(Environment.getExternalStorageDirectory(), "Monthly_Report.pdf");
//        try {
//            pdfDocument.writeTo(new FileOutputStream(file));
//            Toast.makeText(UserInterface.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            File pdfDirPath = new File(getFilesDir(), "pdfs");
            pdfDirPath.mkdirs();
            File file = new File(pdfDirPath, "pdfsend.pdf");
            Uri contentUri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                    BuildConfig.APPLICATION_ID + ".provider", file);
            os = new FileOutputStream(file);
            pdfDocument.writeTo(os);
            pdfDocument.close();
            os.close();
            Intent email = new Intent(Intent.ACTION_SEND);
            email.setData(Uri.parse("mailto:"));
            email.setType("application/pdf");
            email.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            email.putExtra(Intent.EXTRA_SUBJECT, "Monthly Report");
            email.putExtra(Intent.EXTRA_STREAM,contentUri);
            startActivity(
                    Intent
                            .createChooser(email,
                                    "Choose an Email client :"));
        } catch (IOException e) {
            throw new RuntimeException("Error generating file", e);
        }
        pdfDocument.close();

    }
    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}