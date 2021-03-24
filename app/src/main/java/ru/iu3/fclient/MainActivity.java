package ru.iu3.fclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
//import org.appache.commons.codec.DecoderException;
//import org.appache.commons.codec.binary.Hex;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("mbedcrypto");
        initRng();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        //TextView tv = findViewById(R.id.sample_text);
       // tv.setText(stringFromJNI());

        byte[] key = randomBytes(16);
        byte[] data = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        byte[] encrypted = encrypt(key, data);
        byte[] decrypted = decrypt(key, encrypted);
        String originalData  = new String(data, StandardCharsets.UTF_8);
        String encryptedData = new String(encrypted, StandardCharsets.UTF_8);
        String decryptedData = new String(decrypted, StandardCharsets.UTF_8);

        System.out.println(originalData);
        System.out.println(encryptedData);
        System.out.println(decryptedData);

        String output = new String(
                "Original: "  + originalData  + "\n" +
                        "Encrypted: " + encryptedData + "\n" +
                        "Decrypted: " + decryptedData + "\0"
        );

        System.out.println("Original: "  + originalData);
        System.out.println("Encrypted: " + encryptedData);
        System.out.println("Decrypted: " + decryptedData);
    }
    //вспомогательная функция для преобразования строки в двоичный массив.
    public static byte[] StringToHex(String s)
    {
        byte[] hex;
        try
        {
            hex = Hex.decodeHex(s.toCharArray());
        }
        catch (DecoderException ex)
        {
            hex = null;
        }
        return hex;
    }
    public void onButtonClick(View v){
        //Добавим тест шифрации и дешифрации TDEA. Для наглядности зададим
        //ключ и данные HEX-ASCII строками и воспользуемся процедурами преобразования
        //в массив байтов и обратно из библиотеки commons-codec.
        //Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
        byte [] key = StringToHex("0123456789ABCDEF0123456789ABCDE0");
        byte [] enc = encrypt(key,StringToHex("000000000000000102"));
        byte [] dec = decrypt(key,enc);
        String s = new String (Hex.encodeHex(dec)).toUpperCase();
        //Воспользуемся для вывода сообщений классом Toast
        //Этот код выводит на экран сообщение “Clicked”, которое исчезает через некоторое время.
       //Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        // В обработчике onButtonClick заменим код на вызов PinpadActivity.
       // Intent it = new Intent(this, PinpadActivity.class);
       // startActivity(it);
        //Заменим в MainAcitivity вызов PinpadActivity на
        Intent it = new Intent(this, PinpadActivity.class);
        startActivityForResult(it, 0);
        //Теперь видно откуда берется requestCode.
        // Это значение нужно для того чтобы идентифицировать ответы от разных вызовов Activity.
    }
    //Добавим к классу MainActivity обработчик результата.
    //Эта функция будет вызвана, когда PinpadActivity отправит нам Intent с результатом.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK || data != null) {
                String pin = data.getStringExtra("pin");
                Toast.makeText(this, pin, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int n);

    public static native byte[] encrypt(byte[] key, byte[] data);
    public static native byte[] decrypt(byte[] key, byte[] data);
}