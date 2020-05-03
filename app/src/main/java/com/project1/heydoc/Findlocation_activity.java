package com.project1.heydoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.LocaleData;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class Findlocation_activity extends AppCompatActivity implements OnMapReadyCallback, PlacesListener {

    View mLayout;

    EditText typeLocation;
    Button search;
    GoogleMap map;
    Marker currentMarker = null;

    LocationRequest locationRequest;
//    int UPDATE_INTERVAL_MS = 1000;          //위치 업데이트 주기.. 1초
//    int FASTEST_UPDATE_INTERVAL_MS = 500;   //위치 업데이트 주기.. 0.5초

    FusedLocationProviderClient fusedLocationProviderClient;

    String [] REQUIRED_PERMISSION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    int PERMISSION_REQUEST_CODE = 100;      //퍼미션을 요청하는 코드.. 이 값으로 onRequestPermissionsResult에서 수신된 결과 중에 내가 요청한 퍼미션이 뭔지 구별할 수 있음.

    Location location;
    Location mCurrentLocation;
    LatLng currentPosition;

    final int GPS_ENABLE_REQUEST_CODE = 2001;

    boolean needRequset = false;

    List<Marker> previous_marker = new ArrayList<>();
    List<Address> addresses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findlocation);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);           //이 액티비티 화면을 계속 켜놓게 하는 구문

        mLayout = findViewById(R.id.findlocation_xml);
        search = findViewById(R.id.findclick);
        typeLocation = findViewById(R.id.typelocation);

        locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);                     //위치 요청을 설정함. 우선순위 높음.

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();            //위처 설정 요청 빌더를 선언하고,
        builder.addLocationRequest(locationRequest);                                                //위치 요청 변수에 추가한다.

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);            //현재 위치를 얻기 위한 Api이다.

// According to android source code (FusedLocationProvider), Fused Location is actually a location service which combines GPS location and
// network location to achieve balance between battery consumption and accuracy.


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onStart() {                                          //Todo 본 화면에 들어오면 GPS가 자동으로 수신되고, 메인화면으로 나가면 GPS를 끄도록 하는 기능.
        super.onStart();
        Log.i("태그", "onStart");

        if(checkPermission()){
            Log.i("태그", "onStart : call fusedLocationClient.requestLocationUpdates");
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);       //퍼미션이 있다면, 위치업데이트 요청을 한다.

            if(map!=null){
                map.setMyLocationEnabled(true);             //맵이 널이 아니면, 내 위치를 활성화한다.
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("태그", "onStop");
        if(fusedLocationProviderClient!=null){
            Log.i("태그", "onStart : call stopLocationUpdates");              //로케이션이 업데이트가 계속 되고 있으면, 이를 삭제하므로 정지한다.
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("태그", "위치찾기 액티비티_onMapReady");

        map = googleMap;            //맵을 준비되었을 때 가져오는 매개변수인 구글맵으로 설정한다.

        final Geocoder geocoder = new Geocoder(this);

        setDefaultLocation();


        //퍼미션을 가지고 있는지 확인하는 기능을 위해 선언
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){
            Log.i("태그", "위치찾기 액티비티_fineLocation, CoarseLocation에 대한 permission 있음");
            //이 퍼미션을 가지고 있다면 들어오는 조건문

            startLocationUpdate();              //위치 업데이트 시작!

        }else{                          //퍼미션을 가지고 있지 않음
            Log.i("태그", "위치찾기 액티비티_fineLocation, CoarseLocation에 대한 permission 없음!!");

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSION[0])){
                Snackbar.make(mLayout, "이 기능을 사용하려면 위치 접근 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("태그", "스낵바의 온클릭!");
                        ActivityCompat.requestPermissions(Findlocation_activity.this, REQUIRED_PERMISSION, PERMISSION_REQUEST_CODE);
                    }
                }).show();

            }else {
                //사용자가 퍼미션에 대해 거부한적이 없다면 바로 퍼미션 오청을 보냄도록 함. 이 결과는 onRequestPermissionsResult에서 수신.
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION, PERMISSION_REQUEST_CODE);
            }
        }

        map.getUiSettings().setMyLocationButtonEnabled(true);               //Object를 상속한 UiSettings클래스를 부르고, 맵에 내 위치버튼을 넣는다.
        map.animateCamera(CameraUpdateFactory.zoomTo(15));                  //위치가 업데이트될 때 카메라 이동하는데 애니메이션을 주는 기능
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {                  //맵에 클릭 리스너 기능을 넣음
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i("태그","findlocation_activity>onMapReady>onMapClick");
            }
        });

        search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("태그", "검색 버튼 누름");
//                onSearch(geocoder);
                showPlaceInformation(currentPosition);
            }
        });
    }

    private void onSearch(Geocoder geocoder){
        String findingword = typeLocation.getText().toString();

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(findingword, 3);
            Log.i("태그", "지오코더로 주소 받아오는 try 문");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("태그", addresses.get(0).toString());

    }

    @Override
    public void onPlacesFailure(PlacesException e) {
        Log.i("태그", "온 플레이스 검색 실패");
    }

    @Override
    public void onPlacesStart() {
        Log.i("태그", "온 플레이스 검색 시작");
    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        Log.i("태그", "온 플레이스 검색 성공");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(noman.googleplaces.Place place : places){
                    Log.i("태그", "장소들을 넣는 반복문");
                    if(typeLocation.getText().toString().equals("내과")&&place.getName().contains("내과")){
                        Log.i("태그", "내과 추가");
                        onFiltering(place);
                    }else if(typeLocation.getText().toString().equals("외과")&&place.getName().contains("외과")){
                        Log.i("태그", "외과 추가");
                        onFiltering(place);
                    }else if(typeLocation.getText().toString().equals("산부인과")&&place.getName().contains("산부인과")){
                        Log.i("태그", "산부인과 추가");
                        onFiltering(place);
                    }else if(typeLocation.getText().toString().equals("피부과")&&place.getName().contains("피부과")){
                        Log.i("태그", "피부과 추가");
                        onFiltering(place);
                    }else if(typeLocation.getText().toString().equals("비뇨기과")&&place.getName().contains("비뇨기과")){
                        Log.i("태그", "비뇨기과 추가");
                        onFiltering(place);
                    }else if(typeLocation.getText().toString().equals("안과")&&place.getName().contains("안과")){
                        Log.i("태그", "안과 추가");
                        onFiltering(place);
                    }else if(typeLocation.getText().toString().equals("이비인후과")&&place.getName().contains("이비인후과")){
                        Log.i("태그", "이비인후과 추가");
                        onFiltering(place);
                    }else if(typeLocation.getText().toString().equals("병원")){
                        Log.i("태그", "그냥 병원 추가");
                        onFiltering(place);
                    }else if(typeLocation.getText().toString().equals("약국")){
                        Log.i("태그", "그냥 약국 추가");
                        onFiltering(place);
                    }else if(typeLocation.getText().toString().equals("치과")){
                        Log.i("태그", "그냥 치과 추가");
                        onFiltering(place);
                    }
                }

                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);

            }
        });
    }

    @Override
    public void onPlacesFinished() {
        Log.i("태그", "온 플레이스 검색 끝");
    }

    public void onFiltering(Place place){
        LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
        String markersnippet = getCurrentAddress(latLng);
        Log.i("태그", "온 플레이스 성공에서 경위도"+latLng.latitude+","+latLng.longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(place.getName());
        markerOptions.snippet(markersnippet);
        Log.i("태그", "추가되는 항목 : "+markerOptions.getTitle());
        Marker item = map.addMarker(markerOptions);
        Log.i("태그", "추가될 항목 : "+item.getTitle());
        previous_marker.add(item);
    }

    public void showPlaceInformation(LatLng latLng){
        map.clear();

        Log.i("태그", "showPlaceInformation 메소드");
        if(previous_marker!=null){
            previous_marker.clear();
        }

        if(typeLocation.getText().toString().equals("약국")){
            Log.i("태그", "약국 검색");
            new NRPlaces.Builder().listener(Findlocation_activity.this).key("AIzaSyCt1kMRURgeFiqrxhkEnupPcas6jEt-J90").latlng(latLng.latitude, latLng.longitude).
                    radius(500).type(PlaceType.PHARMACY).build().execute();
        }else if(typeLocation.getText().toString().equals("병원")||typeLocation.getText().toString().equals("내과")||typeLocation.getText().toString().contains("외과")||typeLocation.getText().toString().equals("산부인과")
            ||typeLocation.getText().toString().equals("피부과")||typeLocation.getText().toString().equals("비뇨기과")||typeLocation.getText().toString().equals("안과")||typeLocation.getText().toString().equals("이비인후과")){
            Log.i("태그", "병원 검색");
            new NRPlaces.Builder().listener(Findlocation_activity.this).key("AIzaSyCt1kMRURgeFiqrxhkEnupPcas6jEt-J90").latlng(latLng.latitude, latLng.longitude).
                    radius(500).type(PlaceType.HOSPITAL).build().execute();
            new NRPlaces.Builder().listener(Findlocation_activity.this).key("AIzaSyCt1kMRURgeFiqrxhkEnupPcas6jEt-J90").latlng(latLng.latitude, latLng.longitude).
                    radius(500).type(PlaceType.DOCTOR).build().execute();
        }else if(typeLocation.getText().toString().equals("치과")){
            Log.i("태그", "치과 검색");
            new NRPlaces.Builder().listener(Findlocation_activity.this).key("AIzaSyCt1kMRURgeFiqrxhkEnupPcas6jEt-J90").latlng(latLng.latitude, latLng.longitude).
                    radius(500).type(PlaceType.DENTIST).build().execute();
        }else if(typeLocation.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "찾으시는 내용을 정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
            Log.i("태그", "빈칸 검색시");
        }else {
            Toast.makeText(getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
            Log.i("태그", "검색결과 없을 때");
        }

    }


//    LocationCallback 정의 --
//    Used for receiving notifications from the FusedLocationProviderApi when the device location has changed or can no longer be determined.
//    The methods are called if the LocationCallback has been registered with the location client using the requestLocationUpdates
//    (GoogleApiClient, LocationRequest, LocationCallback, Looper) method.
//    해석 : fusedlocationproviderApi로부터 알람을 받기 위해 사용함. 기기 위치가 바뀌거나, 식별할 수 없을 때.

    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();            //로케이션 리절트를 담을 수 있는 리스트를 선언

            if(locationList.size()>0){                                  //로케이션리스트가 있다면 들어오는 조건문
                location = locationList.get(locationList.size()-1);     //로케이션을 이 리스트로 셋팅

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());          //현재 위치를 위경도로 받음

                String markerTitle = getCurrentAddress(currentPosition);                        //마커 제목 정함
                String markerSnippet = "위도 : "+String.valueOf(location.getLatitude())+" 경도 : "+String.valueOf(location.getLongitude());     //마커 내용 정함

                Log.i("태그", "onLocationResult"+markerSnippet);


                setCurrentLocation(location, markerTitle, markerSnippet);               //현재 위치를 나타내도록 하는 메소드
                mCurrentLocation = location;                                        //현재 위치를 로케이션으로 설정
            }
        }
    };


    private void startLocationUpdate() {

        if(!checkLocationServicesStatus()){
            Log.i("태그", "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            if(hasFineLocationPermission != PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED){
                Log.i("태그", "startLocationUpdates : 퍼미션이 없음");
                return;
            }

            Log.i("태그", "startLocationUpdates : call fusedLocationProviderClient.requestLocationUpdates");

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if(checkPermission()){
                map.setMyLocationEnabled(true);
            }
        }

    }


    public String getCurrentAddress(LatLng latLng){
        Log.i("태그", "getCurrentAddress");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());            //지오코더로 gps를 주소로 반환한다.


        try {

            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude,3);         //지오코더를 통해 경위도를 주소로 바꿔본다
            Log.i("태그", "getCurrentAddress에서 경위도"+latLng.latitude+","+latLng.longitude);
            Log.i("태그", "addresses1 : "+addresses.get(0).toString());

        } catch (IOException e) {
            e.printStackTrace();
            //인터넷 문제일 경우
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_SHORT).show();
            return "잘못된 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException){
            //gps 좌표 문제일 경우
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_SHORT).show();
            return "잘못된 GPS 좌표";
        }

        if(addresses == null && addresses.size() == 0 ){                                        //주소리스트가 비어있다면 실행되는 조건문
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_SHORT).show();
            return "주소 미발견";
        }else {
            Log.i("태그", "addresses2 : "+addresses.get(0).toString());
            Address address = addresses.get(0);
            Log.i("태그", "adress : "+address.getAddressLine(0));
            return address.getAddressLine(0);
        }
    }



    private void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        if(currentMarker !=null){                       //현재 위치가 있다면 삭제해준다
            currentMarker.remove();
        }

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());         //위치로부터 현재 위 경도를 받아서 넣는다.

        MarkerOptions markerOptions = new MarkerOptions();              //마커옵션을 선언하고, 필요한 값들을 넣어준다.
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = map.addMarker(markerOptions);               //현재 마커에 위에서 만든 마커를 넣어준다.

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);       //카메라업데이트 만들고 마커의 위치로 이동시킨다.
        map.moveCamera(cameraUpdate);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("태그", "onRequestPermissionsResult");

        if(requestCode == PERMISSION_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSION.length){
            //요청 코드가 같고, 요청한 퍼미션 개수와 받은 퍼미션 개수가 같으면 들어오는 부분

            boolean check_result = true;
            //모든 퍼미션을 허가했는지 확인하기 위함

            for(int result : grantResults){
                if( result != PackageManager.PERMISSION_GRANTED){
                    check_result = false;
                    break;
                }
            }

            if(check_result){
                //퍼미션이 허용되었다면 위치 업데이트를 시작하도록 함
                startLocationUpdate();
            }else {
                //거부한 퍼미션이 있다면 이 기능을 사용할 수 없다는 것을 안내해주고 돌아가기

                if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSION[0])||
                        ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSION[1])){
                    //사용자가 거부만 선택한 경우에는 이 액티비티를 종료하고 나중에 다시 와서 허용하면 사용할 수 있게 한다.
                    Snackbar.make(mLayout, "이 기능을 사용하기 위한 권한 사용이 거부되었습니다. 다시 실행하여 권한을 허용해주세요.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            }).show();
                }else {
                    //"다시 묻지 않음"을 체크하고 사용자가 거부한 경우에는 설정(앱)에서 퍼미션을 허용해야함.
                    Snackbar.make(mLayout, "이 기능을 사용하기 위한 권한 사용이 거부되었습니다. 설정에서 권한을 허용해주세요.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("확인", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            }).show();
                }
            }
        }

    }

    private boolean checkPermission() {                             //퍼미션이 있는지 확인하는 메소드
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){
            return true;
        }

        return false;
    }

    private void showDialogForLocationServiceSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Findlocation_activity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("이 기능을 사용하려면 위치 서비스가 필요합니다.\n" +
                "위치 서비스를 활성화 하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    private void setDefaultLocation() {                             //기본 위치를 설정하는 메소드
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);        //서울 좌표를 디폴트위치 좌표로 선언

        String markerTitle = "위치정보를 가져올 수 없음";
        String markerSnippet = "위치 사용 승인과 GPS 활성화 여부를 확인하세요";

        if(currentMarker != null){                      //현재 위치를 나타내는 마커가 널이 아니면 삭제(갱신을 위함)
            currentMarker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();          //마커옵션을 하나 생성
        markerOptions.position(DEFAULT_LOCATION);                   //디폴트 위치를 넣음
        markerOptions.title(markerTitle);                       //제목을 지정
        markerOptions.snippet(markerSnippet);                   //내용을 지정
        markerOptions.draggable(true);                              //드래그 가능
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));         //마커 아이콘 설정
        currentMarker = map.addMarker(markerOptions);               //현잳위치마커에 위에서 만든 마커를 추가한다.

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);            //카메라 업데이트를 선언(줌인 15)
        map.moveCamera(cameraUpdate);               //카메라를 이동! 여기서 카메라란 내가 보는 뷰라고 생각하면 됨
    }

    public boolean checkLocationServicesStatus(){                                               //위치서비스를 제공하는지 확인할 수 있는 메소드(불린을 반환)
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                                                //로케이션 매니져가 gps나 network로 제공되는게 가능한지를 리턴
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&&locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case GPS_ENABLE_REQUEST_CODE :
                if(checkLocationServicesStatus()){
                    if(checkLocationServicesStatus()){
                        Log.i("태그", "onActivityResult : GPS 활성화 되어있음");

                        needRequset = true;
                        return;
                    }
                }
                break;
        }
    }


    //    private void chkGpsOn(){            //gps 기능이 활성화 되어있는지 확인하기 위한 메소드
//
//        String gpsEabled = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);         //시큐어셋팅(유저만 조작할 수 있는) 부분에서 위치 제공이 허락되었는지를 알 수 있는 uri주소를 선언
//
//        if(!gpsEabled.contains("gps")){             //주소값이 gps를 가지고 있지 않다면.. 실행
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);        //알림다이얼로그를 이용
//            builder.setMessage("이 서비스를 위해 GPS를 활성화해야합니다.");
//            builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    Intent toSetGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);     //gps 셋팅 화면으로 넘어감.
//                    startActivity(toSetGPS);
//                }
//            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {               // 취소를 누르면 해당 액티비티가 종료.
//                    finish();
//                }
//            }).create().show();
//        }
//    }
}
