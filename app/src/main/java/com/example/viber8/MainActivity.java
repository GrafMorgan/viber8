package com.example.viber8;

import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Pet {
    private String nickname;
    private String breed;
    private int age;

    Pet(String nickname, String breed, int age){
        this.nickname = nickname;
        this.breed = breed;
        this.age = age;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBreed() {
        return breed;
    }
    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public  String toString(){
        return "Кличка: " + nickname + "\nПорода: " + breed + "\nВозраст: " + age;
    }
}


public class JSONHelper {
    private static final String FILE_NAME = "data.json";

    static boolean exportToJSON(Context context, List<Pet> dataList) {

        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        dataItems.setPets(dataList);
        String jsonString = gson.toJson(dataItems);

        try(FileOutputStream fileOutputStream =
                    context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fileOutputStream.write(jsonString.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    static List<Pet> importFromJSON(Context context) {

        try(FileInputStream fileInputStream = context.openFileInput(FILE_NAME);
            InputStreamReader streamReader = new InputStreamReader(fileInputStream)){

            Gson gson = new Gson();
            DataItems dataItems = gson.fromJson(streamReader, DataItems.class);
            return  dataItems.getPets();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        return null;
    }

    private static class DataItems {
        private List<Pet> pets;

        List<Pet> getPets() {
            return pets;
        }
        void setPets(List<Pet> pets) {
            this.pets = pets;
        }
    }
}


public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<Pet> adapter;
    private EditText nicknameText, breedText, ageText;
    private List<Pet> pets;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nicknameText = findViewById(R.id.nicknameText);
        breedText = findViewById(R.id.breedText);
        ageText = findViewById(R.id.ageText);
        listView = findViewById(R.id.list);
        pets = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pets);
        listView.setAdapter(adapter);
    }

    public void addPet(View view){
        String name = nicknameText.getText().toString();
        String breed = breedText.getText().toString();
        int age = Integer.parseInt(ageText.getText().toString());
        Pet pet = new Pet(name, breed, age);
        pets.add(pet);
        adapter.notifyDataSetChanged();
    }

    public void save(View view){

        boolean result = JSONHelper.exportToJSON(this, pets);
        if(result){
            Toast.makeText(this, "Данные сохранены", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Не удалось сохранить данные", Toast.LENGTH_LONG).show();
        }
    }
    public void open(View view){
        pets = JSONHelper.importFromJSON(this);
        if(pets!=null){
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pets);
            listView.setAdapter(adapter);
            Toast.makeText(this, "Данные восстановлены", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Не удалось открыть данные", Toast.LENGTH_LONG).show();
        }
    }
}