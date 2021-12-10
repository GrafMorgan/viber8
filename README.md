1.Создаём новый проект

2.Для работы с форматом json добавляем пакет com.google.code.gson в файл guild.gradle
~~~
dependencies {

    implementation 'com.google.code.gson:gson:2.8.8'
    
    implementation 'androidx.appcompat:appcompat:1.3.1'
    implementation 'com.google.android.material:material:1.4.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.1'
    testImplementation 'junit:junit:4.+'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'
}
~~~
3. Добавялем класс Pet
~~~ Java
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
~~~
4. Для работы с json добавляем класс JSONHelper
~~~ Java
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
~~~
5. Затем изменим файл activity_main.xml для взаимодействия с пользователем. Добавим поля EditText, кнопки и ListView для вывода элементов
~~~ XML
 <EditText
        android:id="@+id/nicknameText"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:hint="Введите кличку"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintBottom_toTopOf="@id/breedText"
        app:layout_constraintTop_toTopOf="parent" />
    <EditText
        android:id="@+id/breedText"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:hint="Введите породу"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintBottom_toTopOf="@id/ageText"
        app:layout_constraintTop_toTopOf="@id/nicknameText" />
    <EditText
        android:id="@+id/ageText"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:hint="Введите возраст"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintBottom_toTopOf="@id/addButton"
        app:layout_constraintTop_toBottomOf="@id/breedText" />
    <Button
        android:id="@+id/addButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Добавить"
        android:onClick="addPet"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintBottom_toTopOf="@id/saveButton"
        app:layout_constraintTop_toBottomOf="@id/ageText" />
    <Button
        android:id="@+id/saveButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Сохранить"
        android:onClick="save"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toLeftOf="@id/openButton"
        app:layout_constraintBottom_toTopOf="@id/list"
        app:layout_constraintTop_toBottomOf="@id/addButton"/>
    <Button
        android:id="@+id/openButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Открыть"
        android:onClick="open"
        app:layout_constraintLeft_toRightOf="@id/saveButton"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintBottom_toTopOf="@id/list"
        app:layout_constraintTop_toBottomOf="@id/addButton"/>
    <ListView
        android:id="@+id/list"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toBottomOf="@id/openButton" />
~~~
6.Пропишем код в классе MainActivity. Добавим обработчики кнопок
~~~ Java
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
~~~
7. Проверяеем

