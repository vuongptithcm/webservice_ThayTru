package com.example.adminqlbh.QuanLyHangHoa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.adminqlbh.Api.ApiService;
import com.example.adminqlbh.Api.ApiUtils;
import com.example.adminqlbh.Api.RetrofitClient;
import com.example.adminqlbh.Models.CT_GiaNhap;
import com.example.adminqlbh.Models.CT_GiaNiemYet;
import com.example.adminqlbh.Models.Errors;
import com.example.adminqlbh.Models.GiaHangHoa;
import com.example.adminqlbh.Models.HangHoa;
import com.example.adminqlbh.R;
import com.example.adminqlbh.Util.RealPathUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.example.adminqlbh.QuanLyHangHoa.HangHoaActivity.TAG;
import static com.example.adminqlbh.QuanLyHangHoa.HangHoaActivity.curentPosition;
import static com.example.adminqlbh.QuanLyHangHoa.HangHoaActivity.tempListHH;

public class UpdateHangHoaActivity extends AppCompatActivity {

    private EditText txtInputId, txtInputTenHH, txtInputMota,
            txtInputKL, txtInputGiaNiemYet, txtInputGiaNhap;
    private Button btnAddHH;
    private ImageView imgView_Proct;
    private ApiService apiService;
    private String idHangHoa;
    private Uri imageUriGallery;
    private String imageFileName;
    private String currentImgFileName;
    private Toolbar toolbar;
    private int soluongTon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_insert_activity_hanghoa);
        setControl();
        getProductFromId();
        setEvent();

    }


    public void setControl(){
        imgView_Proct = findViewById(R.id.iv_addImgBTV);
        txtInputId = findViewById(R.id.txtInputId);
        txtInputId.setFocusable(false);
        txtInputId.setEnabled(false);
        txtInputId.setCursorVisible(false);
        txtInputTenHH = findViewById(R.id.txtInputTenHH);
        txtInputMota = findViewById(R.id.txtInputMota);
        txtInputKL = findViewById(R.id.txtInputKL);
        btnAddHH = findViewById(R.id.btnThemHH);
        txtInputGiaNiemYet = findViewById(R.id.txtInputGIA);
        txtInputGiaNhap=findViewById(R.id.txtInputGiaNhap);
        toolbar=findViewById(R.id.toolbar);
        btnAddHH.setText("C???P NH???T");
    }

    //====================Validate======================
    public boolean isValidInput(){
        if(imgView_Proct.getDrawable() == null){
            Toast.makeText(getApplicationContext(), "B???n ch??a ch???n ???nh !!!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(ApiUtils.isEmpty(txtInputId)){
            txtInputId.setError("ID h??ng h??a kh??ng ???????c ????? tr???ng !");
            return false;
        }
        if(ApiUtils.isEmpty(txtInputTenHH)){
            txtInputTenHH.setError("T??n h??ng h??a kh??ng ???????c ????? tr???ng !");
            return false;
        }
        if(ApiUtils.isEmpty(txtInputMota)){
            txtInputMota.setError("M?? t??? h??ng h??a kh??ng ???????c ????? tr???ng !");
            return false;
        }
        if(ApiUtils.isEmpty(txtInputKL)){
            txtInputKL.setError("Kh???i l?????ng h??ng h??a kh??ng ???????c ????? tr???ng v?? ph???i nh??? h??n 10 k?? t??? !");
            return false;
        }

        return true;
    }

    public HangHoa getHangHoa(){
        HangHoa hh = new HangHoa();
        hh.setId(txtInputId.getText().toString());
        hh.setTen(txtInputTenHH.getText().toString());
        hh.setMoTa(txtInputMota.getText().toString());
        if(imageFileName!= null){
            hh.setAnh(imageFileName);
        }
        else hh.setAnh(currentImgFileName);
        hh.setSoluongTon(soluongTon);
        hh.setKhoiLuong(txtInputKL.getText().toString());
        return hh;
    }


    public CT_GiaNiemYet getGiaNIemYet(){
        CT_GiaNiemYet ct_giaNiemYet = new CT_GiaNiemYet();
        ct_giaNiemYet.setIdHH(idHangHoa); // C?? th??? x???y ra l???i khi lock_flag(h??ng H??a) trong b???ng H??ng h??a
        ct_giaNiemYet.setNgayapdung(LocalDate.now().toString());
        ct_giaNiemYet.setGia(new BigDecimal((txtInputGiaNiemYet.getText().toString())));
        return ct_giaNiemYet;
    }


    public CT_GiaNhap getGiaNhap(){
        CT_GiaNhap ct_giaNhap = new CT_GiaNhap();
        ct_giaNhap.setIdHH(idHangHoa); // C?? th??? x???y ra l???i khi lock_flag(h??ng H??a) trong b???ng H??ng h??a
        ct_giaNhap.setNgayapdung(LocalDate.now().toString());
        ct_giaNhap.setGia(new BigDecimal((txtInputGiaNhap.getText().toString())));
        return ct_giaNhap;
    }

    //===================Update hang hoa======================
    public void upadteHangHoa(HangHoa hh){
        apiService = ApiUtils.getApiService();
        apiService.updateHH(hh).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code() == 400){
                    Toast.makeText( getApplicationContext(),"ID Not found !",
                            Toast.LENGTH_SHORT).show();
                }
                if(response.code() == 500){
                    Toast.makeText( getApplicationContext(),"L???i server ! kh??ng c???p nh???t ????u???c h??ng h??a",
                            Toast.LENGTH_SHORT).show();
                }
                if(response.isSuccessful()){
                    insertGiaNiemYet();
                    insertGiaNhap();
                    Toast.makeText( getApplicationContext(),"C???p nh???t h??ng h??a th??nh c??ng !",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText( getApplicationContext(),t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public void setEvent(){
        imgView_Proct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFileImage();
                requestPermionAndPickImage();
            }
        });
        btnAddHH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidInput()){
                    Log.d("Current position", "onClick c???p nh???t: "+ curentPosition);
                    upadteHangHoa(getHangHoa());
                    Toast.makeText( getApplicationContext(),"C???p nh???t h??ng h??a th??nh c??ng !",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Get product from id
    private void getProductFromId(){
        apiService = ApiUtils.getApiService();
        apiService.getProductById(tempListHH.get(curentPosition).getId()).enqueue(new Callback<HangHoa>() {
            @Override
            public void onResponse(Call<HangHoa> call, Response<HangHoa> response) {
                if(response.isSuccessful()){
                    HangHoa hh = response.body();
                    idHangHoa = hh.getId();
                    soluongTon = hh.getSoluongTon();

                    txtInputId.setText(hh.getId());
                    txtInputTenHH.setText(hh.getTen());
                    txtInputMota.setText(hh.getMoTa());
                    Picasso.get().load(RetrofitClient.baseURLImage + hh.getAnh())
                            .placeholder(R.drawable.no_image_icon)
                            .into(imgView_Proct);
                    currentImgFileName = hh.getAnh();
                    txtInputKL.setText(hh.getKhoiLuong());
                    // Hi???n th??? gi?? s???n ph???m
                    getPriceFromIdhh();
                    getGiaNhapFromIdhh();
                }
                if(response.code() == 400){
                    Log.d("Kh??ng t??m th???y h??ng h??a v???i id = " + tempListHH.get(curentPosition).getId(),
                            "onResponse: ");
                }
                if(response.code() == 500){
                    Toast.makeText(getApplicationContext(),"L???i server : kh??ng th??? l???y h??ng h??a t??? id", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<HangHoa> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Oopps! Something went wrong.", Toast.LENGTH_SHORT).show();
                Log.d("L???i ", "onFailure: " + t.getMessage());
            }
        });
    }

    //================ Get gi?? t??? id h??ng h??a =========================
    public void getPriceFromIdhh(){
        // Format String to vietnamese currency
        Format format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        apiService = ApiUtils.getApiService();
        apiService.getPriceFromIDhh(tempListHH.get(curentPosition).getId()).enqueue(new Callback<GiaHangHoa>() {
            @Override
            public void onResponse(Call<GiaHangHoa> call, Response<GiaHangHoa> response) {
                if(response.isSuccessful()){
                    try {
                        // format Float to currency
                        txtInputGiaNiemYet.setText(response.body().getGia());
                    }catch (NullPointerException e){
                        txtInputGiaNiemYet.setText(0);
                    }
                }
                if(response.code() == 500){
                    Log.d("L???i server ", "getPriceFromIdhh");
                }
            }

            @Override
            public void onFailure(Call<GiaHangHoa> call, Throwable t) {
                Log.d("Fetch data error", "onFailure: " + t.getMessage());
            }
        });
    }

    // Insert gi?? ni??m y???t
    public void insertGiaNiemYet(){
        apiService = ApiUtils.getApiService();
        apiService.createGiaNiemYet(getGiaNIemYet()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Toast.makeText( getApplicationContext(),"Th??m gi?? ng??y " + getGiaNIemYet().getNgayapdung() +
                                                                " th??nh c??ng !",
                            Toast.LENGTH_SHORT).show();
                    // Sau khi server response th??m gi?? ni??m y???t ng??y hi???n t???i th??nh c??ng
                    // C???p nh???t danh s??ch t???m v?? tho??t activity
                }
                else if(response.code() == 400){
                    Log.d("Gi?? ni??m y???t ???? t???n t???i ", "onResponse: Th???c hi???n c???p nh???t gi??");
                    updateGNY();
                }
                else if(response.code() == 500){
                    Toast.makeText(getApplicationContext(),"L???i server : Kh??ng th??m ???????c gi?? ni??m y???t", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("L???i", "onFailure: " + t.getMessage());
            }
        });
    }

    // Update gi?? ni??m y???t
    public void updateGNY(){
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("??ang x??? l??....");
        pd.show();
        apiService = ApiUtils.getApiService();
        apiService.updateGiaNiemYet(getGiaNIemYet()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"C???p nh???t gi?? ng??y " + getGiaNIemYet().getNgayapdung() +
                            " th??nh c??ng !", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                    // Sau khi server response c???p nh???t gi?? ni??m y???t ng??y hi???n t???i th??nh c??ng
                    // C???p nh???t danh s??ch t???m v?? tho??t activity
                    finish();
                }
                if(response.code() == 400){
                    Toast.makeText(getApplicationContext(),"C???p nh???t gi?? ng??y " + getGiaNIemYet().getNgayapdung()+
                            " th???t b???i !" , Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
                if(response.code() == 500){
                    Toast.makeText(getApplicationContext(),"L???i server", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("L???i", "onFailure: " + t.getMessage());
                pd.dismiss();
            }
        });
    }

    public void getGiaNhapFromIdhh(){
        // Format String to vietnamese currency
        Format format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        apiService = ApiUtils.getApiService();
        apiService.getGiaNhapFromIDhh(tempListHH.get(curentPosition).getId()).enqueue(new Callback<GiaHangHoa>() {
            @Override
            public void onResponse(Call<GiaHangHoa> call, Response<GiaHangHoa> response) {
                if(response.isSuccessful()){
                    try {
                        // format Float to currency
                        txtInputGiaNhap.setText(response.body().getGia());
                    }catch (NullPointerException e){
                        txtInputGiaNhap.setText(0);
                    }
                }
                if(response.code() == 500){
                    Log.d("L???i server ", "getPriceFromIdhh");
                }
            }

            @Override
            public void onFailure(Call<GiaHangHoa> call, Throwable t) {
                Log.d("Fetch data error", "onFailure: " + t.getMessage());
            }
        });
    }

    // Insert gi?? ni??m y???t
    public void insertGiaNhap(){
        apiService = ApiUtils.getApiService();
        apiService.createGiaNhap(getGiaNhap()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Toast.makeText( getApplicationContext(),"Th??m gi?? nh???p ng??y " + getGiaNhap().getNgayApDung() +
                                    " th??nh c??ng !",
                            Toast.LENGTH_SHORT).show();
                    // Sau khi server response th??m gi?? ni??m y???t ng??y hi???n t???i th??nh c??ng
                    // C???p nh???t danh s??ch t???m v?? tho??t activity
                    tempListHH.remove(curentPosition);
                    tempListHH.add(curentPosition, getHangHoa());
                    finish();
                }
                else if(response.code() == 400){
                    Log.d("Gi?? nh???p ???? t???n t???i ", "onResponse: Th???c hi???n c???p nh???t gi??");
                    updateGiaNhap();
                }
                else if(response.code() == 500){
                    Toast.makeText(getApplicationContext(),"L???i server : Kh??ng th??m ???????c gi?? nh???p", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("L???i", "onFailure: " + t.getMessage());
            }
        });
    }

    // Update gi?? ni??m y???t
    public void updateGiaNhap(){
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("??ang x??? l??....");
        pd.show();
        apiService = ApiUtils.getApiService();
        apiService.updateGiaNhap(getGiaNhap()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"C???p nh???t gi?? nhap ng??y " + getGiaNhap().getNgayApDung() +
                            " th??nh c??ng !", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                    // Sau khi server response c???p nh???t gi?? ni??m y???t ng??y hi???n t???i th??nh c??ng
                    // C???p nh???t danh s??ch t???m v?? tho??t activity
                    tempListHH.remove(curentPosition);
                    tempListHH.add(curentPosition, getHangHoa());
                    finish();
                }
                if(response.code() == 400){
                    Toast.makeText(getApplicationContext(),"C???p nh???t gi?? nh???p ng??y " + getGiaNhap().getNgayApDung() +
                            " th???t b???i !" , Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
                if(response.code() == 500){
                    Toast.makeText(getApplicationContext(),"L???i server", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("L???i", "onFailure: " + t.getMessage());
                pd.dismiss();
            }
        });
    }


    // request permison
    private void requestPermionAndPickImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            chooseFileImage();
            return;
        }
        // request permison for model M or higher, if not it'll crash app
        int result = ContextCompat.checkSelfPermission(this,
                READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            chooseFileImage();
        } else {
            requestPermissions(new String[]{
                    READ_EXTERNAL_STORAGE}, InsertHangHoaActivity.READ_EXTERNAL_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != InsertHangHoaActivity.READ_EXTERNAL_REQUEST) return;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            chooseFileImage();
        } else {
            Toast.makeText(getApplicationContext(), "permission_denied",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void chooseFileImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, InsertHangHoaActivity.PICK_IMAGE_REQUEST);
    }

    // get and show image hang hoa from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == InsertHangHoaActivity.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null){
            imageUriGallery = data.getData();
            try {
                imgView_Proct.setImageURI(imageUriGallery);
                uploadImageToServer(imageUriGallery);
            }catch (NullPointerException e){
                Toast.makeText(getApplicationContext(),"URI not found", Toast.LENGTH_LONG).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImageToServer(Uri uri) {
        if(uri == null) return;
        apiService = ApiUtils.getApiService();
        // show progress dialog
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading image...");
        pd.show();
        // get real path file image from gallery
        String realPathImg = RealPathUtil.getRealPath(this, uri);
        File file = new File(realPathImg);
        imageFileName = file.getName();
        // Create requestBody from choosen file image
        RequestBody requestBody = RequestBody.create(MediaType.parse(getContentResolver().getType(uri)),file);

        // In retrofit 2 to upload file, we using Multipart
        // Declare MultipartBody.Part
        // hinhanh is KEY that define in server
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("hinhanh", file.getName(), requestBody);

        // Call api upload file
        apiService.uploadImage(filePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Upload image successfully ",Toast.LENGTH_LONG).show();
                    Log.e(TAG, "uploadImageToServer: " + realPathImg);
                }
                else {
                    try {
                        Errors.initListError();
                        Toast.makeText(UpdateHangHoaActivity.this, Errors.listErrors.get(response.code()), Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        Log.e(TAG, "onResponse Upload FILE: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(UpdateHangHoaActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
