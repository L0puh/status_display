package status_app.status_app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import android.Manifest; 

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private RadioGroup radioGroupStates;
    private LinearLayout layoutWeatherInput, layoutAIInput, layoutTaskInput, layoutReminderInput, layoutCustomInput;
    private EditText editTextLocation, editTextPrompt, editTextTask, editTextReminderTime, editTextReminderText, editTextCustom;
    private Button buttonSubmit;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mBluetoothSocket;
    private OutputStream mOutputStream;
    private BluetoothDevice mDevice;

    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //TODO: add scanning for device
    private static String ARDUINO_MAC_ADDRESS = "98:DA:50:04:0C:08"; // <-- REPLACE THIS LINE

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 2;

    private enum AppState {
        WEATHER, AI_PROMPT, TASK, REMINDER, CUSTOM
    }
    private AppState currentAppState = AppState.WEATHER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioGroupStates = findViewById(R.id.radioGroupStates);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        layoutWeatherInput = findViewById(R.id.layoutWeatherInput);
        layoutAIInput = findViewById(R.id.layoutAIInput);
        layoutTaskInput = findViewById(R.id.layoutTaskInput);
        layoutReminderInput = findViewById(R.id.layoutReminderInput);
        layoutCustomInput = findViewById(R.id.layoutCustomInput);

        editTextLocation = findViewById(R.id.editTextLocation);
        editTextPrompt = findViewById(R.id.editTextPrompt);
        editTextTask = findViewById(R.id.editTextTask);
        editTextReminderTime = findViewById(R.id.editTextReminderTime);
        editTextReminderText = findViewById(R.id.editTextReminderText);
        editTextCustom = findViewById(R.id.editTextCustom);

        radioGroupStates.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonWeather) {
                switchState(AppState.WEATHER);
            } else if (checkedId == R.id.radioButtonAI) {
                switchState(AppState.AI_PROMPT);
            } else if (checkedId == R.id.radioButtonTask) {
                switchState(AppState.TASK);
            } else if (checkedId == R.id.radioButtonReminder) {
                switchState(AppState.REMINDER);
            } else if (checkedId == R.id.radioButtonCustom) {
                switchState(AppState.CUSTOM);
            }
        });

        buttonSubmit.setOnClickListener(v -> handleSubmit());

        ((RadioButton) findViewById(R.id.radioButtonWeather)).setChecked(true);
        switchState(currentAppState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device!", Toast.LENGTH_LONG).show();
            finish();
        } else {
            checkBluetoothPermissions();
        }
    }

    private void checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { 
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                }, REQUEST_BLUETOOTH_PERMISSIONS);
            } else {
                enableBluetoothAndConnect();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { 
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_BLUETOOTH_PERMISSIONS);
            } else {
                 enableBluetoothAndConnect();
            }
        } else {
            enableBluetoothAndConnect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                enableBluetoothAndConnect();
            } else {
                Toast.makeText(this, "Bluetooth permissions not granted. Cannot connect to Arduino.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void enableBluetoothAndConnect() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToArduinoBluetooth();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Bluetooth enabled. Attempting to connect...", Toast.LENGTH_SHORT).show();
                connectToArduinoBluetooth();
            } else {
                Toast.makeText(this, "Bluetooth not enabled. Cannot connect to Arduino.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void connectToArduinoBluetooth() {
        new Thread(() -> {
            try {
                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    Log.e(TAG, "Bluetooth not available or not enabled.");
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Bluetooth not available or not enabled.", Toast.LENGTH_LONG).show());
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                     Log.e(TAG, "BLUETOOTH_CONNECT permission not granted for getBondedDevices.");
                     runOnUiThread(() -> Toast.makeText(MainActivity.this, "BLUETOOTH_CONNECT permission not granted.", Toast.LENGTH_LONG).show());
                     return;
                }

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                mDevice = null;
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        if (device.getAddress().equals(ARDUINO_MAC_ADDRESS)) {
                            mDevice = device;
                            break;
                        }
                    }
                }

                if (mDevice == null) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Arduino Bluetooth module not found or not paired. Make sure MAC address is correct and device is paired.", Toast.LENGTH_LONG).show());
                    Log.e(TAG, "Arduino Bluetooth module not found or not paired.");
                    return;
                }

                mBluetoothSocket = mDevice.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
                mBluetoothAdapter.cancelDiscovery(); 
                mBluetoothSocket.connect(); 
                mOutputStream = mBluetoothSocket.getOutputStream();

                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Connected to Arduino!", Toast.LENGTH_SHORT).show());
                Log.i(TAG, "Successfully connected to Arduino Bluetooth module.");

            } catch (IOException e) {
                Log.e(TAG, "Error connecting to Arduino Bluetooth: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Failed to connect to Arduino: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    try {
                        if (mBluetoothSocket != null) {
                            mBluetoothSocket.close();
                        }
                    } catch (IOException closeException) {
                        Log.e(TAG, "Could not close the client socket", closeException);
                    }
                });
            }
        }).start();
    }

    private void sendData(String data) {
        if (mOutputStream != null) {
            new Thread(() -> {
                try {
                    mOutputStream.write(data.getBytes());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Sent: " + data, Toast.LENGTH_SHORT).show());
                    Log.d(TAG, "Data sent: " + data);
                } catch (IOException e) {
                    Log.e(TAG, "Error sending data: " + e.getMessage(), e);
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error sending data: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        } else {
            Toast.makeText(this, "Bluetooth not connected!", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Attempted to send data, but Bluetooth not connected.");
        }
    }

    private void switchState(AppState newState) {
        currentAppState = newState;

        layoutWeatherInput.setVisibility(View.GONE);
        layoutAIInput.setVisibility(View.GONE);
        layoutTaskInput.setVisibility(View.GONE);
        layoutReminderInput.setVisibility(View.GONE);
        layoutCustomInput.setVisibility(View.GONE);

        switch (newState) {
            case WEATHER:
                layoutWeatherInput.setVisibility(View.VISIBLE);
                break;
            case AI_PROMPT:
                layoutAIInput.setVisibility(View.VISIBLE);
                break;
            case TASK:
                layoutTaskInput.setVisibility(View.VISIBLE);
                break;
            case REMINDER:
                layoutReminderInput.setVisibility(View.VISIBLE);
                break;
            case CUSTOM:
                layoutCustomInput.setVisibility(View.VISIBLE);
                break;
        }
        Log.d(TAG, "Switched to state: " + newState.name());
    }

    private void handleSubmit() {
        String inputData = ""; 
        boolean inputValid = false; 
        String charToSend = ""; 

        switch (currentAppState) {
            case WEATHER:
                String location = editTextLocation.getText().toString().trim();
                if (!location.isEmpty()) {
                   //TODO: send entire location
                    inputData = "Weather location: " + location;
                    inputValid = true;
                    charToSend = "W"; 
                } else {
                    Toast.makeText(this, "Please enter a location for weather.", Toast.LENGTH_SHORT).show();
                }
                break;
            case AI_PROMPT:
                String prompt = editTextPrompt.getText().toString().trim();
                if (!prompt.isEmpty()) {
                    inputData = "AI Prompt: " + prompt;
                    inputValid = true;
                    charToSend = "A"; 
                } else {
                    Toast.makeText(this, "Please enter a prompt for AI.", Toast.LENGTH_SHORT).show();
                }
                break;
            case TASK:
                String task = editTextTask.getText().toString().trim();
                if (!task.isEmpty()) {
                    inputData = "Task: " + task;
                    inputValid = true;
                    charToSend = "T";
                } else {
                    Toast.makeText(this, "Please enter a task description.", Toast.LENGTH_SHORT).show();
                }
                break;
            case REMINDER:
                String reminderTime = editTextReminderTime.getText().toString().trim();
                String reminderText = editTextReminderText.getText().toString().trim();
                if (!reminderTime.isEmpty() && !reminderText.isEmpty()) {
                    inputData = "Reminder at " + reminderTime + ": " + reminderText;
                    inputValid = true;
                    charToSend = "R"; 
                } else {
                    Toast.makeText(this, "Please enter both reminder time and text.", Toast.LENGTH_SHORT).show();
                }
                break;
            case CUSTOM:
                String customInput = editTextCustom.getText().toString().trim();
                if (!customInput.isEmpty()) {
                    inputData = "Custom Input: " + customInput;
                    inputValid = true;
                    charToSend = 'C';
                } else {
                    Toast.makeText(this, "Please enter some custom input.", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        if (inputValid) {
            Toast.makeText(this, "Submitted: " + inputData, Toast.LENGTH_LONG).show();
            Log.i(TAG, "Submitted: " + inputData);

            if (!charToSend.isEmpty()) {
                sendData(charToSend);
            }
            clearCurrentInput(); 
        }
    }

    private void clearCurrentInput() {
        switch (currentAppState) {
            case WEATHER:
                editTextLocation.setText("");
                break;
            case AI_PROMPT:
                editTextPrompt.setText("");
                break;
            case TASK:
                editTextTask.setText("");
                break;
            case REMINDER:
                editTextReminderTime.setText("");
                editTextReminderText.setText("");
                break;
            case CUSTOM:
                editTextCustom.setText("");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mOutputStream != null) {
                mOutputStream.close();
            }
            if (mBluetoothSocket != null) {
                mBluetoothSocket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing Bluetooth resources: " + e.getMessage(), e);
        }
    }
}
