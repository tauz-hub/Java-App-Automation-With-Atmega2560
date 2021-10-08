package com.example.appcautomation;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
  private BluetoothAdapter btAdapter = null;
  private BluetoothSocket btSocket = null;
  public static String address = null;
  private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  public boolean activar;
  Handler bluetoothIn;
  final int handlerState = 0;
  Button acenderR1,acenderR2,acenderL1,acenderL2,button,Desligar,desconectar;
  private ConnectedThread MyConexionBT;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    acenderR1 = findViewById(R.id.acenderR1);
    acenderR2 = findViewById(R.id.acenderR2);
    acenderL1 = findViewById(R.id.acenderL1);
    acenderL2 = findViewById(R.id.acenderL2);
    button = findViewById(R.id.button);
    Desligar = findViewById(R.id.Desligar);
    desconectar = findViewById(R.id.Desconectar);
    btAdapter = BluetoothAdapter.getDefaultAdapter();
    verificarBluetooth();

    Set<BluetoothDevice> pairedDeveicesList = btAdapter.getBondedDevices();

    for(BluetoothDevice pairedDevice : pairedDeveicesList){
      if(pairedDevice.getName().equals("HC-05")){

        address = pairedDevice.getAddress();
      }
    }

    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        activar = true;
        onResume();
      }
    });
    Desligar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MyConexionBT.write("offAll");
      }
    });
    acenderR1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MyConexionBT.write("onR1");
      }
    });
    acenderR2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MyConexionBT.write("onR2");
      }
    });
    acenderL1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MyConexionBT.write("onL1");
      }
    });
    acenderL2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MyConexionBT.write("onL2");
      }
    });

    desconectar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try{
          btSocket.close();
        } catch(Exception e){
          e.printStackTrace();
        }
      }
    });
  }
  private BluetoothSocket createBluetoothSocket (BluetoothDevice device) throws IOException{

    return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
  }

  private void verificarBluetooth(){
    if(btAdapter.isEnabled()){
    }else{
      Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(intent,1);
    }
  }
  public void onResume() {
    super.onResume();
    if (activar) {
      BluetoothDevice device = btAdapter.getRemoteDevice(address);

      try {
        btSocket = createBluetoothSocket(device);
      } catch (IOException e) {
        Toast.makeText(getBaseContext(), "A conexão com o Socket falhou", Toast.LENGTH_LONG).show();
      }
      // estabelecer conexão
      try {
        btSocket.connect();
      } catch (IOException e) {
        try {
          btSocket.close();
        } catch (IOException e2) {
        }
      }
      MyConexionBT = new ConnectedThread(btSocket);
      MyConexionBT.start();
    }
  }
  private class ConnectedThread extends Thread {
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ConnectedThread(BluetoothSocket socket) {
      InputStream tmpIn = null;
      OutputStream tmpOut = null;
      try {
        tmpIn = socket.getInputStream();
        tmpOut = socket.getOutputStream();
      } catch (IOException e) {
      }
      mmInStream = tmpIn;
      mmOutStream = tmpOut;
    }

    public void run() {
      byte[] buffer = new byte[256];
      int bytes;

      //Ele permanece no modo de escuta para determinar a entrada de dados
      while (true) {
        try {

          bytes = mmInStream.read(buffer);
          String readMessage = new String(buffer, 0, bytes);
          // Envia os dados pro handler
          bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
        } catch (IOException e) {
          break;
        }
      }
    }
    //Envio de quadros
    public void write(String input) {
      try {
        mmOutStream.write(input.getBytes());
      } catch (IOException e) {
        //se não for possível enviar dados, a conexão é encerrada
        Toast.makeText(getBaseContext(), "A conexão falhou", Toast.LENGTH_LONG).show();
        finish();
      }
    }
  }
}