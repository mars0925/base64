package shd.com.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import java.util.Base64;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 照片轉成base64
 */

public class MainActivity extends AppCompatActivity {
    private static final int OPEN_PHOTO_FOLDER_REQUEST_CODE = 1;
    private Button bt;
    private ImageView iv;
    private String TAG = "BASE64";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        bt = findViewById(R.id.button);
        iv = findViewById(R.id.imageView);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, OPEN_PHOTO_FOLDER_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (OPEN_PHOTO_FOLDER_REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            //encode the image
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                byte[] imagedata = readStream(inputStream);

                /*base64編碼*/
                String strEncFile;//Base64編碼
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    strEncFile = new String(Base64.getEncoder().encode(imagedata));
                } else {
                    strEncFile = new String(android.util.Base64.encode(imagedata, android.util.Base64.DEFAULT));
                }
                Log.e("base64_mimeType", strEncFile);//base64內容

                decodeBase64(strEncFile);//還原圖片並顯示

                InputStream is = new ByteArrayInputStream(imagedata);//二次使用InputStream

                String mimeType = URLConnection.guessContentTypeFromStream(is);//判斷mimeType
                Log.e(TAG, "mimeType." + mimeType);

                StringBuffer base64_mimeType = new StringBuffer("data:").append(mimeType).append(";base64,").append(strEncFile);//拼接成所要的base64_mimeType

                Log.e("base64_mimeType", base64_mimeType.toString());
            } catch (Exception ex) {
                Log.e(TAG, "failed." + ex.getMessage());
            }
        }
    }


    /*從inputstream獲得數據*/
    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    /*把base64還原成bitmap,並顯示*/
    public void decodeBase64(String encode64) {
        byte[] decode = android.util.Base64.decode(encode64, android.util.Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        iv.setImageBitmap(bitmap);
    }
}
